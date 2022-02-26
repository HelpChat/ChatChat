dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories.gradlePluginPortal()
}

rootProject.name = "chat-chat"

include("api")
include("plugin")

enableFeaturePreview("VERSION_CATALOGS")
