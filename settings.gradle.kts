dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories.gradlePluginPortal()
}

rootProject.name = "chat-chat"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

listOf(
    "api",
    "plugin",
    "test-module",
).forEach(::includeProject)

fun includeProject(name: String) {
    include(name) {
        this.name = "${rootProject.name}-$name"
    }
}

fun include(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}
