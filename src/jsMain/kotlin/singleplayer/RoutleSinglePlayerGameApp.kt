package singleplayer

import common.RoutleTheme
import api.getCities
import api.getMap
import api.getRandomCity
import io.nacular.doodle.application.Application
import io.nacular.doodle.controls.text.Label
import io.nacular.doodle.controls.theme.CommonLabelBehavior
import io.nacular.doodle.core.Display
import io.nacular.doodle.drawing.TextMetrics
import io.nacular.doodle.core.*
import io.nacular.doodle.focus.FocusManager
import io.nacular.doodle.image.ImageLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow
import kotlin.math.sqrt

class RoutleSinglePlayerGameApp(display: Display,
                                textMetrics: TextMetrics,
                                focusManager: FocusManager,
                                imageLoader: ImageLoader,
                                appScope: CoroutineScope,
                                theme: RoutleTheme) : Application {
    init {
        // Specification for user
        val spec = Label("Loading").apply {
            foregroundColor = theme.rootForegroundColor
            behavior = CommonLabelBehavior(textMetrics)
            font = theme.mediumFont
        }
        display += spec

        // On win
        val winMessage = Label("You win!").apply {
            foregroundColor = theme.rootForegroundColor
            font = theme.largeFont
            behavior = CommonLabelBehavior(textMetrics)
        }
        fun onWin() {
            display.children.clear()
            display += winMessage
        }

        val map = MapView(display.height / 5 * 3, imageLoader, appScope, theme)
        // Set the RoutleMap
        appScope.launch {
            map.map = getMap()
        }
        display += map

        // Info label below map
        val info = Label("Loading").apply {
            foregroundColor = theme.rootForegroundColor
            font = theme.xSmallFont
            behavior = CommonLabelBehavior(textMetrics)
        }
        display += info

        // Self-made input field
        val inputField = InputView(textMetrics, theme.rootForegroundColor) { input ->
            appScope.launch {
                // Get the city and draw it
                val status = map.drawCity(getCities(input))
                info.text = when(status.second) {
                    MapView.Status.Current -> "Already in ${status.first!!.name}"
                    MapView.Status.Ok -> status.first!!.name
                    MapView.Status.Far -> "${status.first!!.name} is to far!"
                    MapView.Status.Nope -> "$input ?"
                    MapView.Status.Win -> {
                        onWin()
                        "You win!"
                    }
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

        // Starting situation
        appScope.launch {
            var from = getRandomCity()
            while (from == null) {
                from = getRandomCity()
            }
            var to = getRandomCity()

            while (to == null || sqrt((to.relX - from.relX).pow(2) + (to.relY - from.relY).pow(2)) < 0.3) {
                to = getRandomCity()
            }

            map.currentCity = from
            map.endCity = to

            spec.text = "Get from ${from.name} to ${to.name}"
            info.text = from.name

            spec.rerender()
            info.rerender()
        }
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }
}