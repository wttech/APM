import pl.allegro.tech.build.axion.release.domain.TagNameSerializationConfig
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition

plugins {
    id("pl.allegro.tech.build.axion-release") version "1.10.1"
    id("org.nosphere.apache.rat") version "0.4.0"
    id("com.cognifide.aem.instance")
    id("com.cognifide.aem.package")
    `maven-publish`
    signing
}

defaultTasks = listOf(":aemSatisfy", ":aemDeploy")
description = "AEM Permission Management :: Root"

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
            vaultDefinition {
                version = rootProject.version as String
            }
        }
    }
}

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

tasks.rat {
    // Files that don't require a license header
    excludes.add("**/LICENSE")
    excludes.add(".travis.yml")


    excludes.add("**/clientlibs/externals/ace/js/**")
    excludes.add("**/clientlibs/js/json2.js")

    excludes.add("**/*.cqsm")
    excludes.add("**/*.jar")
    excludes.add("**/*.json")
    excludes.add("**/*.md")
    excludes.add("**/*.tld")
    excludes.add("**/*.txt")
    excludes.add("**/*.vltignore")
    excludes.add("**/*.war")
    excludes.add("**/*.zip")
    excludes.add("**/*.xml")

    // Module
    excludes.add("**/documentation/*")
    excludes.add("**/misc/**")

    // Gradle files
    excludes.add("**/gradle/**")
    excludes.add("**/*.gradle.kts")
    excludes.add("gradlew")
    excludes.add("gradlew.bat")

    // Eclipse files
    excludes.add("**/.project")
    excludes.add("**/.classpath")

    // IntelliJ files
    excludes.add("**/build/**")
    excludes.add("**/.idea/**")
    excludes.add("**/*.iml")
}

apply(from = "gradle/common.gradle.kts")

fun formatVersion(version: String, position: ScmPosition) =
        version + "-" + position.branch.replace("/", "").replace(".", "")