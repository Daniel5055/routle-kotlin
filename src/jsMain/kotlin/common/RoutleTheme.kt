package common

import io.nacular.doodle.drawing.Color
import io.nacular.doodle.drawing.Font
import io.nacular.doodle.drawing.FontLoader

class RoutleTheme(
    val xSmallFont: Font?,
    val smallFont: Font?,
    val mediumFont: Font?,
    val largeFont: Font?,
    val navbarBackgroundColor: Color = Color(0x202427u),
    val navbarForegroundColor: Color = Color(0x939F9Bu),
    val rootBackgroundColor: Color = Color(0x202427u),
    val rootForegroundColor: Color = Color(0x939F9Bu),
    val redColor: Color = Color(0xe0a1a1u),
    val greenColor: Color = Color(0xa6f2a5u),
    val grayColor: Color = Color(0x7f8d89u),
    )

class DarkRoutleTheme {
    companion object {
        suspend operator fun invoke(fontLoader: FontLoader, screenHeight: Double): RoutleTheme {

            val fontName = "Hubballi"
            val largeFont = fontLoader("/static/Hubballi-Regular.ttf") {
                size = (screenHeight / 15).toInt()
                family = fontName
            }
            val mediumFont = fontLoader {
                size = (screenHeight / 25).toInt()
                family = fontName
            }
            val smallFont = fontLoader {
                size = (screenHeight / 35).toInt()
                family = fontName
            }
            val xSmallFont = fontLoader {
                size = (screenHeight / 45).toInt()
                family = fontName
            }

            return RoutleTheme(xSmallFont, smallFont, mediumFont, largeFont)
        }
    }
}

class LightRoutleTheme {
    companion object {
        suspend operator fun invoke(fontLoader: FontLoader, screenHeight: Double): RoutleTheme {

            val fontName = "Hubballi"
            val largeFont = fontLoader("/static/Hubballi-Regular.ttf") {
                size = (screenHeight / 15).toInt()
                family = fontName
            }
            val mediumFont = fontLoader {
                size = (screenHeight / 25).toInt()
                family = fontName
            }
            val smallFont = fontLoader {
                size = (screenHeight / 35).toInt()
                family = fontName
            }
            val xSmallFont = fontLoader {
                size = (screenHeight / 45).toInt()
                family = fontName
            }

            return RoutleTheme(xSmallFont, smallFont, mediumFont, largeFont)
        }
    }
}

