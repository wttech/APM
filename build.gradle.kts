plugins {
    id("com.cognifide.aem.package")
}

description = "AEM Permission Management :: Root"

allprojects {
    version = "4.0.0-SNAPSHOT"
    group = "com.cognifide.cq"
}

aem {
    tasks {
        compose {
            fromJar("com.cognifide.cq.actions:com.cognifide.cq.actions.api:6.0.2")
            fromJar("com.cognifide.cq.actions:com.cognifide.cq.actions.core:6.0.2")
            fromJar("com.cognifide.cq.actions:com.cognifide.cq.actions.msg.replication:6.0.2")
            fromProject(":bundle")
            fromProject(":content")
        }
    }
}

apply(from = "gradle/common.gradle.kts")