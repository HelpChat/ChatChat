import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("chatchat.base-conventions")
    id("chatchat.publish-conventions")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    // adventure snapshot repo
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    api(libs.adventure.bukkit)
    api(libs.adventure.minimessage)
    api(libs.adventure.configurate)

    compileOnly(libs.spigot)
}

tasks {
    withType<ShadowJar> {
        listOf(
            "net.kyori",
            "io.leangen",
        ).forEach { relocate(it, "at.helpch.chatchat.libs.$it") }

        archiveFileName.set("ChatChat-API-${project.version}.jar")
    }
}
