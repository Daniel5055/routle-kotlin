package common

import io.nacular.doodle.controls.range.Slider
import io.nacular.doodle.controls.theme.range.AbstractSliderBehavior
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.drawing.Color
import io.nacular.doodle.drawing.Stroke
import io.nacular.doodle.drawing.paint
import io.nacular.doodle.event.PointerEvent
import io.nacular.doodle.geometry.Circle
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Rectangle
import kotlin.math.min

class SliderBehavior<T>
    : AbstractSliderBehavior<T>(null) where T: Number, T: Comparable<T> {

    override fun render(view: Slider<T>, canvas: Canvas) {
        // Always horizontal (for now)
        val handleSize = handleSize(view)
        val handleRect = Rectangle(handlePosition(view), 0.0, handleSize, handleSize)

        canvas.line(Point(handleSize / 2.0, view.height / 2.0),
            Point(view.width - handleSize / 2.0, view.height / 2.0),
            Stroke(view.foregroundColor?.paint ?: Color.White.paint))

        canvas.circle(Circle(handleRect.center, min(handleRect.width, handleRect.height) / 2), view.foregroundColor?.paint ?: Color.White.paint)
    }
}