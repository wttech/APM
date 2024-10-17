plugins {
    id("com.cognifide.aem.package")
    id("com.cognifide.aem.package.sync")
    `maven-publish`
    signing
}

description = "APM (AEM Permission Management) is an AEM based, cloud compatible tool focused on streamlining the permission configuration. It provides a rich UX console tailored for administrators. They can write human readable scripts that handle user/group creation/deletion and permissions application, both in bulk. Through it's flexible grammar, exposed API, and high extensibility it vastly improves permission-based implementations."

evaluationDependsOn(":app:aem:ui.apps.base")
evaluationDependsOn(":app:aem:core")

apply(from = rootProject.file("app/common.gradle.kts"))
apply(from = rootProject.file("app/aem/common.gradle.kts"))

aem {
    tasks {
        packageCompose {
            mergePackageProject(":app:aem:ui.apps.base")
            installBundleProject(":app:aem:core")
            vaultDefinition {
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                version.set(rootProject.version as String)
                description.set(project.description)
                acHandling(true)
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
