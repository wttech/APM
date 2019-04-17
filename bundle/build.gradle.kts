plugins {
    id("com.cognifide.aem.bundle")
    id("io.franzbecker.gradle-lombok")
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
            displayName = "AEM Permission Management"
            license = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            vendor = "Cognifid"
            embedPackage("com.google.code.gson", true, "com.google.code.gson:gson:2.3.1")
            embedPackage("com.google.guava", true, "com.google.guava:guava:15.0")
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
    testImplementation("junit:junit:4.10")
    testImplementation("org.mockito:mockito-core:1.9.5")
    testImplementation("org.codehaus.groovy:groovy-all:2.4.13")
    testImplementation("org.spockframework:spock-core:1.1-groovy-2.4")

    compileOnly("com.cognifide.cq.actions:com.cognifide.cq.actions.api:6.0.2")
    compileOnly("com.cognifide.cq.actions:com.cognifide.cq.actions.core:6.0.2")
    compileOnly("com.cognifide.cq.actions:com.cognifide.cq.actions.msg.replication:6.0.2")

    compileOnly("com.google.guava:guava:15.0")
    compileOnly("com.google.code.gson:gson:2.3.1")

    compileOnly("com.adobe.aem:uber-jar:6.4.0:apis")
    compileOnly("org.osgi:osgi.cmpn:6.0.0")
    compileOnly("org.osgi:org.osgi.core:6.0.0")
    compileOnly("org.osgi:org.osgi.service.component.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.service.metatype.annotations:1.3.0")
    compileOnly("org.osgi:org.osgi.annotation:6.0.0")

    compileOnly("commons-lang:commons-lang:2.5")
    compileOnly("commons-io:commons-io:2.4")
    compileOnly("commons-codec:commons-codec:1.5")
    compileOnly("commons-collections:commons-collections:3.2.1")
    compileOnly("javax.jcr:jcr:2.0")
    compileOnly("org.apache.sling:org.apache.sling.jcr.api:2.1.0")
    compileOnly("org.apache.sling:org.apache.sling.commons.osgi:2.2.0")
    compileOnly("org.apache.sling:org.apache.sling.servlets.resolver:2.3.2")
    compileOnly("javax.inject:javax.inject:1")
    compileOnly("javax.servlet:jsp-api:2.0")
    compileOnly("javax.servlet:servlet-api:2.4")
    compileOnly("org.slf4j:slf4j-log4j12:1.7.7")
    compileOnly("org.projectlombok:lombok:1.16.10")
}

//tasks {
//    register<Jar>("sourcesJar") {
//        classifier = "sources"
//        from(sourceSets.main.get().allJava)
//    }
//
//    register<Jar>("javadocJar") {
//        classifier = "javadoc"
//        from(javadoc.get().destinationDir)
//    }
//}

publishing {
    publications {
        create<MavenPublication>("apm") {
            from(components["java"])
//            artifact(tasks["sourcesJar"])
//            artifact(tasks["javadocJar"])
            afterEvaluate {
                artifactId = "apm-bundle"
                version = rootProject.version
            }
            
        }
    }
}
