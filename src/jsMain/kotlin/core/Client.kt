package core

import WebPage
import io.nacular.doodle.animation.Animator
import io.nacular.doodle.animation.impl.AnimatorImpl
import io.nacular.doodle.application.ApplicationViewFactory
import io.nacular.doodle.application.Modules.Companion.FontModule
import io.nacular.doodle.application.Modules.Companion.ImageModule
import io.nacular.doodle.application.Modules.Companion.KeyboardModule
import io.nacular.doodle.application.Modules.Companion.PointerModule
import io.nacular.doodle.application.application
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.DI
import org.kodein.di.bindInstance
import org.kodein.di.bindSingleton
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

    application(modules = (listOf(FontModule,
        ImageModule,
        KeyboardModule,
        PointerModule,
        ApplicationViewFactory.AppViewModule,
        DI.Module(name = "AnimationModules") {
            bindSingleton<Animator> { AnimatorImpl(instance(), instance()) }
        }))) {
        RoutleApp(
            display = instance(),
            textMetrics = instance(),
            fontsLoader = instance(),
            imageLoader = instance(),
            focusManager = instance(),
            animator = instance(),
            appView = instance(),
            appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            webPage = webPage
        )

    }

}
