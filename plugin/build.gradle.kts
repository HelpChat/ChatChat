import dev.triumphteam.helper.*
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.bukkit.bukkitPluginYaml

plugins {
    id("chatchat.base-conventions")
    id("com.gradleup.shadow") version "9.2.2"
    id("me.mattstudios.triumph") version "0.2.8"
    id("xyz.jpenilla.resource-factory") version "1.2.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

version = "${rootProject.version}-${1}"

fun lib(name: String): File {
    return file("${rootDir.absolutePath}/libs/$name" + if (name.endsWith(".jar")) "" else ".jar")
}

tasks {
    runServer {
        runDirectory(file("run/latest"))
        minecraftVersion("1.21.8")

        pluginJars(
            lib("Authenticator.jar"),
        )

        downloadPlugins {
            modrinth("viaversion", "5.6.0")
            modrinth("viabackwards", "5.6.0")
            modrinth("luckperms", "v5.5.0-bukkit")
            modrinth("worldedit", "3ISh7ADm") // 7.3.17
            modrinth("worldguard", "7.0.14")
            modrinth("pvpmanager", "3.19.3")
            modrinth("decentholograms", "2.9.8")
            modrinth("essentialsx", "2.21.2")
            modrinth("vaultunlocked", "2.9.0")
            modrinth("placeholderapi", "2.11.6")

            pluginJars(
                lib("Authenticator.jar"),
            )
        }

        doFirst {
            val eulaFile = file("run/latest/eula.txt")
            eulaFile.parentFile.mkdirs()
            if (!eulaFile.exists()) {
                eulaFile.writeText("eula=true")
            }
        }
    }
}

repositories {
    papi()
    triumphSnapshots()
    // adventure snapshot repo
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    // towny
    maven("https://repo.glaremasters.me/repository/towny/")
    // dsrv + dependencies
    maven("https://m2.dv8tion.net/releases")
    maven("https://nexus.scarsz.me/content/groups/public")
    // supervanish, griefprevention
    maven("https://jitpack.io")
    // essentialsx
    maven("https://repo.essentialsx.net/releases/")
    maven("https://repo.papermc.io/repository/maven-public/")

    maven {
        name = "tcoded-releases"
        url = uri("https://repo.tcoded.com/releases")
    }

    flatDir {
        name = "libs.dir"
        dirs = setOf(file("libs"))
    }
}

dependencies {
    implementation(projects.chatChatApi)

    implementation(libs.triumph.cmds)
    implementation(libs.adventure.configurate)
    implementation(libs.configurate)
    implementation(libs.bstats)

    compileOnly(libs.paper)
    compileOnly(libs.papi)
    compileOnly(libs.towny)
    compileOnly(libs.essentials) {
        exclude("org.spigotmc")
    }
    compileOnly(libs.discordsrv)
    compileOnly(libs.supervanish)
    compileOnly(libs.griefprevention)

    implementation("com.tcoded:FoliaLib:0.5.1")

    compileOnly(files("${rootDir}/libs/Authenticator.jar"))
}

tasks {
    withType<ShadowJar> {
        listOf(
            "dev.triumphteam",
            "org.spongepowered",
            "io.leangen",
            "org.yaml",
            "org.bstats",
            "com.tcoded"
        ).forEach { relocate(it, "at.helpch.chatchat.libs.$it") }

        archiveFileName.set("ChatChat-${project.version}.jar")
    }
}

val yaml = bukkitPluginYaml {
    main = "at.helpch.chatchat.ChatChatPlugin"
    authors.add("HelpChat")
    name = "ChatChat"
    foliaSupported = true
    apiVersion = "1.21.4"
    description = "DelucksChat 2.0 or smth like that"
    depend = listOf("PlaceholderAPI")

    dependencies {
        softDepend = listOf("Towny", "DiscordSRV", "SuperVanish", "PremiumVanish", "GriefPrevention")
        loadBefore = listOf("Essentials")
    }

    permissions {
        create("chatchat.admin") {
            description = "Execute admin commands"
            default = Permission.Default.OP
        }
        create("chatchat.socialspy") {
            description = "Spy on private messages"
            default = Permission.Default.OP
        }
        create("chatchat.test.format") {
            description = "Test formats"
            default = Permission.Default.OP
        }

        create("chatchat.pm") {
            description = "Send or reply to private messages"
            default = Permission.Default.OP
        }
        create("chatchat.pm.toggle") {
            description = "Toggle private messages"
            default = Permission.Default.OP
        }

        create("chatchat.ignore") {
            description = "Permission to use /ignore to toggle ignoring a user"
            default = Permission.Default.OP
        }

        create("chatchat.ignore.bypass") {
            description = "Bypass being ignored."
            default = Permission.Default.OP
        }

        create("chatchat.ignorelist") {
            description = "List all the players that you are ignoring."
            default = Permission.Default.OP
        }

        create("chatchat.utf") {
            description = "Send any char in chat"
            default = Permission.Default.OP
        }
        create("chatchat.url") {
            description = "Send clickable URLs in chat"
            default = Permission.Default.FALSE
        }
        create("chatchat.channel.bypass-radius") {
            description = "Bypass the channel radius"
            default = Permission.Default.OP
        }

        create("chatchat.mention.personal") {
            description = "Mention other players"
            default = Permission.Default.OP
        }
        create("chatchat.mention.everyone") {
            description = "Mention the whole channel"
            default = Permission.Default.OP
        }
        create("chatchat.mention.personal.block") {
            description = "Don't get mentioned by other players"
            default = Permission.Default.OP
        }
        create("chatchat.mention.everyone.block") {
            description = "Don't get mentioned when there's a channel ping"
            default = Permission.Default.OP
        }
        create("chatchat.mention.personal.block.override") {
            description = "Mention other players even when they have mentions blocked"
            default = Permission.Default.OP
        }
        create("chatchat.mention.everyone.block.override") {
            description = "Mention the whole channel even those that have channel mentions blocked"
            default = Permission.Default.OP
        }

        create("chatchat.tag.item") {
            description = "Use the <item> tag in chat"
            default = Permission.Default.OP
        }
        create("chatchat.tag.click") {
            description = "Use the <click> tag in chat"
            default = Permission.Default.FALSE
        }
        create("chatchat.tag.color") {
            description = "Use the <color> tags in chat"
            default = Permission.Default.OP
        }
        create("chatchat.tag.font") {
            description = "Use the <font> tag in chat"
            default = Permission.Default.FALSE
        }
        create("chatchat.tag.gradient") {
            description = "Use the <gradient> tag in chat"
            default = Permission.Default.OP
        }
        create("chatchat.tag.hover") {
            description = "Use the <hover> tag in chat"
            default = Permission.Default.OP
        }
        create("chatchat.tag.insertion") {
            description = "Use the <insert> tag in chat"
            default = Permission.Default.FALSE
        }
        create("chatchat.tag.keybind") {
            description = "Use the <key> tag in chat"
            default = Permission.Default.FALSE
        }
        create("chatchat.tag.newline") {
            description = "Use the <newline> tags in chat"
            default = Permission.Default.OP
        }
        create("chatchat.tag.rainbow") {
            description = "Use the <rainbow> tag in chat"
            default = Permission.Default.OP
        }
        create("chatchat.tag.reset") {
            description = "Use the <reset> tag in chat"
            default = Permission.Default.OP
        }
        create("chatchat.tag.translatable") {
            description = "Use the <translatable> tags in chat"
            default = Permission.Default.FALSE
        }
        create("chatchat.tag.obfuscated") {
            description = "Use the <obfuscated> tags in chat"
            default = Permission.Default.OP
        }
        create("chatchat.tag.bold") {
            description = "Use the <bold> tags in chat"
            default = Permission.Default.OP
        }
        create("chatchat.tag.strikethrough") {
            description = "Use the <strikethrough> tags in chat"
            default = Permission.Default.OP
        }
        create("chatchat.tag.underlined") {
            description = "Use the <underlined> tags in chat"
            default = Permission.Default.OP
        }
        create("chatchat.tag.italic") {
            description = "Use the <italic> tags in chat"
            default = Permission.Default.OP
        }
    }
}

sourceSets.main {
    resourceFactory {
        factory(yaml.resourceFactory())
    }
}
