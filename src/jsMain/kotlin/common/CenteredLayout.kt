package common

import io.nacular.doodle.core.Layout
import io.nacular.doodle.core.PositionableContainer

// Layout where all children are centered
abstract class CenteredLayout : Layout{
    override fun layout(container: PositionableContainer) {
        container.children.forEach { child ->
            child.x = (container.width - child.width) / 2
        }
    }
}