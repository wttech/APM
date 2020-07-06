package com.cognifide.apm.core.endpoints

import com.cognifide.apm.api.scripts.Script
import org.apache.commons.io.FilenameUtils
import java.util.*

class ScriptDto(script: Script) {

    val name: String = FilenameUtils.getName(script.path)
    val path: String = script.path
    val author: String = script.author
    val launchEnabled: Boolean = script.isLaunchEnabled
    val launchMode: String = script.launchMode.name.toLowerCase()
    val lastModified: Date = script.lastModified
    val valid: Boolean = script.isValid

}