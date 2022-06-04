import io.nacular.doodle.core.View
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.drawing.Color
import io.nacular.doodle.drawing.TextMetrics
import io.nacular.doodle.drawing.paint
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Rectangle

open class TextView(private val textMetrics: TextMetrics, var text: String, var color: Color) : View() {

    init {
        bounds = Rectangle(Point.Origin, textMetrics.size(text))
    }

    override fun render(canvas: Canvas) {
        size = textMetrics.size(text, font)
        canvas.text(text, font, Point.Origin, color.paint)
    }

}