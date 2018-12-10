pluginManagement {
    repositories {
        jcenter()
        mavenCentral()
        maven(url = "https://jcenter.bintray.com/")
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://dl.bintray.com/cognifide/maven-public")
        maven(url = "https://repo.adobe.com/nexus/content/groups/public/")
        mavenLocal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.cognifide.aem") {
                useModule("com.cognifide.gradle:aem-plugin:5.1.3")
            }
        }
    }
}

rootProject.name = "apm"

include(":bundle")
include(":content")
include(":documentation")