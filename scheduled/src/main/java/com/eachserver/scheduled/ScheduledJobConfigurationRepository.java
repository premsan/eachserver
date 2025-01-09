package com.eachserver.scheduled;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledJobConfigurationRepository
        extends CrudRepository<ScheduledJobConfiguration, String> {}
