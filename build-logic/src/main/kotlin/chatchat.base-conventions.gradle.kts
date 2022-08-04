plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_17
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
