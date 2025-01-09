plugins {
    java
    alias(libs.plugins.org.springframework.boot)
    alias(libs.plugins.io.spring.dependency.management)
    alias(libs.plugins.com.diffplug.spotless)
}

group = "com.eachserver"

val artifactVersion: String by rootProject.extra
version = artifactVersion

val javaToolChainVersion: Int by rootProject.extra
java.toolchain.languageVersion =  JavaLanguageVersion.of(javaToolChainVersion)

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":application"))
    implementation(project(":db"))
    implementation(project(":security"))
    implementation(project(":ui"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    runtimeOnly("com.h2database:h2")

    compileOnly("org.projectlombok:lombok")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    annotationProcessor("org.projectlombok:lombok")


    testImplementation(platform(libs.org.junit.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    format("html") {
        val htmlTabWidth: Int by rootProject.extra
        prettier().config(mapOf("tabWidth" to htmlTabWidth))

        target("src/**/templates/**/*.html")
    }

    java {
        val googleJavaFormatVersion: String by rootProject.extra

        googleJavaFormat(googleJavaFormatVersion).aosp().reflowLongStrings().skipJavadocFormatting()
        formatAnnotations()
    }
}