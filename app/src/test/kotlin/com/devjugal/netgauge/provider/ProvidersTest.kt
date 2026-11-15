package com.devjugal.netgauge.provider

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProvidersTest {
    @Test
    fun `ids are unique`() {
        val ids = Providers.all.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `default is in the list`() {
        assertTrue(Providers.default in Providers.all)
    }

    @Test
    fun `every provider allows its own start page`() {
        Providers.all.forEach { assertTrue(it.id, it.allows(it.url)) }
    }

    @Test
    fun `unknown or missing id falls back to default`() {
        assertEquals(Providers.default, Providers.byId("removed-site"))
        assertEquals(Providers.default, Providers.byId(null))
    }

    @Test
    fun `byId finds a known provider`() {
        assertEquals(FastCom, Providers.byId("fast"))
    }

    @Test
    fun `allows subdomains of allowed hosts`() {
        assertTrue(FastCom.allows("https://api.fast.com/netflix/speedtest"))
        assertTrue(FastCom.allows("https://FAST.com/#"))
    }

    @Test
    fun `rejects other hosts, look-alikes and plain http`() {
        assertFalse(FastCom.allows("https://www.netflix.com/privacy"))
        assertFalse(FastCom.allows("https://notfast.com/"))
        assertFalse(FastCom.allows("https://fast.com.evil.example/"))
        assertFalse(FastCom.allows("http://fast.com/"))
        assertFalse(FastCom.allows("javascript:alert(1)"))
        assertFalse(FastCom.allows("not a url"))
    }
}
