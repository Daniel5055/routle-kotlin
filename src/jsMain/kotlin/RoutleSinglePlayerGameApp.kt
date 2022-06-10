import io.nacular.doodle.application.Application
import io.nacular.doodle.controls.form.textField
import io.nacular.doodle.controls.text.Label
import io.nacular.doodle.controls.text.TextField
import io.nacular.doodle.controls.text.TextFieldBehavior
import io.nacular.doodle.controls.theme.CommonLabelBehavior
import io.nacular.doodle.core.Display
import io.nacular.doodle.drawing.TextMetrics
import io.nacular.doodle.core.*
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.drawing.paint
import io.nacular.doodle.focus.FocusManager
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Rectangle
import io.nacular.doodle.geometry.Size
import io.nacular.doodle.image.ImageLoader
import io.nacular.doodle.theme.native.NativeTextFieldBehaviorModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow
import kotlin.math.sqrt

class RoutleSinglePlayerGameApp(display: Display, textMetrics: TextMetrics, focusManager: FocusManager, imageLoader: ImageLoader, theme: RoutleTheme) : Application, CoroutineScope {
    init {
        val spec = Label("Loading").apply {
            foregroundColor = theme.rootForegroundColor
            behavior = CommonLabelBehavior(textMetrics)
            font = theme.mediumFont
        }
        display += spec
        val winMessage = Label("You win!").apply {
            foregroundColor = theme.rootForegroundColor
            font = theme.largeFont
            behavior = CommonLabelBehavior(textMetrics)
        }

        val map = MapView(display.height / 5 * 3) {
            display.children.clear()
            display += winMessage
        }
        display += map

        val info = Label("Loading").apply {
            foregroundColor = theme.rootForegroundColor
            font = theme.xSmallFont
            behavior = CommonLabelBehavior(textMetrics)
        }
        display += info

        val inputField = InputView(textMetrics, theme.rootForegroundColor) {input ->
            launch {
                // Get the city and draw it
                val cities = getCities(input)
                if (cities.isNotEmpty()) {
                    if (map.drawCity(cities)) {
                        info.text = input
                    } else {
                        info.text = "$input is too far!"
                    }
                } else {
                    info.text = "$input ?"
                }
                info.rerender()
            }
        }.apply {
            font = theme.mediumFont
        }
        display += inputField
        focusManager.requestFocus(inputField)

        display.layout = object: Layout {
            override fun layout(container: PositionableContainer) {
                var y = 0.0
                container.children.forEach { child ->
                    child.x = (container.width - child.width) / 2
                    child.y = y
                    y += child.height + 5.0
                }
            }

        }

        launch {
            var from = getRandomCity()
            while (from == null) {
                from = getRandomCity()
            }
            var to = getRandomCity()

            while (to == null || sqrt((to.relX - from.relX).pow(2) + (to.relY - from.relY).pow(2)) < 0.3) {
                to = getRandomCity()
            }

            spec.text = "Get from ${from.name} to ${to.name}"
            spec.rerender()
            map.currentCity = from
            info.text = from.name
            info.rerender()
            map.endCity = to
        }

        // Set the image
        launch {
            val routleMap = getMap()

            imageLoader.load(routleMap.path)?.let {
                map.mapImage = it
            }

            map.setSearchRadius(routleMap.searchRadius)
        }
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    override val coroutineContext: CoroutineContext
        get() = Job()
}