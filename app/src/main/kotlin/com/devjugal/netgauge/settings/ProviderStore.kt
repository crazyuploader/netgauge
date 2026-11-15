package com.devjugal.netgauge.settings

import android.content.Context
import androidx.core.content.edit

/** Remembers the last-used speed test site, so the app opens it again next time. */
class ProviderStore(
    context: Context,
) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var lastProviderId: String?
        get() = prefs.getString(KEY_PROVIDER_ID, null)
        set(value) = prefs.edit { putString(KEY_PROVIDER_ID, value) }

    private companion object {
        const val KEY_PROVIDER_ID = "provider_id"
    }
}
