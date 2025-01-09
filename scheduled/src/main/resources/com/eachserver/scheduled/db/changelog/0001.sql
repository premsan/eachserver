CREATE TABLE "scheduled_scheduled_job_configuration" (
    "id" CHAR(36) NOT NULL,
    "version" INT NOT NULL,
    "chrono_unit" VARCHAR(256) NOT NULL,
    "period" BIGINT NOT NULL,
    "attributes" VARCHAR(65535) NOT NULL,
    "updated_at" BIGINT NOT NULL,
    "updated_by" CHAR(36) NOT NULL,
    CONSTRAINT "scheduled_scheduled_job_configuration_pk" PRIMARY KEY ("id")
);