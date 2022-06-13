package singleplayer

import core.NavbarButtonBehavior
import common.RoutleTheme
import common.StackedLayout
import api.getMaps
import api.goToSingleplayerMap
import common.CenteredLayout
import common.SliderBehavior
import core.SettingsView
import io.nacular.doodle.application.Application
import io.nacular.doodle.controls.buttons.PushButton
import io.nacular.doodle.controls.range.Slider
import io.nacular.doodle.controls.text.Label
import io.nacular.doodle.controls.theme.CommonLabelBehavior
import io.nacular.doodle.core.Display
import io.nacular.doodle.drawing.TextMetrics
import io.nacular.doodle.core.*
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.drawing.paint
import io.nacular.doodle.geometry.Size
import io.nacular.doodle.theme.basic.range.BasicSliderBehavior
import io.nacular.doodle.theme.basic.range.TickLocation
import io.nacular.doodle.theme.basic.range.TickPresentation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.math.round

class RoutleSinglePlayerApp(display: Display, textMetrics: TextMetrics, appScope: CoroutineScope, settings: SettingsView, theme: RoutleTheme) : Application {

    var mapDifficulty = 1.0

    init {
        val whereContainer = object: Container() {
            init {
                children += Label("Where to?").apply {
                    foregroundColor = theme.rootForegroundColor
                    behavior = CommonLabelBehavior(textMetrics)
                    font = theme.mediumFont
                }

                appScope.launch {
                    getMaps().sortedBy { it.name }.forEach { map ->
                        children += PushButton(map.name).apply {
                            foregroundColor = theme.rootForegroundColor
                            font = theme.smallFont

                            fired += {
                                goToSingleplayerMap(map, mapDifficulty)
                            }

                            size = textMetrics.size(text, font)
                            behavior = NavbarButtonBehavior()
                        }
                    }

                    children.forEach {
                        if (it.width > width) {
                            width = it.width
                        }

                        height += it.height
                        height += 5.0
                    }
                }



                layout = StackedLayout()
            }
        }
        display += whereContainer

        val difficultyContainer = object: Container() {
            init {
                children += Label("Difficulty").apply {
                    foregroundColor = theme.rootForegroundColor
                    behavior = CommonLabelBehavior(textMetrics)
                    font = theme.mediumFont
                }
                val difficulty = Label("Normal").apply {
                    foregroundColor = theme.rootForegroundColor
                    behavior = CommonLabelBehavior(textMetrics)
                    font = theme.smallFont
                }
                children += difficulty

                children += Slider(1..5).apply {
                    size = Size(200, 15)
                    ticks = 5
                    snapToTicks = true
                    foregroundColor = theme.rootForegroundColor
                    behavior = SliderBehavior()
                    value = 3

                    changed += {_,_,new  ->
                        difficulty.text = when (new) {
                            5 -> {mapDifficulty = 0.6; "Fredrik mode" }
                            4 -> {mapDifficulty = 0.8; "Hard"}
                            3 -> {mapDifficulty = 1.0; "Normal"}
                            2 -> {mapDifficulty = 2.0; "Easy"}
                            1 -> {mapDifficulty = 4.0; "Creative mode"}
                            else -> "??"
                        }
                    }
                }

                children.forEach {
                    if (it.width > width) {
                        width = it.width
                    }

                    height += it.height
                }
                height += 5.0
                        height += 5
                layout = StackedLayout()
            }

        }
        display += difficultyContainer

        display.layout = object: CenteredLayout() {
            override fun layout(container: PositionableContainer) {
                super.layout(container)
                whereContainer.y = 10.0
                difficultyContainer.y = whereContainer.height + 50.0
            }
        }
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }
}