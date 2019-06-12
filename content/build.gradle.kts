plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
    signing
}

description = "AEM Permission Management :: Package"

publishing {
    publications {
        create<MavenPublication>("apm") {
            val apmContent by tasks.named(Compose.NAME, Compose::class)
            artifact(apmContent)
            afterEvaluate {
                artifactId = "apm-content"
                version = rootProject.version
            }
        }
    }
}
