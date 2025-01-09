package com.eachserver.scheduled;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledJobRepository {

    private final List<ScheduledJob> scheduledJobs;
    private final ScheduledJobConfigurationRepository scheduledJobConfigurationRepository;
    private final TaskScheduler taskScheduler;

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void start() {

        for (final ScheduledJob scheduledJob : scheduledJobs) {

            final Optional<ScheduledJobConfiguration> scheduledJobConfigurationOptional =
                    scheduledJobConfigurationRepository.findById(scheduledJob.getId());

            taskScheduler.schedule(
                    new ScheduledJobRunnable(scheduledJob, scheduledJobConfigurationRepository),
                    new PeriodicTrigger(
                            scheduledJobConfigurationOptional
                                    .map(
                                            scheduledJobConfiguration ->
                                                    Duration.of(
                                                            scheduledJobConfiguration.getPeriod(),
                                                            scheduledJobConfiguration
                                                                    .getChronoUnit()))
                                    .orElse(scheduledJob.delay())));
        }
    }
}
