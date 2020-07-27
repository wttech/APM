import com.cognifide.gradle.aem.bundle.tasks.bundle
import org.gradle.jvm.tasks.Jar

plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package")
    java
    `maven-publish`
    signing
}

description = "AEM Permission Management :: Actions - Checks"

apply(from = rootProject.file("app/common.gradle.kts"))
apply(from = rootProject.file("app/aem/common.gradle.kts"))

aem {
    tasks {
        packageCompose {
            installBundleProject(":app:aem:actions.checks")
            vaultDefinition {
                val currentVersion = rootProject.version as String
                version.set(currentVersion)
                description.set("APM Extension - a set of 'check' actions, which verify configuration of permissions.")
                property("dependencies", "com.cognifide.apm:apm:" + currentVersion.substringBefore("-SNAPSHOT"))
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
        }
    }
}