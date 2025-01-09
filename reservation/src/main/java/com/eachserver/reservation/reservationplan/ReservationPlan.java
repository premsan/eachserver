package com.eachserver.reservation.reservationplan;

import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@Table(name = "reservation_reservation_plan")
public class ReservationPlan {

    @Id
    @Column("id")
    private String id;

    @Version
    @Column("version")
    private Long version;

    @Column("resource_id")
    private String resourceId;

    @Column("chrono_unit")
    private ChronoUnit chronoUnit;

    @Column("min_unit")
    private Long minUnit;

    @Column("max_unit")
    private Long maxUnit;

    @Column("zone_id")
    private String zoneId;

    @Column("start_at")
    private Long startAt;

    @Column("end_at")
    private Long endAt;

    @Column("updated_at")
    private Long updatedAt;

    @Column("updated_by")
    private String updatedBy;
}
