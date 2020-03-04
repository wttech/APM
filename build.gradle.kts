import com.cognifide.gradle.aem.pkg.tasks.Compose
import pl.allegro.tech.build.axion.release.domain.TagNameSerializationConfig
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition

plugins {
    id("pl.allegro.tech.build.axion-release") version "1.10.1"
    id("com.cognifide.aem.instance")
    id("com.cognifide.aem.package")
    `maven-publish`
    signing
}

defaultTasks = listOf(":aemSatisfy", ":aemDeploy")
description = "AEM Permission Management :: Root"

scmVersion {
    useHighestVersion = true
    ignoreUncommittedChanges = false
    tag(closureOf<TagNameSerializationConfig> {
        prefix = "apm"
    })
}

project.version = scmVersion.version

allprojects {
    group = "com.cognifide.aem"
}

aem {
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
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            fromProject("bundle")
            fromProject("content")
            fromJar("org.jetbrains.kotlin:kotlin-osgi-bundle:1.3.40")
            vaultDefinition {
                version = scmVersion.version as String
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

apply(from = "gradle/common.gradle.kts")

fun formatVersion(version: String, position: ScmPosition) =
        version + "-" + position.branch.replace("/", "").replace(".", "")