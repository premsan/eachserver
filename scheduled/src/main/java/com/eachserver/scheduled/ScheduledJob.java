package com.eachserver.scheduled;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface ScheduledJob {

    default String getId() {

        return getClass().getSimpleName();
    }

    default Duration delay() {

        return Duration.ofNanos(Long.MAX_VALUE);
    }

    default List<String> attributeNames() {

        return Collections.emptyList();
    }

    void run(final Map<String, String> attributes);
}
