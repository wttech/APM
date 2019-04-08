plugins {
    id("org.nosphere.apache.rat") version "0.4.0"
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

tasks.rat {
    // Files that don't require a license header
    excludes.add("**/LICENSE")
    excludes.add(".travis.yml")


    excludes.add("**/clientlibs/externals/ace/js/**")
    excludes.add("**/clientlibs/js/json2.js")

    excludes.add("**/*.cqsm")
    excludes.add("**/*.jar")
    excludes.add("**/*.json")
    excludes.add("**/*.md")
    excludes.add("**/*.tld")
    excludes.add("**/*.txt")
    excludes.add("**/*.vltignore")
    excludes.add("**/*.war")
    excludes.add("**/*.zip")
    excludes.add("**/*.xml")

    // Module
    excludes.add("**/documentation/*")
    excludes.add("**/misc/**")

    // Gradle files
    excludes.add("**/gradle/**")
    excludes.add("**/*.gradle.kts")
    excludes.add("gradlew")
    excludes.add("gradlew.bat")

    // Eclipse files
    excludes.add("**/.project")
    excludes.add("**/.classpath")

    // IntelliJ files
    excludes.add("**/build/**")
    excludes.add("**/.idea/**")
    excludes.add("**/*.iml")
}

apply(from = "gradle/common.gradle.kts")