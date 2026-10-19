package com.energy.reading;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReadingController.class)
class ReadingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReadingRepository readingRepository;

    @MockBean
    private MeterClient meterClient;

    @Test
    void createsReadingForRegisteredMeter() throws Exception {
        when(readingRepository.save(any(Reading.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"meterId":"meter-1","value":12.5}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.meterId").value("meter-1"))
                .andExpect(jsonPath("$.value").value(12.5));
    }

    @Test
    void rejectsReadingsForUnknownMeters() throws Exception {
        doThrow(new MeterNotFoundException("missing-meter"))
                .when(meterClient).requireRegisteredMeter("missing-meter");

        mockMvc.perform(post("/api/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"meterId":"missing-meter","value":12.5}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Meter not found: missing-meter"));
    }

    @Test
    void rejectsNegativeReadingValues() throws Exception {
        mockMvc.perform(post("/api/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"meterId":"meter-1","value":-1}
                                """))
                .andExpect(status().isBadRequest());
    }
}
