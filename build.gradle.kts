plugins {
    id("java")
    alias(libs.plugins.com.diffplug.spotless)
}

/*
 * Extra properties
 */
val artifactVersion by extra("0.0.1-SNAPSHOT")
val javaToolChainVersion by extra(21)
val googleJavaFormatVersion by extra("1.19.2")
val htmlTabWidth by extra(4)

group = "com.eachserver"
version = artifactVersion

spotless {
    ratchetFrom("origin/main")

    format("misc") {
        target("*.gradle", ".gitattributes", ".gitignore")

        trimTrailingWhitespace()
        indentWithTabs()
        endWithNewline()
    }
}