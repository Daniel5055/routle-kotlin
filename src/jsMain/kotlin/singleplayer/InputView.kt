package singleplayer

import io.nacular.doodle.core.View
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.drawing.Color
import io.nacular.doodle.drawing.TextMetrics
import io.nacular.doodle.drawing.paint
import io.nacular.doodle.event.KeyText.Companion.Enter
import io.nacular.doodle.event.KeyEvent
import io.nacular.doodle.event.KeyListener
import io.nacular.doodle.event.KeyText.Companion.Backspace
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Size

class InputView(private val textMetrics: TextMetrics, private val colour: Color, private val onEnter: (String) -> Unit) : View() {

    private var inputText = ""
        set(value) {
            field = value
            rerender()
        }

    var keyListener = object: KeyListener {
        override fun pressed(event: KeyEvent) {
            when (event.key) {
                Enter -> {
                    onEnter(inputText)
                    inputText = ""
                }

                Backspace -> {
                    inputText = inputText.dropLast(1)
                    width = textMetrics.width(inputText, font)
                }

                else -> {
                    if (event.key.text.length == 1) {
                        inputText += event.key.text
                        width = textMetrics.width(inputText, font)
                        rerender()
                    }
                }
            }
            event.consume()
        }
    }

    init {
        size = Size(500, 80)
        keyChanged += keyListener
    }

    override fun render(canvas: Canvas) {
        canvas.text(inputText, font, Point.Origin, colour.paint)
    }
}