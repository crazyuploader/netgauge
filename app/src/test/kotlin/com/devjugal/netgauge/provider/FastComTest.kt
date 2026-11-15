package com.devjugal.netgauge.provider

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FastComTest {
    private val script = requireNotNull(FastCom.setupScript)

    @Test
    fun `preset matches the requested test settings`() {
        assertEquals(
            mapOf(
                "minConnections" to "8",
                "maxConnections" to "16",
                "minDuration" to "60",
                "maxDuration" to "120",
                "measureUploadLatency" to "true",
                "showAdvanced" to "true",
                "shouldPersist" to "true",
            ),
            FAST_COM_PRESET,
        )
    }

    @Test
    fun `script writes every preset key`() {
        FAST_COM_PRESET.forEach { (key, value) ->
            assertTrue(key, script.contains("localStorage.setItem('$key','$value');"))
        }
    }

    @Test
    fun `script keeps a saved config and asks for a reload after writing`() {
        assertTrue(script.contains("if(localStorage.getItem('shouldPersist')!==null)return 'ok';"))
        assertTrue(script.endsWith("return '${SpeedTestProvider.RELOAD}';})()"))
    }
}
