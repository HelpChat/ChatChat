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
    // supervanish
    maven("https://jitpack.io")
    // essentialsx
    maven("https://repo.essentialsx.net/releases/")
}

dependencies {
    implementation(projects.api)

    implementation(libs.triumph.cmds)
    implementation(libs.configurate)
    implementation(libs.bstats)

    compileOnly(libs.spigot)
    compileOnly(libs.papi)
    compileOnly(libs.towny)
    compileOnly(libs.essentials)
    compileOnly(libs.discordsrv)
    compileOnly(libs.supervanish)
}

bukkit {
    name = "ChatChat"
    description = "DelucksChat 2.0 or smth like that"
    authors = listOf("HelpChat")
    depend = listOf("PlaceholderAPI")
    softdepend = listOf("Towny", "DiscordSRV", "SuperVanish", "PremiumVanish")
    loadbefore = listOf("Essentials")
    apiVersion = "1.13"
    permissions {
        permission("chatchat.admin") {
            description = "Execute admin commands"
            default = "op"
        }
        permission("chatchat.socialspy") {
            description = "Spy on private messages"
            default = "op"
        }
        permission("chatchat.test.format") {
            description = "Test formats"
            default = "op"
        }

        permission("chatchat.pm") {
            description = "Send or reply to private messages"
            default = "op"
        }
        permission("chatchat.pm.toggle") {
            description = "Toggle private messages"
            default = "op"
        }

        permission("chatchat.ignore") {
            description = "Permission to use /ignore to toggle ignoring a user"
            default = "op"
        }

        permission("chatchat.ignore.bypass") {
            description = "Bypass being ignored."
            default = "op"
        }

        permission("chatchat.ignorelist") {
            description = "List all the players that you are ignoring."
            default = "op"
        }

        permission("chatchat.utf") {
            description = "Send any char in chat"
            default = "op"
        }
        permission("chatchat.url") {
            description = "Send clickable URLs in chat"
            default = "false"
        }
        permission("chatchat.channel.bypass-radius") {
            description = "Bypass the channel radius"
            default = "op"
        }

        permission("chatchat.mention.personal") {
            description = "Mention other players"
            default = "op"
        }
        permission("chatchat.mention.everyone") {
            description = "Mention the whole channel"
            default = "op"
        }
        permission("chatchat.mention.personal.block") {
            description = "Don't get mentioned by other players"
            default = "op"
        }
        permission("chatchat.mention.everyone.block") {
            description = "Don't get mentioned when there's a channel ping"
            default = "op"
        }
        permission("chatchat.mention.personal.block.override") {
            description = "Mention other players even when they have mentions blocked"
            default = "op"
        }
        permission("chatchat.mention.everyone.block.override") {
            description = "Mention the whole channel even those that have channel mentions blocked"
            default = "op"
        }

        permission("chatchat.tag.item") {
            description = "Use the <item> tag in chat"
            default = "op"
        }
        permission("chatchat.tag.click") {
            description = "Use the <click> tag in chat"
            default = "false"
        }
        permission("chatchat.tag.color") {
            description = "Use the <color> tags in chat"
            default = "op"
        }
        permission("chatchat.tag.font") {
            description = "Use the <font> tag in chat"
            default = "false"
        }
        permission("chatchat.tag.gradient") {
            description = "Use the <gradient> tag in chat"
            default = "op"
        }
        permission("chatchat.tag.hover") {
            description = "Use the <hover> tag in chat"
            default = "op"
        }
        permission("chatchat.tag.insertion") {
            description = "Use the <insert> tag in chat"
            default = "false"
        }
        permission("chatchat.tag.keybind") {
            description = "Use the <key> tag in chat"
            default = "false"
        }
        permission("chatchat.tag.newline") {
            description = "Use the <newline> tags in chat"
            default = "op"
        }
        permission("chatchat.tag.rainbow") {
            description = "Use the <rainbow> tag in chat"
            default = "op"
        }
        permission("chatchat.tag.reset") {
            description = "Use the <reset> tag in chat"
            default = "op"
        }
        permission("chatchat.tag.translatable") {
            description = "Use the <translatable> tags in chat"
            default = "false"
        }
        permission("chatchat.tag.obfuscated") {
            description = "Use the <obfuscated> tags in chat"
            default = "op"
        }
        permission("chatchat.tag.bold") {
            description = "Use the <bold> tags in chat"
            default = "op"
        }
        permission("chatchat.tag.strikethrough") {
            description = "Use the <strikethrough> tags in chat"
            default = "op"
        }
        permission("chatchat.tag.underlined") {
            description = "Use the <underlined> tags in chat"
            default = "op"
        }
        permission("chatchat.tag.italic") {
            description = "Use the <italic> tags in chat"
            default = "op"
        }
    }
}

tasks {
    withType<ShadowJar> {
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
