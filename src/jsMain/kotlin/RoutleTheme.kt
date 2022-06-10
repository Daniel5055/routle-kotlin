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
    )

class DarkRoutleTheme {

    private constructor()

    companion object {
        suspend operator fun invoke(fontLoader: FontLoader, displayHeight: Double): RoutleTheme {

            val fontName = "Hubballi"
            val largeFont = fontLoader("/static/Hubballi-Regular.ttf") {
                size = (displayHeight / 15).toInt()
                family = fontName
            }
            val mediumFont = fontLoader {
                size = (displayHeight / 25).toInt()
                family = fontName
            }
            val smallFont = fontLoader {
                size = (displayHeight / 35).toInt()
                family = fontName
            }
            val xSmallFont = fontLoader {
                size = (displayHeight / 45).toInt()
                family = fontName
            }

            return RoutleTheme(xSmallFont, smallFont, mediumFont, largeFont)

        }
    }
}

