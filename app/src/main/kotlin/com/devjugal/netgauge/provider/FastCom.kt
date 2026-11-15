package com.devjugal.netgauge.provider

import com.devjugal.netgauge.input.KeyPolicy

/**
 * fast.com settings written on first run. fast.com keeps its settings in localStorage
 * when "Save config for this device" is ticked, under these keys.
 */
internal val FAST_COM_PRESET =
    linkedMapOf(
        "minConnections" to "8",
        "maxConnections" to "16",
        "minDuration" to "60",
        "maxDuration" to "120",
        "measureUploadLatency" to "true", // Measure loaded latency during upload
        "showAdvanced" to "true", // Always show all metrics
        "shouldPersist" to "true", // Save config for this device
    )

/** Writes [FAST_COM_PRESET] only when no saved config exists, so changes made in the page stay. */
internal fun fastComSetupScript(): String {
    val writes = FAST_COM_PRESET.entries.joinToString("") { (key, value) -> "localStorage.setItem('$key','$value');" }
    return "(function(){" +
        "if(localStorage.getItem('shouldPersist')!==null)return '${SpeedTestProvider.OK}';" +
        writes +
        "return '${SpeedTestProvider.RELOAD}';" +
        "})()"
}

val FastCom =
    SpeedTestProvider(
        id = "fast",
        displayName = "FAST.com",
        url = "https://fast.com/",
        allowedHosts = setOf("fast.com"),
        setupScript = fastComSetupScript(),
        keyPolicy = KeyPolicy.BLOCK_NAVIGATION,
    )
