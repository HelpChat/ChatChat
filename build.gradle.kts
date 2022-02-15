plugins {
    id("java")
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {

    group = "at.helpch"
    version = "1.0"

    apply {
        plugin("java")
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:21.0.1")
    }

    tasks {
        withType<JavaCompile> {
            sourceCompatibility = "11"
            targetCompatibility = "11"
            options.encoding = "UTF-8"
        }
    }
}
