CREATE TABLE "security_web_key" (
    "id" CHAR(36) NOT NULL,
    "version" BIGINT NOT NULL,
    "key_id" VARCHAR(36) NOT NULL,
    "key_json" VARCHAR(65535) NOT NULL,
    "created_at" BIGINT NOT NULL,
    CONSTRAINT "security_web_key_pk" PRIMARY KEY ("id")
);

CREATE INDEX "security_web_key_key_id_idx" ON "security_web_key" ("key_id");