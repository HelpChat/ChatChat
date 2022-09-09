dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories.gradlePluginPortal()
}

rootProject.name = "chat-chat"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("api")
include("plugin")
include("test-module")
