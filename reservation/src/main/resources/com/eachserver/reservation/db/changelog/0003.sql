CREATE TABLE "reservation_reservation" (
    "id" CHAR(36) NOT NULL,
    "version" BIGINT NOT NULL,
    "plan_id" CHAR(36) NOT NULL,
    "name" VARCHAR(256) NOT NULL,
    "description" VARCHAR(65535) NOT NULL,
    "start_at" BIGINT NOT NULL,
    "end_at" BIGINT NOT NULL,
    "updated_at" BIGINT NOT NULL,
    "updated_by" CHAR(36) NOT NULL,
    CONSTRAINT "reservation_reservation_pk" PRIMARY KEY ("id")
);

CREATE INDEX "reservation_reservation_start_at_idx" ON "reservation_reservation" ("start_at");
CREATE INDEX "reservation_reservation_end_at_idx" ON "reservation_reservation" ("end_at");