const meterForm = document.querySelector("#meter-form");
const readingForm = document.querySelector("#reading-form");
const meterSelect = document.querySelector("#reading-meter");
const readingRows = document.querySelector("#reading-rows");
const meterMessage = document.querySelector("#meter-message");
const readingMessage = document.querySelector("#reading-message");
const recordsMessage = document.querySelector("#records-message");
const traceStatus = document.querySelector("#trace-status");
const healthServices = ["gateway", "discovery", "meter", "reading"];
let traceSequence = 0;
let traceTimer;
const flows = {
    register: {
        steps: [
            ["BROWSER", "trace-browser"], ["UI / NGINX", "trace-ui"], ["GATEWAY", "trace-gateway"],
            ["EUREKA LOOKUP", "trace-discovery"], ["METER SERVICE", "trace-meter"], ["METER DB", "trace-db"]
        ],
        description: "The gateway resolves meter-service through Eureka. The meter service validates the request and saves the meter in its own database."
    },
    record: {
        steps: [
            ["BROWSER", "trace-browser"], ["UI / NGINX", "trace-ui"], ["GATEWAY", "trace-gateway"],
            ["EUREKA LOOKUP", "trace-discovery"], ["READING SERVICE", "trace-reading"],
            ["METER SERVICE CHECK", "trace-meter"], ["READING DB", "trace-db"]
        ],
        description: "The reading service checks the meter over HTTP, then writes to its own database. If the meter service is unavailable, the reading request can fail."
    },
    history: {
        steps: [
            ["BROWSER", "trace-browser"], ["UI / NGINX", "trace-ui"], ["GATEWAY", "trace-gateway"],
            ["EUREKA LOOKUP", "trace-discovery"], ["READING SERVICE", "trace-reading"], ["READING DB", "trace-db"]
        ],
        description: "The gateway discovers reading-service. The reading service queries its own database; it does not call meter-service for history."
    }
};

async function request(path, options, flowName = null) {
    const method = options?.method || "GET";
    const traceId = flowName ? startRequestTrace(flowName, method, path) : null;
    const startedAt = performance.now();

    try {
        const response = await fetch(path, {
            ...options,
            headers: {
                Accept: "application/json",
                ...(options?.body ? { "Content-Type": "application/json" } : {}),
                ...options?.headers
            }
        });

        const responseText = await response.text();
        let body;
        if (responseText) {
            try {
                body = JSON.parse(responseText);
            } catch {
                body = null;
            }
        }

        if (!response.ok) {
            const detail = body?.error || body?.message || responseText || `Request failed (${response.status})`;
            const error = new Error(detail);
            error.status = response.status;
            throw error;
        }

        if (response.status === 204 || !responseText) {
            if (traceId !== null) finishRequestTrace(traceId, method, path, response.status, startedAt, true);
            return null;
        }
        if (body === null) {
            const error = new Error("The service returned an invalid JSON response.");
            error.status = response.status;
            throw error;
        }

        if (traceId !== null) finishRequestTrace(traceId, method, path, response.status, startedAt, true);
        return body;
    } catch (error) {
        if (traceId !== null) {
            finishRequestTrace(traceId, method, path, error.status, startedAt, false);
        }
        throw error;
    }
}

function showMessage(element, text, kind = "") {
    element.textContent = text;
    element.classList.toggle("message-error", kind === "error");
    element.classList.toggle("message-success", kind === "success");
}

function selectFlow(name) {
    const flow = flows[name];
    if (!flow) return;

    traceSequence += 1;
    clearInterval(traceTimer);
    traceTimer = null;
    const traceLine = document.querySelector("#trace-line");
    traceLine.replaceChildren();
    flow.steps.forEach(([label, className], index) => {
        const step = document.createElement("span");
        step.className = className;
        step.dataset.traceStep = String(index);
        step.textContent = label;
        traceLine.append(step);

        if (index < flow.steps.length - 1) {
            const arrow = document.createElement("i");
            arrow.textContent = "→";
            arrow.setAttribute("aria-hidden", "true");
            traceLine.append(arrow);
        }
    });

    document.querySelector("#trace-description").textContent = flow.description;
    traceLine.dataset.state = "idle";
    delete traceLine.dataset.result;
    traceStatus.textContent = "Select an operation or submit a request to follow its path.";
    document.querySelectorAll("[data-flow]").forEach((button) => {
        button.setAttribute("aria-pressed", String(button.dataset.flow === name));
    });
}

function startRequestTrace(flowName, method, path) {
    selectFlow(flowName);
    const traceId = traceSequence;
    const traceLine = document.querySelector("#trace-line");
    const steps = [...traceLine.querySelectorAll("[data-trace-step]")];
    let activeStep = 0;

    traceLine.dataset.state = "pending";
    traceStatus.textContent = `IN FLIGHT · ${method} ${path}`;
    steps[activeStep]?.classList.add("is-active");
    traceTimer = setInterval(() => {
        if (traceId !== traceSequence) return;
        steps[activeStep]?.classList.remove("is-active");
        steps[activeStep]?.classList.add("is-complete");
        activeStep += 1;
        if (activeStep < steps.length) {
            steps[activeStep].classList.add("is-active");
        } else {
            clearInterval(traceTimer);
            traceTimer = null;
        }
    }, 350);

    return traceId;
}

function finishRequestTrace(traceId, method, path, status, startedAt, succeeded) {
    if (traceId !== traceSequence) return;

    clearInterval(traceTimer);
    traceTimer = null;
    const traceLine = document.querySelector("#trace-line");
    traceLine.querySelectorAll("[data-trace-step]").forEach((step) => {
        step.classList.remove("is-active");
        if (succeeded) step.classList.add("is-complete");
    });
    traceLine.dataset.state = "complete";
    traceLine.dataset.result = succeeded ? "success" : "error";

    const duration = Math.round(performance.now() - startedAt);
    const outcome = status ? `HTTP ${status}` : "NO RESPONSE";
    traceStatus.textContent = `${succeeded ? "COMPLETE" : "FAILED"} · ${outcome} · ${method} ${path} · ${duration} ms`;
}

async function refreshServiceHealth() {
    await Promise.all(healthServices.map(async (service) => {
        const chip = document.querySelector(`[data-service="${service}"]`);
        const label = document.querySelector(`#health-${service}`);
        chip.dataset.status = "checking";
        label.textContent = "CHECKING";

        const controller = new AbortController();
        const timeout = setTimeout(() => controller.abort(), 4000);
        try {
            const response = await fetch(`/ops/health/${service}`, {
                headers: { Accept: "application/json" },
                signal: controller.signal
            });
            if (response.status === 502 || response.status === 504) {
                chip.dataset.status = "unreachable";
                label.textContent = "UNREACHABLE";
                return;
            }
            let health;
            try {
                health = await response.json();
            } catch {
                chip.dataset.status = "invalid";
                label.textContent = "INVALID RESPONSE";
                return;
            }
            const isUp = response.ok && health?.status === "UP";
            chip.dataset.status = isUp ? "up" : "down";
            label.textContent = isUp ? "UP" : "DOWN";
        } catch {
            chip.dataset.status = "unreachable";
            label.textContent = "UNREACHABLE";
        } finally {
            clearTimeout(timeout);
        }
    }));
}

function appendCell(row, value, className = "") {
    const cell = document.createElement("td");
    cell.textContent = value;
    if (className) cell.className = className;
    row.append(cell);
}

function renderMeters(meters) {
    const selectedMeter = meterSelect.value;
    meterSelect.replaceChildren();

    if (!meters.length) {
        const option = document.createElement("option");
        option.value = "";
        option.textContent = "Register a meter first";
        meterSelect.append(option);
        return;
    }

    for (const meter of meters) {
        const option = document.createElement("option");
        option.value = meter.meterId;
        option.textContent = `${meter.meterId} · ${meter.status}`;
        meterSelect.append(option);
    }

    if (meters.some((meter) => meter.meterId === selectedMeter)) {
        meterSelect.value = selectedMeter;
    }
}

function renderReadings(readings) {
    readingRows.replaceChildren();

    if (!readings.length) {
        const row = document.createElement("tr");
        const cell = document.createElement("td");
        cell.colSpan = 4;
        cell.className = "empty-state";
        cell.textContent = "No readings yet for this meter.";
        row.append(cell);
        readingRows.append(row);
        return;
    }

    for (const reading of readings) {
        const row = document.createElement("tr");
        appendCell(row, reading.meterId);
        appendCell(row, `${Number(reading.value).toFixed(2)} kWh`, "value-cell");
        appendCell(row, new Date(reading.recordedAt).toLocaleString());
        appendCell(row, String(reading.id), "id-cell");
        readingRows.append(row);
    }
}

async function loadMeters() {
    const meters = await request("/api/meters");
    renderMeters(meters);
    return meters;
}

async function loadReadings(flowName = null) {
    const meterId = meterSelect.value;
    if (!meterId) {
        renderReadings([]);
        return;
    }
    const readings = await request(`/api/readings/meter/${encodeURIComponent(meterId)}`, undefined, flowName);
    renderReadings(readings);
    showMessage(recordsMessage, `${readings.length} reading${readings.length === 1 ? "" : "s"} for ${meterId}.`);
}

meterForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    selectFlow("register");
    const submitButton = meterForm.querySelector("button[type=submit]");
    submitButton.disabled = true;
    showMessage(meterMessage, "Registering meter…");

    try {
        const meter = await request("/api/meters", {
            method: "POST",
            body: JSON.stringify({
                meterId: meterForm.elements.meterId.value.trim(),
                status: meterForm.elements.status.value
            })
        }, "register");
        await loadMeters();
        meterSelect.value = meter.meterId;
        showMessage(meterMessage, `${meter.meterId} registered successfully.`, "success");
    } catch (error) {
        showMessage(meterMessage, error.message, "error");
    } finally {
        submitButton.disabled = false;
    }
});

readingForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    selectFlow("record");
    if (!meterSelect.value) {
        showMessage(readingMessage, "Register a meter before submitting a reading.", "error");
        return;
    }

    const submitButton = readingForm.querySelector("button[type=submit]");
    submitButton.disabled = true;
    showMessage(readingMessage, "Checking meter and saving reading…");

    try {
        const reading = await request("/api/readings", {
            method: "POST",
            body: JSON.stringify({
                meterId: meterSelect.value,
                value: Number(readingForm.elements.value.value)
            })
        }, "record");
        showMessage(readingMessage, `Reading ${reading.id} saved for ${reading.meterId}.`, "success");
        await loadReadings();
    } catch (error) {
        showMessage(readingMessage, error.message, "error");
    } finally {
        submitButton.disabled = false;
    }
});

meterSelect.addEventListener("change", () => {
    loadReadings("history").catch((error) => showMessage(recordsMessage, error.message, "error"));
});

document.querySelector("#refresh-readings").addEventListener("click", async () => {
    selectFlow("history");
    try {
        const meters = await loadMeters();
        if (!meters.length) {
            showMessage(recordsMessage, "Register a meter to start recording readings.");
            return;
        }
        await loadReadings("history");
    } catch (error) {
        showMessage(recordsMessage, error.message, "error");
    }
});

document.querySelectorAll("[data-flow]").forEach((button) => {
    button.addEventListener("click", () => selectFlow(button.dataset.flow));
});

document.querySelector("#refresh-status").addEventListener("click", refreshServiceHealth);

document.querySelector("#docs-link").addEventListener("click", (event) => {
    event.preventDefault();
    document.querySelector("#how-it-works").scrollIntoView({ behavior: "smooth" });
});

async function loadInitialData() {
    for (let attempt = 0; attempt < 4; attempt += 1) {
        try {
            await loadMeters();
            await loadReadings();
            return;
        } catch (error) {
            showMessage(meterMessage, error.message, "error");
            showMessage(recordsMessage, "Waiting for the API services to become available…", "error");
            if (attempt < 3) {
                await new Promise((resolve) => setTimeout(resolve, (attempt + 1) * 1000));
            } else {
                showMessage(recordsMessage, "The API is not ready yet. Check that the services are running, then refresh.", "error");
            }
        }
    }
}

selectFlow("record");
refreshServiceHealth();
setInterval(refreshServiceHealth, 15000);
loadInitialData();
