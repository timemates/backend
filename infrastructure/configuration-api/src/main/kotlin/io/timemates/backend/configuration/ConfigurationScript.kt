package io.timemates.backend.configuration

import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.implicitReceivers

@KotlinScript(
    displayName = "TimeMates configuration",
    fileExtension = ".tmc",
    compilationConfiguration = ConfigurationScriptCompilationSettings::class,
)
public abstract class ConfigurationScript

public object ConfigurationScriptCompilationSettings : ScriptCompilationConfiguration({
    implicitReceivers(TimeMatesConfigurationScope::class)
})