import com.cognifide.gradle.aem.bundle.tasks.bundle
import org.gradle.jvm.tasks.Jar

plugins {
    id("com.cognifide.aem.bundle")
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.noarg") version "1.3.72"
    antlr
    groovy
    java
    `maven-publish`
    signing
}

description = "APM Core"

apply(from = rootProject.file("app/common.gradle.kts"))
apply(from = rootProject.file("app/aem/common.gradle.kts"))

aem {
    tasks {
        jar {
            bundle {
                exportPackage("com.cognifide.apm.core.*")
                importPackage("javax.annotation;version=0.0.0")
                attribute("Sling-Model-Packages",
                        listOf(
                                "com.cognifide.apm.core.crypto",
                                "com.cognifide.apm.core.endpoints",
                                "com.cognifide.apm.core.ui.models",
                                "com.cognifide.apm.core.scripts",
                                "com.cognifide.apm.core.services",
                                "com.cognifide.apm.core.history"
                        ).joinToString(","))
                attribute("APM-Actions", "com.cognifide.apm.foundation.actions")
                excludePackage("org.antlr.stringtemplate", "org.antlr.v4.gui")
                embedPackage("org.antlr:antlr4-runtime:4.7.2", "org.antlr.v4.runtime.*")
                embedPackage("org.jetbrains.kotlin:kotlin-reflect:1.3.72", "kotlin.reflect.*")
                embedPackage("org.jetbrains.kotlin:kotlin-stdlib:1.3.72", "kotlin.*")
            }
        }
    }
}

dependencies {
    implementation(project(":app:aem:api"))
    implementation(project(":app:aem:actions.main"))

    antlr("org.antlr:antlr4:4.7.2")

    compileOnly("org.projectlombok:lombok:1.18.8")
    annotationProcessor("org.projectlombok:lombok:1.18.8")

    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("reflect"))
}

sourceSets {
    main {
        java {
            srcDirs("src/main/generated")
        }
    }
}

tasks {
    named("compileKotlin").configure {
        dependsOn("generateGrammarSource")
    }

    named("generateGrammarSource", AntlrTask::class).configure {
        maxHeapSize = "64m"
        arguments = arguments + listOf("-visitor", "-long-messages", "-package", "com.cognifide.apm.core.grammar.antlr")
        outputDirectory = project.file("src/main/generated/com/cognifide/apm/core/grammar/antlr")
    }

    javadoc {
        exclude("com/cognifide/apm/core/history/**")
    }

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
            pom {
                name.set("APM - " + project.name)
                description.set(project.description)
            }
        }
    }
}