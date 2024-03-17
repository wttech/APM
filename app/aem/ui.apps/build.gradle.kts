plugins {
    id("com.cognifide.aem.package")
    id("com.cognifide.aem.package.sync")
    `maven-publish`
    signing
}

description = "APM (AEM Permission Management) is an AEM based, cloud compatible tool focused on streamlining the permission configuration. It provides a rich UX console tailored for administrators. They can write human readable scripts that handle user/group creation/deletion and permissions application, both in bulk. Through it's flexible grammar, exposed API, and high extensibility it vastly improves permission-based implementations."

evaluationDependsOn(":app:aem:ui.apps.base")
evaluationDependsOn(":app:aem:api")
evaluationDependsOn(":app:aem:runmodes")
evaluationDependsOn(":app:aem:core")
evaluationDependsOn(":app:aem:actions.main")
evaluationDependsOn(":app:aem:install")

apply(from = rootProject.file("app/common.gradle.kts"))
apply(from = rootProject.file("app/aem/common.gradle.kts"))

aem {
    tasks {
        packageCompose {
            mergePackageProject(":app:aem:ui.apps.base")
            installBundleProject(":app:aem:api")
            installBundleProject(":app:aem:runmodes")
            installBundleProject(":app:aem:core")
            installBundleProject(":app:aem:actions.main")
            installBundleProject(":app:aem:install")
            vaultDefinition {
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                version.set(rootProject.version as String)
                description.set(project.description)
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
