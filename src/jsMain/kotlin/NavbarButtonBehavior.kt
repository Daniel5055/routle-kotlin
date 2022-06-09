import io.nacular.doodle.controls.buttons.Button
import io.nacular.doodle.controls.theme.CommonButtonBehavior
import io.nacular.doodle.controls.theme.CommonTextButtonBehavior
import io.nacular.doodle.drawing.*
import io.nacular.doodle.geometry.Point

class NavbarButtonBehavior() : CommonButtonBehavior<Button>() {

    override fun render(view: Button, canvas: Canvas) {
        // Render changes on button state change
        val textColor = view.run {
            when {
                model.selected || model.pressed && model.armed -> foregroundColor?.darker(0.5f)
                model.pointerOver || model.pressed -> foregroundColor?.darker(0.25f)
                else -> foregroundColor
            }
        }

        canvas.text(view.text,
            view.font,
            Point.Origin,
            textColor?.paint ?: Color.White.paint)
    }

}