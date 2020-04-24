plugins {
    id("com.cognifide.aem.package")
    id("com.cognifide.aem.package.sync")
    `maven-publish`
    signing
}

description = "AEM Permission Management :: Package"

apply(from = rootProject.file("app/common.gradle.kts"))
apply(from = rootProject.file("app/aem/common.gradle.kts"))

aem {
    tasks {
        packageCompose {
            installBundle("org.jetbrains.kotlin:kotlin-osgi-bundle:1.3.72")
            installBundleProject(":app:aem:core")
            vaultDefinition {
                version.set(rootProject.version as String)
            }
        }
    }
}
