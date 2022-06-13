package core

import api.getClientScreenSize
import common.DarkRoutleTheme
import WebPage
import io.nacular.doodle.animation.Animation
import io.nacular.doodle.animation.Animator
import io.nacular.doodle.animation.NoneUnit
import io.nacular.doodle.animation.speedUpSlowDown
import io.nacular.doodle.animation.transition.Transition
import io.nacular.doodle.application.Application
import io.nacular.doodle.application.ApplicationViewFactory
import io.nacular.doodle.application.Modules.Companion.FocusModule
import io.nacular.doodle.application.Modules.Companion.ImageModule
import io.nacular.doodle.application.Modules.Companion.KeyboardModule
import io.nacular.doodle.drawing.TextMetrics
import io.nacular.doodle.core.*
import io.nacular.doodle.drawing.*
import kotlinx.coroutines.*
import org.kodein.di.instance
import io.nacular.doodle.application.Modules.Companion.PointerModule
import io.nacular.doodle.controls.Photo
import io.nacular.doodle.event.PointerEvent
import io.nacular.doodle.event.PointerListener
import io.nacular.doodle.focus.FocusManager
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Size
import io.nacular.doodle.image.ImageLoader
import io.nacular.doodle.utils.observable
import io.nacular.measured.units.Angle.Companion.degrees
import io.nacular.measured.units.Angle.Companion.radians
import io.nacular.measured.units.Measure
import io.nacular.measured.units.Time
import io.nacular.measured.units.Time.Companion.milliseconds
import io.nacular.measured.units.Time.Companion.seconds
import io.nacular.measured.units.times
import singleplayer.RoutleSinglePlayerApp
import singleplayer.RoutleSinglePlayerGameApp

class RoutleApp(display: Display,
                textMetrics: TextMetrics,
                fontsLoader: FontLoader,
                imageLoader: ImageLoader,
                focusManager: FocusManager,
                animator: Animator,
                appScope: CoroutineScope,
                appView: ApplicationViewFactory,
                webPage: WebPage?)
    : Application {

    private var animation: Animation? by observable(null) { old, _ ->
        old?.cancel()
    }

    private var progress: Float = 0.0f
    init {
        appScope.launch {

            // Load theme
            val theme = DarkRoutleTheme(fontsLoader, getClientScreenSize().height)

            // Navbar
            val navbar = NavbarView(textMetrics, theme)
            display += navbar

            // Settings
            val settings = SettingsView(animator, theme)
            appScope.launch {
                imageLoader.load("/static/settings.png")?.let { image ->
                    settings.image = image
                }
            }

            // Determining the webpage to run
            val root = when (webPage) {
                WebPage.singleplayerMenu -> {
                    appView(modules = listOf(PointerModule)) {
                        RoutleSinglePlayerApp(
                            display = instance(),
                            textMetrics = instance(),
                            appScope = appScope,
                            settings = settings,
                            theme = theme)
                    }
                }
                WebPage.singleplayerGame -> {
                    appView(modules = listOf(PointerModule, ImageModule, FocusModule, KeyboardModule)) {
                        RoutleSinglePlayerGameApp(
                            display = instance(),
                            textMetrics = instance(),
                            focusManager = focusManager,
                            imageLoader = instance(),
                            appScope = appScope,
                            settings = settings,
                            theme = theme
                        )
                    }
                }
                else -> appView(modules = listOf(PointerModule)) {
                    RoutleSinglePlayerApp(
                        display = instance(),
                        textMetrics = instance(),
                        appScope = appScope,
                        settings = settings,
                        theme = theme)
                }
            }
            display += root
            display += settings


            // Layout managing
            display.layout = object : Layout {
                override fun layout(container: PositionableContainer) {
                    // Readjust navbar
                    navbar.width = display.width
                    navbar.height = getClientScreenSize().height / 15

                    // Root container
                    root.y = navbar.height
                    root.width = display.width
                    root.height = display.height - navbar.height
                    root.x = (display.width - root.width) / 2

                    settings.x = display.width - settings.width - 30.0
                    settings.y = display.height - settings.width - 30.0
                }
            }
        }
    }
    override fun shutdown() {
    }
}