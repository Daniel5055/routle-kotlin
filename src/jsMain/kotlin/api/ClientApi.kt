package api

import io.nacular.doodle.geometry.Size
import kotlinx.browser.window

fun getClientScreenSize(): Size {
    return Size(window.screen.width, window.screen.height)
}

fun isMobileClient(): Boolean {
    return window.navigator.userAgent.contains("mobi")
}