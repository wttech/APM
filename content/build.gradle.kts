import com.cognifide.gradle.aem.pkg.tasks.Compose

plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
    signing
}

description = "AEM Permission Management :: Package"

aem {
    tasks {
        compose {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            fromProject(":bundle")
            fromProject(":content")
            vaultDefinition {
                version = rootProject.version as String
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("apm") {
            val apmContent by tasks.named(Compose.NAME, Compose::class)
            artifact(apmContent)
            afterEvaluate {
                artifactId = "apm-content"
                version = rootProject.version
            }
        }
    }
}
