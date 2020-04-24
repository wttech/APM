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

aem {
    tasks {
        bundle {
            installPath = "/apps/apm/install"
            javaPackage = "com.cognifide.cq.cqsm"
            category = "apm"
            displayName = "AEM Permission Management"
            license = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            vendor = "Cognifide"
            exportPackage("com.cognifide.cq.cqsm.foundation.actions.*")
            exportPackage("com.cognifide.cq.cqsm.api.*")
            exportPackage("com.cognifide.cq.cqsm.core.models.*")
            exportPackage("com.cognifide.apm.tools.*")
            exportPackage("com.cognifide.apm.endpoints.*")
            attribute("Sling-Model-Packages", "com.cognifide.apm.endpoints,com.cognifide.cq.cqsm.core.models,com.cognifide.cq.cqsm.core.scripts,com.cognifide.cq.cqsm.api.history,com.cognifide.cq.cqsm.core.history")
            attribute("Sling-Nodetypes", "CQ-INF/nodetypes/apm_nodetypes.cnd")
            attribute("CQ-Security-Management-Actions", "com.cognifide.cq.cqsm.foundation.actions")
        }
    }
}

dependencies {
    testCompile("junit:junit:4.12")
    testCompile("org.mockito:mockito-core:1.9.5")
    testCompile("org.codehaus.groovy:groovy-all:2.5.7")
    testCompile("org.spockframework:spock-core:1.3-groovy-2.5")

    antlr("org.antlr:antlr4:4.7.2")

    compileOnly("org.projectlombok:lombok:1.18.8")
    annotationProcessor("org.projectlombok:lombok:1.18.8")

    compileOnly("com.cognifide.cq.actions:com.cognifide.cq.actions.api:6.0.2")

    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("osgi-bundle"))
}

tasks {
    named("compileKotlin").configure {
        dependsOn("generateGrammarSource")
    }

    named("generateGrammarSource", AntlrTask::class).configure {
        maxHeapSize = "64m"
        arguments = arguments + listOf("-visitor", "-long-messages", "-package", "com.cognifide.apm.grammar.antlr")
        outputDirectory = project.file("src/main/generated/com/cognifide/apm/grammar/antlr")
    }

}

sourceSets {
    main {
        java {
            srcDirs("src/main/generated")
        }
    }
}