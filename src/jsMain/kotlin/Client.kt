import io.nacular.doodle.application.Modules.Companion.FontModule
import io.nacular.doodle.application.Modules.Companion.ImageModule
import io.nacular.doodle.application.Modules.Companion.KeyboardModule
import io.nacular.doodle.application.application
import kotlinx.browser.document
import org.kodein.di.instance

fun main() {
    document.bgColor = "#202427"
    application(modules = listOf(FontModule, ImageModule, KeyboardModule)) {
        RoutleApp(display = instance(),
            textMetrics = instance(),
            fontsLoader = instance(),
            imageLoader = instance(),
            focusManager = instance())
    }
}
