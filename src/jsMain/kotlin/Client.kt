import io.nacular.doodle.application.ApplicationViewFactory
import io.nacular.doodle.application.Modules.Companion.FontModule
import io.nacular.doodle.application.Modules.Companion.ImageModule
import io.nacular.doodle.application.Modules.Companion.KeyboardModule
import io.nacular.doodle.application.Modules.Companion.PointerModule
import io.nacular.doodle.application.application
import kotlinx.browser.document
import org.kodein.di.instance

fun main() {
    document.bgColor = "#202427"

    var webPage: WebPage? = null
    if (document.getElementById(WebPage.index.id) != null) {
        webPage = WebPage.index
    }
    else if (document.getElementById(WebPage.singleplayerMenu.id) != null) {
        webPage = WebPage.singleplayerMenu
    }
    else if (document.getElementById(WebPage.singleplayerGame.id) != null) {
        webPage = WebPage.singleplayerGame
    }

    application(modules = (listOf(FontModule, ImageModule, KeyboardModule, PointerModule, ApplicationViewFactory.AppViewModule))) {
        RoutleApp(display = instance(),
            textMetrics = instance(),
            fontsLoader = instance(),
            focusManager = instance(),
            appView = instance(),
            webPage = webPage
        )

    }

}
