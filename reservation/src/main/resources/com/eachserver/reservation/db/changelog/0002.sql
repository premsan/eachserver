CREATE TABLE "reservation_reservation_plan" (
    "id" CHAR(36) NOT NULL,
    "version" BIGINT NOT NULL,
    "resource_id" CHAR(36) NOT NULL,
    "chrono_unit" VARCHAR(256) NOT NULL,
    "min_unit" BIGINT NOT NULL,
    "max_unit" BIGINT NOT NULL,
    "zone_id" VARCHAR(256) NOT NULL,
    "start_at" BIGINT NOT NULL,
    "end_at" BIGINT NOT NULL,
    "updated_at" BIGINT NOT NULL,
    "updated_by" CHAR(36) NOT NULL,
    CONSTRAINT "reservation_reservation_plan_pk" PRIMARY KEY ("id")
);