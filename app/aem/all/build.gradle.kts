plugins {
    id("com.cognifide.aem.package")
    id("maven-publish")
}

apply(from = rootProject.file("app/common.gradle.kts"))
apply(from = rootProject.file("app/aem/common.gradle.kts"))

description = "AEM Permission Management :: Application"

aem {
    tasks {
        packageCompose {
            installBundleProject(":app:aem:checks")
            mergePackageProject(":app:aem:ui.apps")
            vaultDefinition {
                version.set(rootProject.version as String)
            }
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("apm") {
            artifact(common.publicationArtifact("packageCompose"))
            afterEvaluate {
                artifactId = "apm"
                version = rootProject.version
            }
        }
    }
}