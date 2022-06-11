package core

import common.RoutleTheme
import api.goToSingleplayerMenu
import io.nacular.doodle.controls.buttons.PushButton
import io.nacular.doodle.controls.text.Label
import io.nacular.doodle.controls.theme.CommonLabelBehavior
import io.nacular.doodle.core.*
import io.nacular.doodle.drawing.*
import io.nacular.doodle.geometry.Point

class NavbarView(textMetrics: TextMetrics, theme: RoutleTheme) : Container() {
    init {
        backgroundColor = theme.navbarBackgroundColor
        foregroundColor = theme.navbarForegroundColor

        val title = Label("Routle").apply {
            foregroundColor = theme.navbarForegroundColor
            font = theme.largeFont

            behavior = CommonLabelBehavior(textMetrics)
        }
        children += title

        val singleplayer = PushButton("Singleplayer").apply {
            foregroundColor = theme.navbarForegroundColor
            font = theme.smallFont

            fired += {
                goToSingleplayerMenu()
            }

            size = textMetrics.size(text, font)
            behavior = NavbarButtonBehavior()
            acceptsThemes = false
        }
        children += singleplayer

        layout = object: Layout {
            override fun layout(container: PositionableContainer) {
                title.x = (container.width - title.width) / 2
                title.y = container.height - title.height

                singleplayer.x = (title.x - singleplayer.width) / 2
                singleplayer.y = container.height - singleplayer.height - 5.0
            }
        }
    }

    override fun render(canvas: Canvas) {
        canvas.rect(bounds.atOrigin, backgroundColor!!.paint)
        canvas.line(Point(0.0, height), Point(width, height), Stroke(foregroundColor!!.paint, 2.0))
    }
}

