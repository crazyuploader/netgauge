package com.devjugal.netgauge

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import com.devjugal.netgauge.provider.Providers
import com.devjugal.netgauge.provider.SpeedTestProvider
import com.devjugal.netgauge.settings.ProviderStore
import com.devjugal.netgauge.web.ProviderWebViewClient

/** Shows the chosen speed test site full screen. */
class MainActivity : ComponentActivity() {
    private lateinit var provider: SpeedTestProvider
    private lateinit var container: FrameLayout
    private var webView: WebView? = null // null after a renderer crash closed the screen
    private var rendererCrashes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // A test runs for a minute or two; keep the TV or phone from dimming.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WebView.setWebContentsDebuggingEnabled(applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0)

        val store = ProviderStore(this)
        provider = Providers.byId(store.lastProviderId)
        store.lastProviderId = provider.id

        // Insets go on a plain container: WebView does not lay its content out inside its own padding.
        container = FrameLayout(this)
        ViewCompat.setOnApplyWindowInsetsListener(container) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.updatePadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
        setContentView(container)
        showWebView(savedInstanceState)
    }

    private fun showWebView(savedInstanceState: Bundle? = null) {
        val view = createWebView()
        webView = view
        container.addView(view, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        view.requestFocus()
        if (savedInstanceState == null || view.restoreState(savedInstanceState) == null) {
            view.loadUrl(provider.url)
        }
    }

    private fun destroyWebView() {
        webView?.let {
            container.removeView(it)
            it.destroy()
        }
        webView = null
    }

    // JavaScript is required: every speed test site is a JavaScript app. No JS bridge is added.
    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(): WebView =
        WebView(this).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true // sites keep their settings in localStorage
                allowFileAccess = false
                allowContentAccess = false
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            }
            overScrollMode = View.OVER_SCROLL_NEVER
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            webViewClient = ProviderWebViewClient(provider, onRendererGone = ::onRendererGone)
        }

    /** The renderer crashed or was killed (often low memory). The WebView is unusable and must be replaced. */
    private fun onRendererGone() {
        destroyWebView()
        rendererCrashes++
        // In the background, or crashing again and again: close instead of restarting a test nobody sees.
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) || rendererCrashes > MAX_RENDERER_RESTARTS) {
            finish()
            return
        }
        showWebView()
    }

    // Keys must be filtered here, before the focused WebView sees them. androidx marks its override of this public
    // framework method @RestrictTo, which makes lint flag every app that overrides it.
    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean = provider.keyPolicy.blocks(event.keyCode) || super.dispatchKeyEvent(event)

    override fun onResume() {
        super.onResume()
        webView?.onResume()
        webView?.resumeTimers()
    }

    // onPause alone keeps the page's JavaScript running; pauseTimers halts its timers, so a test does not carry on
    // in the background.
    override fun onPause() {
        webView?.onPause()
        webView?.pauseTimers()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView?.saveState(outState)
    }

    override fun onDestroy() {
        destroyWebView()
        super.onDestroy()
    }

    private companion object {
        const val MAX_RENDERER_RESTARTS = 2
    }
}
