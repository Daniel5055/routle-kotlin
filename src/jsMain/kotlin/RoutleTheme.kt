import io.nacular.doodle.drawing.Color
import io.nacular.doodle.drawing.Font
import io.nacular.doodle.drawing.FontLoader

class RoutleTheme(
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

            val largeFont = fontLoader("/static/Quicksand-Regular.ttf") {
                size = (displayHeight / 15).toInt()
                family = "Quicksand"
            }
            val mediumFont = fontLoader {
                size = (displayHeight / 25).toInt()
                family = "Quicksand"
            }
            val smallFont = fontLoader {
                size = (displayHeight / 35).toInt()
                family = "Quicksand"
            }

            return RoutleTheme(smallFont, mediumFont, largeFont)

        }
    }
}

