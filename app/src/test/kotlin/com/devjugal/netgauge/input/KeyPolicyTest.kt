package com.devjugal.netgauge.input

import android.view.KeyEvent
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyPolicyTest {
    @Test
    fun `block navigation drops arrows, tab and page keys`() {
        listOf(
            KeyEvent.KEYCODE_TAB,
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_PAGE_UP,
            KeyEvent.KEYCODE_PAGE_DOWN,
        ).forEach { assertTrue("$it", KeyPolicy.BLOCK_NAVIGATION.blocks(it)) }
    }

    @Test
    fun `block navigation keeps back, OK and media keys`() {
        listOf(
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_HOME,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
        ).forEach { assertFalse("$it", KeyPolicy.BLOCK_NAVIGATION.blocks(it)) }
    }

    @Test
    fun `allow all drops nothing`() {
        assertFalse(KeyPolicy.ALLOW_ALL.blocks(KeyEvent.KEYCODE_DPAD_DOWN))
    }
}
