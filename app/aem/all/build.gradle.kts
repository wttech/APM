plugins {
    id("com.cognifide.aem.package")
    id("maven-publish")
    `maven-publish`
    signing
}

apply(from = rootProject.file("app/common.gradle.kts"))
apply(from = rootProject.file("app/aem/common.gradle.kts"))

description = "APM (AEM Permission Management) - AEM All-In-One Package"

aem {
    tasks {
        packageCompose {
            nestPackageProject(":app:aem:ui.apps")
            nestPackageProject(":app:aem:ui.content")
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("apmCrx") {
            groupId = project.group.toString() + ".crx"
            artifact(common.publicationArtifact("packageCompose"))
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

