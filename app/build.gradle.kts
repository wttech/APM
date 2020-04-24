import com.cognifide.gradle.aem.pkg.tasks.Compose

plugins {
    id("com.cognifide.aem.instance")
    id("com.cognifide.aem.package")
    `maven-publish`
    signing
}

description = "AEM Permission Management :: Application"

aem {
    config {
        packageMetaCommonRoot = "content/src/main/content/META-INF"
    }
    tasks {
        satisfy {
            packages {
                group("default") {
                    dependency("com.cognifide.cq.actions:com.cognifide.cq.actions.api:6.0.2")
                    dependency("com.cognifide.cq.actions:com.cognifide.cq.actions.core:6.0.2")
                    dependency("com.cognifide.cq.actions:com.cognifide.cq.actions.msg.replication:6.0.2")
                }
            }
        }
        compose {
            baseName = "apm"
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            fromProject(":app:bundle:main:core")
            fromProject(":app:bundle:extension:checks")
            fromProject(":app:content")
            fromJar("org.jetbrains.kotlin:kotlin-osgi-bundle:1.3.40")
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
                artifactId = "apm"
                version = project.version
            }
        }
    }
}
