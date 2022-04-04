import dev.triumphteam.helper.*
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("chatchat.base-conventions")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("me.mattstudios.triumph") version "0.2.8"
}

repositories {
    papi()
    triumphSnapshots()
    // towny
    maven("https://repo.glaremasters.me/repository/towny/")
    // dsrv + dependencies
    maven("https://m2.dv8tion.net/releases")
    maven("https://nexus.scarsz.me/content/groups/public")
}

dependencies {
    implementation(project(":api"))

    implementation(libs.triumph.cmds)
    implementation(libs.adventure.bukkit)
    implementation(libs.adventure.minimessage)
    implementation(libs.configurate)
    implementation(libs.bstats)
    implementation(libs.adventure.configurate)

    compileOnly(libs.spigot)
    compileOnly(libs.papi)
    compileOnly(libs.towny)
    compileOnly(libs.discordsrv)
}

bukkit {
    name = "ChatChat"
    description = "DelucksChat 2.0 or smth like that"
    authors = listOf("HelpChat")
    depend = listOf("PlaceholderAPI")
    softdepend = listOf("Towny", "DiscordSRV")
    apiVersion = "1.13"
    permissions {
        permission("chatchat.format.default") {
            description = "Default chat format"
            default = "true"
        }
        permission("chatchat.format.staff") {
            description = "Staff chat format"
            default = "op"
        }
        permission("chatchat.channel.see.default") {
            description = "See default channel"
            default = "true"
        }
        permission("chatchat.channel.use.default") {
            description = "Use default channel"
            default = "true"
        }
        permission("chatchat.channel.see.staff") {
            description = "See staff channel"
            default = "op"
        }
        permission("chatchat.channel.use.staff") {
            description = "Use staff channel"
            default = "op"
        }
        permission("chatchat.mention") {
            description = "Mention other players"
            default = "true"
        }
        permission("chatchat.mention.everyone") {
            description = "Mention the whole channel"
            default = "op"
        }
    }
}

tasks {
    withType<ShadowJar> {
        minimize()

        listOf("net.kyori",
            "dev.triumphteam",
            "org.spongepowered",
            "io.leangen",
            "org.yaml",
            "org.bstats"
        ).forEach { relocate(it, "at.helpch.chatchat.libs.$it") }

        archiveFileName.set("ChatChat-${project.version}.jar")
    }
}
