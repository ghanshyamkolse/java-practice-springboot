package com.energy.reading;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DiscoveryMeterClient implements MeterClient {

    private final RestClient restClient;

    public DiscoveryMeterClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public void requireRegisteredMeter(String meterId) {
        restClient.get()
                .uri("http://meter-service/api/meters/{meterId}", meterId)
                .retrieve()
                .toBodilessEntity();
    }
}
