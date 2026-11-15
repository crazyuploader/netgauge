package com.devjugal.netgauge.web

import android.os.Build
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.devjugal.netgauge.provider.SpeedTestProvider

/**
 * Keeps the WebView on the provider's site and runs the provider's setup script once.
 *
 * @param onRendererGone Called when the WebView's renderer crashed or was killed. The WebView
 *   cannot be used after that, so the caller must replace it.
 */
class ProviderWebViewClient(
    private val provider: SpeedTestProvider,
    private val onRendererGone: () -> Unit,
) : WebViewClient() {
    private var setupDone = false

    /** Blocks top-level navigation away from the provider's site, for example the Privacy link. */
    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest,
    ): Boolean = request.isForMainFrame && !provider.allows(request.url.toString())

    override fun onPageFinished(
        view: WebView,
        url: String,
    ) {
        val script = provider.setupScript ?: return
        if (setupDone || !provider.allows(url)) return
        view.evaluateJavascript(script) { result ->
            // evaluateJavascript returns the value as JSON, so a string arrives quoted. Anything else (null on an
            // error page or a script error) leaves setupDone false, so the next successful page load tries again.
            when (result) {
                "\"${SpeedTestProvider.RELOAD}\"" -> {
                    setupDone = true
                    if (view.isAttachedToWindow) view.reload() // skip if the WebView was destroyed meanwhile
                }

                "\"${SpeedTestProvider.OK}\"" -> {
                    setupDone = true
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRenderProcessGone(
        view: WebView,
        detail: RenderProcessGoneDetail,
    ): Boolean {
        onRendererGone()
        return true
    }
}
