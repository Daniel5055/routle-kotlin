package common

import io.nacular.doodle.core.Layout
import io.nacular.doodle.core.PositionableContainer

open class StackedLayout : Layout {
    override fun layout(container: PositionableContainer) {
        var y = 0.0
        container.children.forEach { child ->
            child.x = (container.width - child.width) / 2
            child.y = y
            y += child.height + 5.0
        }
    }
}