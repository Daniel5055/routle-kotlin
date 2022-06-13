package common

import io.nacular.doodle.core.Layout
import io.nacular.doodle.core.PositionableContainer
import io.nacular.doodle.layout.center
import io.nacular.doodle.utils.HorizontalAlignment
import io.nacular.doodle.utils.Orientation

open class StackedLayout(val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Center) : Layout {
    override fun layout(container: PositionableContainer) {
        var y = 0.0
        container.children.forEach { child ->
            child.x = when (horizontalAlignment) {
                HorizontalAlignment.Center -> (container.width - child.width) / 2
                HorizontalAlignment.Left -> 0.0
                HorizontalAlignment.Right -> container.width - child.width
            }
            child.y = y
            y += child.height + 5.0
        }
    }
}