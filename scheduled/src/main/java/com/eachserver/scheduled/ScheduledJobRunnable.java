package com.eachserver.scheduled;

import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScheduledJobRunnable implements Runnable {

    private final ScheduledJob scheduledJob;
    private final ScheduledJobConfigurationRepository scheduledJobConfigurationRepository;

    @Override
    public void run() {

        final Optional<ScheduledJobConfiguration> scheduledJobConfigurationOptional =
                scheduledJobConfigurationRepository.findById(scheduledJob.getId());

        scheduledJob.run(
                scheduledJobConfigurationOptional
                        .map(ScheduledJobConfiguration::getAttributes)
                        .orElse(null));
    }
}
