import dev.triumphteam.helper.*
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("chatchat.base-conventions")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("me.mattstudios.triumph") version "0.2.8"
}

repositories {
    sonatype()
    paper()
    papi()
}

dependencies {
    implementation(project(":api"))

    implementation(libs.triumph.cmds)
    implementation(libs.adventure.bukkit)
    implementation(libs.adventure.minimessage)
    implementation(libs.configurate)

    compileOnly(libs.spigot)
    compileOnly(libs.papi)
}

bukkit {
    name = "ChatChat"
    description = "DelucksChat 2.0 or smth like that"
    authors = listOf("HelpChat")
    depend = listOf("PlaceholderAPI")
    apiVersion = "1.13"
}

tasks {
    withType<ShadowJar> {
        minimize()
        relocate("net.kyori", "at.helpch.chatchat.libs.adventure")
        archiveFileName.set("ChatChat-${project.version}.jar")
    }
}
