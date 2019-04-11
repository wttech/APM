import com.cognifide.gradle.aem.pkg.tasks.Compose
import pl.allegro.tech.build.axion.release.domain.TagNameSerializationConfig
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition

plugins {
    id("nu.studer.credentials") version "1.0.7"
    id("pl.allegro.tech.build.axion-release") version "1.10.1"
    id("org.nosphere.apache.rat") version "0.4.0"
    id("com.cognifide.aem.package")
    `maven-publish`
}

description = "AEM Permission Management :: Root"

scmVersion {
    useHighestVersion = true
    ignoreUncommittedChanges = false
    tag(closureOf<TagNameSerializationConfig> {
        prefix = "apm"
        branchPrefix = mapOf(
                "aem/6.3.0" to "aem630",
                "aem/6.4.0" to "aem640",
                "aem/6.5.0" to "aem650")
        branchVersionCreator = mapOf(
                "aem/.*" to KotlinClosure2({ version : String, position : ScmPosition -> version + "-" + position.branch.replace(Regex("\\/\\."),"")}),
                ".*" to "simple"
        )
    })
}

project.version = scmVersion.version

allprojects {
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

val apmRepositoryUsername: String? by extra
val apmRepositoryPassword: String? by extra
publishing {
    publications {
        create<MavenPublication>("apm") {
            val apmContent by tasks.named(Compose.NAME, Compose::class)
            artifact(apmContent)
            afterEvaluate {
                artifactId = "apm-content"
            }
        }
    }
    repositories {
        maven {
            name = "OSSSonatypeOrg"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = apmRepositoryUsername
                password = apmRepositoryPassword
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
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