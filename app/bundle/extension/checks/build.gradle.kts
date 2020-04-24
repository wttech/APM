plugins {
    id("com.cognifide.aem.bundle")
    java
    `maven-publish`
    signing
}

description = "AEM Permission Management :: Checks"

aem {
    tasks {
        bundle {
            installPath = "/apps/apm/install"
            javaPackage = "com.cognifide.apm"
            category = "apm"
            displayName = "AEM Permission Management :: Checks"
            license = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            vendor = "Cognifide"
            exportPackage("com.cognifide.apm.action.*")
            attribute("CQ-Security-Management-Actions", "com.cognifide.apm.action")
        }
    }
}

dependencies {
    implementation(project(":app:bundle:main:core"))
}