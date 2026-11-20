# WebViewInitializer

Exported from CodeAssist as a Gradle project. The build files here were generated from
the project model, so they are a faithful starting point rather than a build that was
ever run: open it in Android Studio (or run `gradle build`) and expect to adjust it.

## What it was generated with

- Android Gradle plugin 8.13.0
- Kotlin 2.4.0
- Gradle 8.13, through the wrapper included here (`./gradlew build`; on Linux or
  macOS run `chmod +x gradlew` first, since a zip keeps no executable bit)

Kotlin is pinned to the version CodeAssist compiled this project with, so the sources
build the same way there. It is newer than the D8/R8 this AGP bundles, which warns
about Kotlin metadata it cannot rewrite; raising the AGP version clears that.

## Modules

- `:lib` (lib): Android library

## Publishing

`:lib` has a Maven publication:
`./gradlew publishToMavenLocal` installs it, and any Maven repository can be added to the
`publishing` block of the module's build script.

To serve it from JitPack, push this folder as the ROOT of a GitHub repository (with
`settings.gradle.kts`, `gradlew` and `jitpack.yml` at the top level), create a release or
tag, and look the repository up on https://jitpack.io. Users then add
`maven("https://jitpack.io")` to their repositories and depend on
`com.github.<user>:<repository>:<tag>`.

## Notes

Everything in the project model was carried over.

The Android SDK location is not exported: Android Studio writes `local.properties`
on the first sync, or set `sdk.dir` there yourself.
