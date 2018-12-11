plugins {
    id("com.cognifide.aem.bundle")
    groovy
    java
}

description = "AEM Permission Management :: Application Core"

aem {
    bundle.apply {
        this.attribute("Bundle-Category", "apm")
        this.attribute("Bundle-Vendor", "Cognifide")
        this.attribute("Sling-Model-Packages", "com.cognifide.cq.cqsm.core.models,com.cognifide.cq.cqsm.core.scripts,com.cognifide.cq.cqsm.api.history")
        this.attribute("Export-Package", "com.cognifide.cq.cqsm.foundation.actions.*;com.cognifide.cq.cqsm.api.*;com.cognifide.cq.cqsm.core.models.*;com.cognifide.cq.cqsm.core.automaticexecutor.*")
        this.attribute("Import-Package", "!sun.misc,*")
        this.attribute("Sling-Nodetypes", "CQ-INF/nodetypes/cqsm_nodetypes.cnd")
        this.attribute("CQ-Security-Management-Actions", "com.cognifide.cq.cqsm.foundation.actions")
    }
}

dependencies {
    aemInstall("com.cognifide.cq.actions:com.cognifide.cq.actions.api:6.0.2")
    aemInstall("com.cognifide.cq.actions:com.cognifide.cq.actions.core:6.0.2")
    aemInstall("com.cognifide.cq.actions:com.cognifide.cq.actions.msg.replication:6.0.2")

    testCompile("junit:junit:4.10")
    testCompile("org.mockito:mockito-core:1.9.5")
    testCompile("org.codehaus.groovy:groovy-all:2.4.13")
    testCompile("org.spockframework:spock-core:1.1-groovy-2.4")

    compileOnly("com.google.guava:guava:15.0")
    compileOnly("com.google.code.gson:gson:2.3.1")

    compileOnly(group = "com.adobe.aem", name = "uber-jar", version = "6.3.0", classifier = "apis")
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
