import dev.triumphteam.helper.*
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("me.mattstudios.triumph") version "0.2.2"
}

repositories {

    sonatype()
    paper()
    papi()
}

dependencies {

    implementation(
        triumph("cmd", "1.4.6"), // command framework
        adventure("bukkit", "4.0.1"), // adventure
        "net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT", // mini-message
        "org.spongepowered:configurate-yaml:4.1.2",
    )
    implementation(project(":api"))

    compileOnly(
        spigot("1.18.1"),
        papi("2.10.10"),
    )
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
        relocate("net.kyori", "me.kaliber.libs.adventure")
        archiveFileName.set("ChatChat-${project.version}.jar")
    }

}
