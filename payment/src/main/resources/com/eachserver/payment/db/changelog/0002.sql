CREATE TABLE "payment_payment_attempt" (
    "id" CHAR(36) NOT NULL,
    "version" BIGINT NOT NULL,
    "payment_id" CHAR(36) NOT NULL,
    "gateway_id" VARCHAR(256) NOT NULL,
    "gateway_attempt_id" VARCHAR(256),
    "gateway_attempt_url" VARCHAR(2048),
    "gateway_attempt_attributes" VARCHAR(65535),
    "status" VARCHAR(256),
    "updated_at" BIGINT NOT NULL,
    "updated_by" CHAR(36) NOT NULL,
    CONSTRAINT "payment_payment_attempt_pk" PRIMARY KEY ("id")
);

CREATE INDEX "payment_payment_attempt_payment_id_idx" ON "payment_payment_attempt" ("payment_id");