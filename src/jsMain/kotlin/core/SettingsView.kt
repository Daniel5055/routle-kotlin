package core

import common.RoutleTheme
import io.nacular.doodle.animation.Animation
import io.nacular.doodle.animation.Animator
import io.nacular.doodle.animation.speedUpSlowDown
import io.nacular.doodle.controls.Photo
import io.nacular.doodle.core.Container
import io.nacular.doodle.core.Layout
import io.nacular.doodle.core.PositionableContainer
import io.nacular.doodle.core.View
import io.nacular.doodle.drawing.AffineTransform
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.drawing.Stroke
import io.nacular.doodle.event.PointerEvent
import io.nacular.doodle.event.PointerListener
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Rectangle
import io.nacular.doodle.geometry.Size
import io.nacular.doodle.image.Image
import io.nacular.doodle.utils.observable
import io.nacular.measured.units.Angle
import io.nacular.measured.units.Time
import io.nacular.measured.units.times

class SettingsView(private val animator: Animator, private val theme: RoutleTheme) : Container() {


    // The main views of the container
    private var photo: Photo? = null
    private var settings = object: View() {
        init {
            width = 80.0
            height = 80.0

            // Start at bottom of screen
            y = height

            layout = object: Layout {
                override fun layout(container: PositionableContainer) {
                    var y = 0.0
                    container.children.forEach {
                        it.y = y
                        y += it.height
                        it.x = 10.0
                    }
                }
            }
        }

        fun addSetting(container: Container) {
            children += container
            container.x = 5.0
            if (container.width + container.x * 2 > width) {
                width = container.width + container.x * 2.0
            }

            height += container.height + 5.0
            y = height
        }

        override fun render(canvas: Canvas) {
            canvas.line(Point.Origin, Point(0.0, height), Stroke(theme.rootForegroundColor))
        }
    }

    // When the image is set, initialise the photo with it and add to display
    var image: Image? = null
        set(value) {
            field = value
            if (value != null) {
                photo = Photo(image!!).apply {
                    size = Size(50.0, 50.0)

                    pointerChanged += object: PointerListener {
                        override fun clicked(event: PointerEvent) {
                            isOpened = if (isOpened) {
                                doAnimation(progress, 0.0f)
                                false
                            } else {
                                doAnimation(progress, 1.0f)
                                true
                            }
                        }
                    }
                }
                children += photo!!
            }
        }

    // The progress of the animation
    private var progress = 0.0f

    // The animation
    private var animation: Animation? by observable(null) { old, _ ->
        old?.cancel()
    }

    // Whether the animation has been started in one direction or another
    private var isOpened = false

    init {
        children += settings
        width = settings.width
        height = 100.0
        layout = object: Layout {
            override fun layout(container: PositionableContainer) {
                photo?.x = width - photo!!.width
                photo?.y = height - photo!!.height

                bounds = settings.bounds
            }

        }
    }

    fun addSetting(container: Container) {
        settings.addSetting(container)
    }

    private fun doAnimation(start: Float, end: Float ) {
        animation = (animator(start to end) using speedUpSlowDown(1 * Time.seconds)) {
            photo?.transform = AffineTransform().rotate(Point(photo!!.x + photo!!.width / 2, photo!!.y+photo!!.height / 2), 180.0 * it * Angle.degrees)
            settings.y = height - it * height
            progress = it
        }.apply {
            completed += {
                animation = null
            }
        }
    }
}