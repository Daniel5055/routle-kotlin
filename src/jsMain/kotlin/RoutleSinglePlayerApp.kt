import io.nacular.doodle.application.Application
import io.nacular.doodle.controls.buttons.PushButton
import io.nacular.doodle.controls.text.Label
import io.nacular.doodle.controls.theme.CommonLabelBehavior
import io.nacular.doodle.core.Display
import io.nacular.doodle.drawing.TextMetrics
import io.nacular.doodle.core.*
import io.nacular.doodle.drawing.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class RoutleSinglePlayerApp(display: Display, textMetrics: TextMetrics, theme: RoutleTheme) : Application, CoroutineScope {
    init {
        val spec = Label("Where to?").apply {
            foregroundColor = theme.rootForegroundColor
            behavior = CommonLabelBehavior(textMetrics)
            font = theme.mediumFont
        }
        display += spec

        launch {
            val maps = getMaps()

            maps.forEach {map ->
                display += PushButton(map.name).apply {
                    foregroundColor = theme.rootForegroundColor
                    font = theme.smallFont

                    fired += {
                        goToSingleplayerMap(map)
                    }

                    size = textMetrics.size(text, font)
                    behavior = NavbarButtonBehavior()
                }
            }


        }

        display.layout = StackedLayout()

    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    override val coroutineContext: CoroutineContext
        get() = Job()
}