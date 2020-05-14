package com.cognifide.apm.core.services.version

import com.cognifide.apm.api.scripts.Script
import com.cognifide.apm.core.grammar.ReferenceGraph
import org.apache.sling.api.resource.ResourceResolver

interface VersionService {

    fun getScriptVersion(resolver: ResourceResolver, script: Script): ScriptVersion

    fun getVersionPath(script: Script): String

    fun countChecksum(root: ReferenceGraph.TreeRoot): String

    fun updateVersionIfNeeded(resolver: ResourceResolver, vararg scripts: Script)
}