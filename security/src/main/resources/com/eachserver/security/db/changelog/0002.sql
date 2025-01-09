CREATE TABLE "security_authority" (
    "id" CHAR(36) NOT NULL,
    "version" INT NOT NULL,
    "name" VARCHAR(256) NOT NULL,
    "updated_at" BIGINT NOT NULL,
    "updated_by" CHAR(36) NOT NULL,
    CONSTRAINT "security_authority_pk" PRIMARY KEY ("id")
);

CREATE UNIQUE INDEX "security_authority_ix1" ON "security_authority" ("name");

CREATE TABLE "security_role" (
    "id" CHAR(36) NOT NULL,
    "version" INT NOT NULL,
    "name" VARCHAR(256) NOT NULL,
    "updated_at" BIGINT NOT NULL,
    "updated_by" CHAR(36) NOT NULL,
    CONSTRAINT "security_role_pk" PRIMARY KEY ("id")
);

CREATE UNIQUE INDEX "security_role_ix1" ON "security_role" ("name");

CREATE TABLE "security_role_authority" (
    "id" CHAR(36) NOT NULL,
    "version" INT NOT NULL,
    "role_id" CHAR(36) NOT NULL,
    "authority_id" CHAR(36) NOT NULL,
    "updated_at" BIGINT NOT NULL,
    "updated_by" CHAR(36) NOT NULL,
    CONSTRAINT "security_role_authority_pk" PRIMARY KEY ("id"),
    CONSTRAINT "security_role_authority_fk1" FOREIGN KEY ("role_id") REFERENCES "security_role" ("id") ON DELETE CASCADE,
    CONSTRAINT "security_role_authority_fk2" FOREIGN KEY ("authority_id") REFERENCES "security_authority" ("id") ON DELETE CASCADE
);

CREATE INDEX "security_role_authority_ix1" ON "security_role_authority" ("role_id");
CREATE INDEX "security_role_authority_ix2" ON "security_role_authority" ("authority_id");
CREATE UNIQUE INDEX "security_role_authority_ix3" ON "security_role_authority" ("role_id", "authority_id");

CREATE TABLE "security_user" (
    "id" CHAR(36) NOT NULL,
    "version" INT NOT NULL,
    "email" VARCHAR(256) NOT NULL,
    "disabled" BOOLEAN DEFAULT FALSE NOT NULL,
    "updated_at" BIGINT NOT NULL,
    "updated_by" CHAR(36) NOT NULL,
    CONSTRAINT "security_user_pk" PRIMARY KEY ("id")
);

CREATE UNIQUE INDEX "security_user_idx1" ON "security_user" ("email");

CREATE TABLE "security_user_role" (
    "id" CHAR(36) NOT NULL,
    "version" INT NOT NULL,
    "user_id" CHAR(36) NOT NULL,
    "role_id" CHAR(36) NOT NULL,
    "updated_at" BIGINT NOT NULL,
    "updated_by" CHAR(36) NOT NULL,
    CONSTRAINT "security_user_role_pk" PRIMARY KEY ("id"),
    CONSTRAINT "security_user_role_fk1" FOREIGN KEY ("user_id") REFERENCES "security_user" ("id") ON DELETE CASCADE,
    CONSTRAINT "security_user_role_fk2" FOREIGN KEY ("role_id") REFERENCES "security_role" ("id") ON DELETE CASCADE
);

CREATE INDEX "security_user_role_idx1" ON "security_user_role" ("role_id");
CREATE UNIQUE INDEX "security_user_role_idx2" ON "security_user_role" ("user_id", "role_id");