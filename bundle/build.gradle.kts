plugins {
    id("com.cognifide.aem.bundle")
    id("io.franzbecker.gradle-lombok")
    kotlin("jvm")
    antlr
    groovy
    java
    `maven-publish`
    signing
}

description = "AEM Permission Management :: Application Core"

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
            attribute("Sling-Model-Packages", "com.cognifide.cq.cqsm.core.models,com.cognifide.cq.cqsm.core.scripts,com.cognifide.cq.cqsm.api.history")
            attribute("Sling-Nodetypes", "CQ-INF/nodetypes/cqsm_nodetypes.cnd")
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

    compileOnly("com.cognifide.cq.actions:com.cognifide.cq.actions.api:6.0.2")

    compileOnly("com.adobe.aem:uber-jar:6.3.0:apis")
    compileOnly("org.osgi:osgi.cmpn:6.0.0")
    compileOnly("org.osgi:org.osgi.core:6.0.0")
    compileOnly("org.osgi:org.osgi.service.component.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.service.metatype.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.annotation:6.0.0")

    compileOnly("com.google.code.gson:gson:2.3.1")
    compileOnly("commons-lang:commons-lang:2.5")
    compileOnly("commons-io:commons-io:2.4")
    compileOnly("commons-codec:commons-codec:1.5")
    compileOnly("commons-collections:commons-collections:3.2.1")
    compileOnly("javax.jcr:jcr:2.0")
    compileOnly("javax.inject:javax.inject:1")
    compileOnly("javax.servlet:jsp-api:2.0")
    compileOnly("javax.servlet:servlet-api:2.4")
    compileOnly("org.slf4j:slf4j-log4j12:1.7.7")
    compileOnly("org.projectlombok:lombok:1.16.20")
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    register<org.gradle.jvm.tasks.Jar>("sourcesJar") {
        classifier = "sources"
        from(sourceSets.main.get().allJava)
    }
    register<org.gradle.jvm.tasks.Jar>("javadocJar") {
        classifier = "javadoc"
        from(javadoc.get().destinationDir)
    }
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
    dependsOn(tasks.generateGrammarSource)
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-visitor", "-long-messages", "-package", "com.cognifide.apm.antlr")
    outputDirectory = project.file("src/main/generated/com/cognifide/apm/antlr")
}

tasks.test {
    useJUnit()
}

sourceSets {
    main {
        java {
            srcDirs("src/main/generated")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("apm") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            afterEvaluate {
                artifactId = "apm-bundle"
                version = rootProject.version
            }
        }
    }
}
