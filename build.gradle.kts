import org.nosphere.apache.rat.RatTask
import pl.allegro.tech.build.axion.release.domain.TagNameSerializationConfig
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition

plugins {
    id("pl.allegro.tech.build.axion-release") version "1.11.0"
    id("org.nosphere.apache.rat") version "0.6.0"
    `maven-publish`
    signing
}

defaultTasks("deployAll")

scmVersion {
    useHighestVersion = true
    ignoreUncommittedChanges = false
    tag(closureOf<TagNameSerializationConfig> {
        prefix = "apm"
    })
}

project.version = scmVersion.version

allprojects {
    group = "com.cognifide.aem"
}

tasks {
    register("deployApp") {
        dependsOn(":app:aem:ui.apps:packageDeploy")
    }
    register("deployAll") {
        dependsOn(":app:aem:all:packageDeploy")
    }
    withType<RatTask>().configureEach {
        // Files that don't require a license header
        excludes.add("**/LICENSE")
        excludes.add(".github/**")

        excludes.add("**/main/generated/**")
        excludes.add("**/clientlibs/externals/ace/js/**")
        excludes.add("**/clientlibs/js/json2.js")

        excludes.add("**/*.apm")
        excludes.add("**/*.jar")
        excludes.add("**/*.json")
        excludes.add("**/*.md")
        excludes.add("**/*.tld")
        excludes.add("**/*.txt")
        excludes.add("**/*.vltignore")
        excludes.add("**/*.war")
        excludes.add("**/*.zip")
        excludes.add("**/*.xml")
        excludes.add("**/*.svg")
        excludes.add("**/*.png")
        excludes.add("**/*.cnd")

        // Module
        excludes.add("**/docs/*")
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
        excludes.add("**/out/**")
        excludes.add("**/.idea/**")
        excludes.add("**/*.iml")

        // Swagger API Definition
        excludes.add("**/apidefinition/*.yaml")
    }
}

apply(from = "gradle/common.gradle.kts")

fun formatVersion(version: String, position: ScmPosition) =
        version + "-" + position.branch.replace("/", "").replace(".", "")