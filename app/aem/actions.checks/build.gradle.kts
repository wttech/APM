import com.cognifide.gradle.aem.bundle.tasks.bundle
import org.gradle.jvm.tasks.Jar

plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package")
    java
    `maven-publish`
    signing
}

description = "APM Actions Checks"

apply(from = rootProject.file("app/common.gradle.kts"))
apply(from = rootProject.file("app/aem/common.gradle.kts"))

aem {
    tasks {
        packageCompose {
            installBundleProject(":app:aem:actions.checks")
            vaultDefinition {
                val currentVersion = rootProject.version as String
                version.set(currentVersion)
                description.set(project.description)
                property("dependencies", "com.cognifide.apm:apm-ui.apps:" + currentVersion.substringBefore("-SNAPSHOT"))
            }
        }
        jar {
            bundle {
                exportPackage("com.cognifide.apm.checks.actions.*")
                attribute("APM-Actions", "com.cognifide.apm.checks.actions")
            }
        }
    }
}

dependencies {
    implementation(project(":app:aem:api"))
}

tasks {
    getByName("packageDeploy") {
        mustRunAfter(":app:aem:ui.apps:packageDeploy")
    }

    register<Jar>("sourcesJar") {
        from(sourceSets.main.get().allSource)
        archiveClassifier.set("sources")
    }

    register<Jar>("javadocJar") {
        from(javadoc.get().destinationDir)
        archiveClassifier.set("javadoc")
        dependsOn(javadoc)
    }
}

publishing {
    publications {
        register<MavenPublication>("apm") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            afterEvaluate {
                artifactId = "apm-" + project.name
                version = rootProject.version
            }
            pom {
                name.set("APM - " + project.name)
                description.set(project.description)
            }
        }
        register<MavenPublication>("apmCrx") {
            groupId = project.group.toString() + ".crx"
            artifact(tasks["packageCompose"])
            afterEvaluate {
                artifactId = "apm-" + project.name
                version = rootProject.version
            }
            pom {
                name.set("APM - " + project.name)
                description.set(project.description)
            }
        }
    }
}