package com.cognifide.apm.core.services.version

interface ScriptVersion {

    val scriptPath: String
    val lastChecksum: String?
}