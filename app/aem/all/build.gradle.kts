plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
    signing
}

description = "APM (AEM Permission Management) - AEM All-In-One Package"

evaluationDependsOn(":app:aem:ui.apps")
evaluationDependsOn(":app:aem:ui.system.user")
evaluationDependsOn(":app:aem:ui.content")

apply(from = rootProject.file("app/common.gradle.kts"))
apply(from = rootProject.file("app/aem/common.gradle.kts"))

aem {
    tasks {
        packageCompose {
            nestPackageProject(":app:aem:ui.apps") {
                dirPath.set("/apps/apm-packages/application/install")
            }
            nestPackageProject(":app:aem:ui.system.user") {
                dirPath.set("/apps/apm-packages/content/install")
            }
            nestPackageProject(":app:aem:ui.content") {
                dirPath.set("/apps/apm-packages/content/install")
            }
        }
    }
}

publishing {
    publications {
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
