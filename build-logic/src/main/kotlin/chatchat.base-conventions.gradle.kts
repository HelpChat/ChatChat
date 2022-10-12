plugins {
    `java-library`
}

version = "1.0-SNAPSHOT-${System.getenv("BUILD_NUMBER")}"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }
}
