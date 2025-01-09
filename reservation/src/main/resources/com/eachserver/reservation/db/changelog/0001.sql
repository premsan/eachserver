CREATE TABLE "reservation_reservation_resource" (
    "id" CHAR(36) NOT NULL,
    "version" BIGINT NOT NULL,
    "name" VARCHAR(256) NOT NULL,
    "description" VARCHAR(65535) NOT NULL,
    "updated_at" BIGINT NOT NULL,
    "updated_by" CHAR(36) NOT NULL,
    CONSTRAINT "reservation_reservation_resource_pk" PRIMARY KEY ("id")
);