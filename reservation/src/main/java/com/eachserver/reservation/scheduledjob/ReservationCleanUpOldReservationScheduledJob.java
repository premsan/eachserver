package com.eachserver.reservation.scheduledjob;

import com.eachserver.reservation.reservation.ReservationRepository;
import com.eachserver.scheduled.ScheduledJob;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationCleanUpOldReservationScheduledJob implements ScheduledJob {

    private final ReservationRepository reservationRepository;

    @Override
    public void run(final Map<String, String> attributes) {

        reservationRepository.deleteAll();
    }
}
