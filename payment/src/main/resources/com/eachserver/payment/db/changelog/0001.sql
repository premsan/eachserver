CREATE TABLE "payment_payment" (
    "id" CHAR(36) NOT NULL,
    "version" BIGINT NOT NULL,
    "reference_id" CHAR(36) NOT NULL,
    "currency" VARCHAR(3) NOT NULL,
    "amount" DECIMAL(36, 18) NOT NULL,
    "name" VARCHAR(256) NOT NULL,
    "description" VARCHAR(65535) NOT NULL,
    "updated_at" BIGINT NOT NULL,
    "updated_by" CHAR(36) NOT NULL,
    CONSTRAINT "payment_payment_pk" PRIMARY KEY ("id")
);

CREATE INDEX "payment_payment_reference_id_idx" ON "payment_payment" ("reference_id");