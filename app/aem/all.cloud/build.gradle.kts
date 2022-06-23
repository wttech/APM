plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
    signing
}

description = "APM (AEM Permission Management) - AEM All-In-One Package (Cloud)"

evaluationDependsOn(":app:aem:ui.apps.cloud")

apply(from = rootProject.file("app/common.gradle.kts"))
apply(from = rootProject.file("app/aem/common.gradle.kts"))

aem {
    tasks {
        packageCompose {
            archiveBaseName.set("all")
            archiveClassifier.set("cloud")
            nestPackageProject(":app:aem:ui.apps.cloud") {
                dirPath.set("/apps/apm-packages/application/install")
            }
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("apmCrx") {
            groupId = project.group.toString() + ".crx"
            artifactId = "apm-all"
            artifact(tasks["packageCompose"])
            afterEvaluate {
                artifactId = "apm-" + project.name
                version = rootProject.version
            }
        }
    }
}
