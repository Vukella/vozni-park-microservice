package com.voznipark.reporting.client;

import com.voznipark.reporting.dto.external.TrafficAccidentDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

// Returns Optional.empty() on any HTTP/network failure — caller logs and skips
@Component
@Slf4j
public class FleetServiceClient {

    private final RestClient restClient;

    public FleetServiceClient(@Value("${fleet.service.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /** Synchronous call — fleet-service must be running when a TrafficAccidentReportedEvent arrives. */
    public Optional<TrafficAccidentDetailDTO> getAccidentById(Long accidentId) {
        try {
            TrafficAccidentDetailDTO dto = restClient.get()
                    .uri("/api/traffic-accidents/{id}", accidentId)
                    .retrieve()
                    .body(TrafficAccidentDetailDTO.class);
            return Optional.ofNullable(dto);
        } catch (RestClientException e) {
            log.error("Failed to fetch accident {} from fleet-service: {}", accidentId, e.getMessage());
            return Optional.empty();
        }
    }
}
