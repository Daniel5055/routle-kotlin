package common

import api.setBackgroundColour
import io.nacular.doodle.drawing.Color
import io.nacular.doodle.drawing.Font
import io.nacular.doodle.drawing.FontLoader

class RoutleTheme(val fontLoader: FontLoader, val screenHeight: Double)
{
    var xSmallFont: Font? = null
        private set
    var smallFont: Font? = null
        private set
    var mediumFont: Font? = null
        private set
    var largeFont: Font? = null
        private set
    var navbarBackgroundColor: Color = Color(0x202427u)
        private set
    var navbarForegroundColor: Color = Color(0x939F9Bu)
        private set
    var rootBackgroundColor: Color = Color(0x202427u)
        private set
    var rootForegroundColor: Color = Color(0x939F9Bu)
        private set
    var redColor: Color = Color(0xe0a1a1u)
        private set
    var greenColor: Color = Color(0xa6f2a5u)
        private set
    var grayColor: Color = Color(0x7f8d89u)
        private set

    suspend fun dark() {
        loadFonts("Hubballi", "/static/Hubballi-Regular.ttf")

        navbarBackgroundColor = Color(0x202427u)
        navbarForegroundColor = Color(0x939F9Bu)
        rootBackgroundColor = Color(0x202427u)
        rootForegroundColor = Color(0x939F9Bu)

        redColor = Color(0xe0a1a1u)
        greenColor = Color(0xa6f2a5u)
        grayColor = Color(0x7f8d89u)

        setBackgroundColour(rootBackgroundColor)
    }
    suspend fun light() {
        loadFonts("Hubballi", "/static/Hubballi-Regular.ttf")

        navbarBackgroundColor = Color(0xFFFFFFu)
        navbarForegroundColor = Color(0x000000u)
        rootBackgroundColor = Color(0xFFFFFFu)
        rootForegroundColor = Color(0x000000u)

        redColor = Color(0xFF0000u)
        greenColor = Color(0x00FF00u)
        grayColor = Color(0x888888u)

        setBackgroundColour(rootBackgroundColor)
    }

    private suspend fun loadFonts(fontName: String, fontPath: String) {
        largeFont = fontLoader(fontPath) {
            size = (screenHeight / 15).toInt()
            family = fontName
        }
        mediumFont = fontLoader {
            size = (screenHeight / 25).toInt()
            family = fontName
        }
        smallFont = fontLoader {
            size = (screenHeight / 35).toInt()
            family = fontName
        }
        xSmallFont = fontLoader {
            size = (screenHeight / 45).toInt()
            family = fontName
        }
    }
}
