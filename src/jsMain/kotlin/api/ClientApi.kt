package api

import io.nacular.doodle.drawing.Color
import io.nacular.doodle.geometry.Size
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams

fun getClientScreenSize(): Size {
    return Size(window.screen.width, window.screen.height)
}

fun isMobileClient(): Boolean {
    return window.navigator.userAgent.contains("mobi")
}

fun getMapDifficulty(): Double {
    return URLSearchParams(window.location.search).get("difficulty")?.toDouble() ?: 1.0
}

fun reloadPage() {
    window.location.href = window.location.href
}

fun setBackgroundColour(color: Color) {
    println(color.hexString)
    document.bgColor = "#${color.hexString}"
}