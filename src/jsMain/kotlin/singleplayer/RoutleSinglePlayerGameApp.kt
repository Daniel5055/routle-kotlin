package singleplayer

import api.*
import common.RoutleTheme
import common.SliderBehavior
import common.StackedLayout
import core.NavbarButtonBehavior
import core.SettingsView
import io.nacular.doodle.application.Application
import io.nacular.doodle.controls.buttons.Button
import io.nacular.doodle.controls.buttons.PushButton
import io.nacular.doodle.controls.range.Slider
import io.nacular.doodle.controls.text.Label
import io.nacular.doodle.controls.theme.CommonLabelBehavior
import io.nacular.doodle.core.Display
import io.nacular.doodle.drawing.TextMetrics
import io.nacular.doodle.core.*
import io.nacular.doodle.event.PointerEvent
import io.nacular.doodle.event.PointerListener
import io.nacular.doodle.focus.FocusManager
import io.nacular.doodle.geometry.Size
import io.nacular.doodle.image.ImageLoader
import io.nacular.doodle.utils.HorizontalAlignment
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
                                settings: SettingsView,
                                theme: RoutleTheme) : Application {
    init {



        // Specification for user
        val spec = Label("Loading").apply {
            foregroundColor = theme.rootForegroundColor
            behavior = CommonLabelBehavior(textMetrics)
            font = theme.mediumFont
        }
        display += spec
        val winMessage = Label("Number of Cities: ").apply {
            foregroundColor = theme.rootForegroundColor
            font = theme.mediumFont
            behavior = CommonLabelBehavior(textMetrics)
        }
        // On win declaration
        var onWin = {}

        val map = MapView(display.height / 5 * 3, getMapDifficulty(), imageLoader, appScope, theme)
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

        // OnWin implementation
        onWin = {
            display -= inputField
            inputField.keyChanged -= inputField.keyListener
            display += winMessage.apply { text += map.cityCount }
            display += PushButton("Play again?").apply {
                foregroundColor = theme.rootForegroundColor
                font = theme.smallFont
                size = textMetrics.size(text, font)
                behavior = NavbarButtonBehavior()

                fired += {
                    reloadPage()
                }
            }
        }

        display.layout = object: Layout {
            override fun layout(container: PositionableContainer) {
                var y = 10.0
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

        // Add to settings
        val difficultySetting = object: Container() {
            init {
                val text = Label("Difficulty").apply {
                    foregroundColor = theme.rootForegroundColor
                    font = theme.smallFont
                    behavior = CommonLabelBehavior(textMetrics)
                }
                children += text
                val slider = Slider(0.25..2.0).apply {
                    size = Size(text.width, 10.0)
                    foregroundColor = theme.rootForegroundColor
                    behavior = SliderBehavior()
                    value = 1.125

                    changed += {_,old,new  ->
                        map.searchRadius /= 1 / old
                        map.searchRadius *= 1 / new
                    }
                }
                children += slider
                layout = StackedLayout(HorizontalAlignment.Left)
                width = text.width
                height = text.height + slider.height + 5.0
            }
        }
        settings.addSetting(difficultySetting)
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }
}