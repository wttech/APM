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
            val currentVersion = rootProject.version as String
            archiveFileName.set("apm-$currentVersion")
            installBundle("org.jetbrains.kotlin:kotlin-osgi-bundle:1.3.72")
            installBundleProject(":app:aem:api")
            installBundleProject(":app:aem:core")
            installBundleProject(":app:aem:actions.main")
            vaultDefinition {
                name.set("apm")
                version.set(currentVersion)
                description.set("APM (AEM Permission Management) is an AEM based tool focused on streamlining the permission configuration. It provides a rich UX console tailored for administrators. They can write human readable scripts that handle user/group creation/deletion and permissions application, both in bulk. Through it's flexible grammar, exposed API, and high extensibility it vastly improves permission-based implementations.")
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
