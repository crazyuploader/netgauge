package com.devjugal.netgauge.input

import android.view.KeyEvent

/** Which remote or keyboard keys the app drops before they reach the web page. */
enum class KeyPolicy(
    private val blockedKeyCodes: Set<Int>,
) {
    /**
     * For pages that start on their own: arrows, Tab and page keys would only scroll or move focus.
     * On these sites the page's own controls are reachable by touch only.
     */
    BLOCK_NAVIGATION(
        setOf(
            KeyEvent.KEYCODE_TAB,
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_PAGE_UP,
            KeyEvent.KEYCODE_PAGE_DOWN,
            KeyEvent.KEYCODE_MOVE_HOME,
            KeyEvent.KEYCODE_MOVE_END,
        ),
    ),

    /** For pages that need the remote, for example a site with a start button. */
    ALLOW_ALL(emptySet()),
    ;

    fun blocks(keyCode: Int): Boolean = keyCode in blockedKeyCodes
}
