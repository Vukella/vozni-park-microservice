package com.voznipark.fleet.service;

import com.voznipark.fleet.entity.ResourceReservation;
import com.voznipark.fleet.repository.ResourceReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ResourceReservationRepository reservationRepository;
    private final StatusCalculationService statusCalculationService;

    /**
     * Releases all active reservations for a completed travel order.
     * If nothing is active, logs and returns.
     */
    @Transactional
    public void releaseForTravelOrder(Long travelOrderId) {
        List<ResourceReservation> active =
                reservationRepository.findByTravelOrderIdAndReleasedAtIsNull(travelOrderId);

        if (active.isEmpty()) {
            log.info("Reservation: No active reservations for travel order {} — skipping", travelOrderId);
            return;
        }

        Instant now = Instant.now();
        active.forEach(r -> {
            r.setReleasedAt(now);
            r.setReleaseReason("COMPLETED");
            reservationRepository.save(r);

            if ("VEHICLE".equals(r.getResourceType())) {
                statusCalculationService.recalculateAndSaveVehicleStatus(r.getResourceId());
            } else if ("DRIVER".equals(r.getResourceType())) {
                statusCalculationService.recalculateAndSaveDriverStatus(r.getResourceId());
            }
        });

        log.info("Reservation: Released {} reservation(s) for travel order {} with reason COMPLETED",
                active.size(), travelOrderId);
    }
}