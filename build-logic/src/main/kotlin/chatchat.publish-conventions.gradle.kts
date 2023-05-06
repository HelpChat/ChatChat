plugins {
    `java-library`
    `maven-publish`
}

publishing {
    repositories {
        maven {
            name = "helpchat"
            url = uri("https://repo.helpch.at/snapshots")
            credentials {
                username = System.getenv("REPO_USER")
                password = System.getenv("REPO_PASS")
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("chat-chat")
                description.set("TODO") // Add description
                url.set("https://github.com/HelpChat/ChatChat/")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }

                developers {
                    // add devs
                }

                // Change later
                scm {
                    connection.set("scm:git:git://github.com/HelpChat/ChatChat.git")
                    developerConnection.set("scm:git:ssh://github.com:HelpChat/ChatChat.git")
                    url.set("https://github.com/HelpChat/ChatChat")
                }
            }
        }
    }
}
