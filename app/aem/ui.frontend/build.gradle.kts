plugins {
    id("com.github.node-gradle.node") version "2.2.4"
}

node {
    download = true
    version = "13.9.0"
    npmVersion = "6.14.8"
}

tasks.create("build")
tasks["build"].dependsOn("npm_run_build")
tasks["npm_run_build"].dependsOn("npm_install")