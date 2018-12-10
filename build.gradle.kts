import com.cognifide.gradle.aem.pkg.ComposeTask

plugins {
    id("com.cognifide.aem.package") version "5.1.3"
}

buildscript {
    repositories {
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("io.franzbecker:gradle-lombok:1.14")
    }
}

allprojects {
    group = "com.cognifide.cq"
    version = "3.1.0-SNAPSHOT"

    apply(plugin = "maven")
    apply(plugin = "io.franzbecker.gradle-lombok")

    repositories {
        jcenter()
        mavenCentral()
        maven(url = "https://repo.adobe.com/nexus/content/groups/public/")
        mavenLocal()
    }
}

defaultTasks(":aem")
tasks.create("aem") {
    dependsOn("aemDeploy")
}
tasks.getByName("aemCompose") {
    val t: ComposeTask = this as ComposeTask
    t.includeProject(":bundle")
    t.includeProject(":content")
}