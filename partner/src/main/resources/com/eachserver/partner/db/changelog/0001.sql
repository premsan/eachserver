CREATE TABLE "partner_partner_api" (
    "id" CHAR(36) NOT NULL,
    "version" BIGINT NOT NULL,
    "host" VARCHAR(256) NOT NULL,
    "path" VARCHAR(256) NOT NULL,
    "inbound" BOOLEAN DEFAULT FALSE NOT NULL,
    "outbound" BOOLEAN DEFAULT FALSE NOT NULL,
    "updated_at" BIGINT NOT NULL,
    "updated_by" CHAR(36) NOT NULL,
    CONSTRAINT "partner_partner_api_pk" PRIMARY KEY ("id")
);

CREATE INDEX "partner_partner_api_path_idx" ON "partner_partner_api" ("path");