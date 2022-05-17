plugins {
    id("com.cognifide.aem.package")
    id("com.cognifide.aem.package.sync")
    `maven-publish`
    signing
}

apply(from = rootProject.file("app/common.gradle.kts"))
apply(from = rootProject.file("app/aem/common.gradle.kts"))
