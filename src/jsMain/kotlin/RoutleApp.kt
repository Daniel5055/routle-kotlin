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
import kotlin.coroutines.CoroutineContext
import io.nacular.doodle.application.Modules.Companion.PointerModule
import io.nacular.doodle.focus.FocusManager

class RoutleApp(val display: Display, textMetrics: TextMetrics, val fontsLoader: FontLoader, focusManager: FocusManager, appView: ApplicationViewFactory, webPage: WebPage?)
    : Application, CoroutineScope {

    init {
        launch {
            // Load theme
            val theme = DarkRoutleTheme(fontsLoader, display.height)

            // navbar
            val navbar = NavbarView(textMetrics, theme)
            display += navbar

            // Determining the webpage to run
            // TODO("Move appview into when statement to customise modules choice)
            val root = appView(modules = listOf(PointerModule, ImageModule, FocusModule, KeyboardModule)) {
                when (webPage) {
                    WebPage.singleplayerMenu -> RoutleSinglePlayerApp(display = instance(), textMetrics = instance(), theme = theme)
                    WebPage.singleplayerGame -> RoutleSinglePlayerGameApp(display = instance(), textMetrics = instance(), focusManager = focusManager, imageLoader = instance(), theme = theme)
                    else -> RoutleSinglePlayerApp(display = instance(), textMetrics = instance(), theme = theme)
                }
            }
            display += root

            // Layout managing
            display.layout = object : Layout {
                override fun layout(container: PositionableContainer) {
                    // Readjust navbar
                    navbar.width = display.width
                    navbar.height = 70.0

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

    override val coroutineContext: CoroutineContext
        get() = Job()
}