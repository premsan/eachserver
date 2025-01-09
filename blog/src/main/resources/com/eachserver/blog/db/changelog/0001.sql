CREATE TABLE "blog_blog" (
    "id" CHAR(36) NOT NULL,
    "version" INT NOT NULL,
    "title" VARCHAR(256) NOT NULL,
    "content" VARCHAR(65535) NOT NULL,
    "updated_at" BIGINT NOT NULL,
    "updated_by" CHAR(36) NOT NULL,
    CONSTRAINT "blog_blog_pk" PRIMARY KEY ("id")
);

