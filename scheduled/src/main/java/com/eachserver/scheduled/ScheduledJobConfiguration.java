package com.eachserver.scheduled;

import java.time.temporal.ChronoUnit;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scheduled_scheduled_job_configuration")
public class ScheduledJobConfiguration {

    @Id
    @Column("id")
    private String id;

    @Version
    @Column("version")
    private Long version;

    @Column("chrono_unit")
    private ChronoUnit chronoUnit;

    @Column("period")
    private Long period;

    @Column("attributes")
    private Map<String, String> attributes;
}
