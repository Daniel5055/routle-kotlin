package core

import api.getClientScreenSize
import common.DarkRoutleTheme
import WebPage
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
import io.nacular.doodle.focus.FocusManager
import singleplayer.RoutleSinglePlayerApp
import singleplayer.RoutleSinglePlayerGameApp

class RoutleApp(display: Display,
                textMetrics: TextMetrics,
                fontsLoader: FontLoader,
                focusManager: FocusManager,
                appScope: CoroutineScope,
                appView: ApplicationViewFactory,
                webPage: WebPage?)
    : Application {

    init {
        appScope.launch {

            // Load theme
            val theme = DarkRoutleTheme(fontsLoader, getClientScreenSize().height)

            // Navbar
            val navbar = NavbarView(textMetrics, theme)
            display += navbar

            // Determining the webpage to run
            val root = when (webPage) {
                WebPage.singleplayerMenu -> {
                    appView(modules = listOf(PointerModule)) {
                        RoutleSinglePlayerApp(
                            display = instance(),
                            textMetrics = instance(),
                            appScope = appScope,
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
                            theme = theme
                        )
                    }
                }
                else -> appView(modules = listOf(PointerModule)) {
                    RoutleSinglePlayerApp(
                        display = instance(),
                        textMetrics = instance(),
                        appScope = appScope,
                        theme = theme)
                }
            }
            display += root

            // Layout managing
            display.layout = object : Layout {
                override fun layout(container: PositionableContainer) {
                    // Readjust navbar
                    navbar.width = display.width
                    navbar.height = getClientScreenSize().height / 15

                    // Root container
                    root.y = navbar.height + 10.0
                    root.width = display.width
                    root.height = display.height - navbar.height
                    root.x = (display.width - root.width) / 2
                }
            }
        }
    }
    override fun shutdown() {
    }
}