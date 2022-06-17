package core

import api.getClientScreenSize
import WebPage
import common.RoutleTheme
import common.SliderBehavior
import common.StackedLayout
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
import io.nacular.doodle.controls.range.Slider
import io.nacular.doodle.controls.text.Label
import io.nacular.doodle.controls.theme.CommonLabelBehavior
import io.nacular.doodle.event.PointerEvent
import io.nacular.doodle.event.PointerListener
import io.nacular.doodle.focus.FocusManager
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Size
import io.nacular.doodle.image.ImageLoader
import io.nacular.doodle.utils.HorizontalAlignment
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

    init {
        appScope.launch {

            // Load theme
            val theme = RoutleTheme(fontsLoader, getClientScreenSize().height).also { it.dark() }

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

            val themeSetting = object: Container() {
                init {
                    val text = Label("Theme (Beta)").apply {
                        foregroundColor = theme.rootForegroundColor
                        font = theme.smallFont
                        behavior = CommonLabelBehavior(textMetrics)
                    }
                    children += text
                    val slider = Slider(0..1).apply {
                        size = Size(text.width, 10.0)
                        foregroundColor = theme.rootForegroundColor
                        behavior = SliderBehavior()
                        value = 0

                        changed += {_,_,new ->
                            if (new == 0) {
                                appScope.launch {
                                    theme.dark()
                                }
                            }
                            else {
                                appScope.launch {
                                    theme.light()
                                }
                            }
                            display.children.forEach {
                                it.rerender()
                            }
                        }
                    }
                    children += slider
                    layout = StackedLayout(HorizontalAlignment.Left)
                    width = text.width
                    height = text.height + slider.height + 5.0
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
                    settings.y = display.height - settings.height - 30.0
                }
            }
        }
    }
    override fun shutdown() {
    }
}