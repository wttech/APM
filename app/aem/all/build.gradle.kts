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
            installBundleProject(":app:aem:actions.checks")
            mergePackageProject(":app:aem:ui.apps")
            vaultDefinition {
                description.set("APM (AEM Permission Management) is an AEM based tool focused on streamlining the permission configuration. It provides a rich UX console tailored for administrators. They can write human readable scripts that handle user/group creation/deletion and permissions application, both in bulk. Through it's flexible grammar, exposed API, and high extensibility it vastly improves permission-based implementations.")
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