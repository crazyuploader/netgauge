package com.devjugal.netgauge.provider

import com.devjugal.netgauge.input.KeyPolicy
import java.net.URI

/**
 * A speed test website the app can open.
 *
 * @property id Stable key, saved as the last-used provider. Never change it once released.
 * @property url Page to load. Must be https.
 * @property allowedHosts Hosts the WebView may navigate to; subdomains are included. Other links are blocked.
 * @property setupScript JavaScript run after the first successful page load. It must return the string [OK]
 *   when nothing changed, or [RELOAD] to reload the page, for example after it wrote settings the page
 *   reads at start. Any other result (an error page, a script error) makes it run again on the next load.
 * @property keyPolicy Which remote keys are dropped on this site.
 */
data class SpeedTestProvider(
    val id: String,
    val displayName: String,
    val url: String,
    val allowedHosts: Set<String>,
    val setupScript: String? = null,
    val keyPolicy: KeyPolicy = KeyPolicy.BLOCK_NAVIGATION,
) {
    /** True when [url] is https and on one of [allowedHosts] or their subdomains. */
    fun allows(url: String): Boolean {
        val uri = runCatching { URI(url) }.getOrNull() ?: return false
        val host = uri.host?.lowercase() ?: return false
        return uri.scheme == "https" && allowedHosts.any { host == it || host.endsWith(".$it") }
    }

    companion object {
        const val OK = "ok"
        const val RELOAD = "reload"
    }
}
