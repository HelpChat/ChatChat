plugins {
    id("chatchat.base-conventions")
    id("maven-publish")
}

dependencies {
    compileOnly(libs.adventure.bukkit)
    compileOnly(libs.spigot)
}

publishing {

}
