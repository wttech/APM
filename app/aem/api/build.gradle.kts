import com.cognifide.gradle.aem.bundle.tasks.bundle
import org.gradle.jvm.tasks.Jar

plugins {
    id("com.cognifide.aem.bundle")
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.noarg") version "1.3.70"
    antlr
    groovy
    java
    `maven-publish`
    signing
}

description = "AEM Permission Management :: Core"

apply(from = rootProject.file("app/common.gradle.kts"))
apply(from = rootProject.file("app/aem/common.gradle.kts"))

aem {
    tasks {
        jar {
            bundle {
                exportPackage("com.cognifide.cq.cqsm.*") //TODO repair after extraction of API
//                exportPackage("com.cognifide.cq.cqsm.foundation.actions.*")
//                exportPackage("com.cognifide.cq.cqsm.api.*")
//                exportPackage("com.cognifide.cq.cqsm.core.models.*")
                exportPackage("com.cognifide.apm.tools.*")
                exportPackage("com.cognifide.apm.endpoints.*")
                attribute("Sling-Model-Packages", "com.cognifide.apm.endpoints,com.cognifide.cq.cqsm.core.models,com.cognifide.cq.cqsm.core.scripts,com.cognifide.cq.cqsm.api.history,com.cognifide.cq.cqsm.core.history")
                attribute("Sling-Nodetypes", "CQ-INF/nodetypes/apm_nodetypes.cnd")
                attribute("CQ-Security-Management-Actions", "com.cognifide.cq.cqsm.foundation.actions")
            }
        }
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.8")
    annotationProcessor("org.projectlombok:lombok:1.18.8")

    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("osgi-bundle"))
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