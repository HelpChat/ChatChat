import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("chatchat.base-conventions")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    api(libs.adventure.bukkit)
    api(libs.adventure.minimessage)
    api(libs.adventure.configurate)

    compileOnly(libs.spigot)
}

publishing {

}

tasks {
    withType<ShadowJar> {
        listOf("net.kyori",
            "io.leangen",
        ).forEach { relocate(it, "at.helpch.chatchat.libs.$it") }

        archiveFileName.set("ChatChat-API-${project.version}.jar")
    }
}

publishing {
    repositories {
        maven {
            name = "helpchat"
            url = uri("https://repo.helpch.at/snapshots")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
