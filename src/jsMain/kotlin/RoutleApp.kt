import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.nacular.doodle.application.Application
import io.nacular.doodle.drawing.TextMetrics
import io.nacular.doodle.core.*
import io.nacular.doodle.drawing.*
import io.nacular.doodle.focus.FocusManager
import io.nacular.doodle.image.ImageLoader
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow
import kotlin.math.sqrt

class RoutleApp(private val display: Display,
                textMetrics: TextMetrics,
                val fontsLoader: FontLoader,
                imageLoader: ImageLoader,
                focusManager: FocusManager)
    : Application, CoroutineScope {

    private var fontColour = Color(147u, 159u, 155u)

    private var largeFontQueue = mutableListOf<View>()
    private var normalFontQueue = mutableListOf<View>()
    private var largeFont : Font? = null
    private var normalFont : Font? = null

    init {
        launch {

            // Load Fonts
            // local fonts not appearing on chrome for some reason
            loadFont("Quicksand", (display.height / 25).toInt(), "/static/Quicksand-Regular.ttf")?.let { font ->
                normalFontQueue.forEach {
                    it.font = font
                    it.rerender()
                    normalFont = font
                }
            }

            loadFont("Quicksand", (display.height / 15).toInt())?.let { font ->
                largeFontQueue.forEach {
                    it.font = font
                    it.rerender()
                    largeFont = font
                }
            }
        }

        val winMessage = TextView(textMetrics, "You won!", fontColour)
        largeFontQueue.add(winMessage)

        val title = TextView(textMetrics, "Routle", fontColour)
        display += title
        largeFontQueue.add(title)

        val spec = object: TextView(textMetrics, "loading", fontColour) {
            fun setRouteCities(fromCity: City, toCity: City) {
                text = "Get from ${fromCity.name} to ${toCity.name}"
                rerender()
            }
        }
        display += spec
        normalFontQueue.add(spec)

        val map = MapView(display.height / 2) {
            display.children.clear()
            display += winMessage
        }
        display += map

        val inputField = InputView(textMetrics, fontColour) {input ->
            launch {
                // Get the city and draw it
                val cities = getCities(input)
                if (cities.isNotEmpty()) {
                    map.drawCity(cities)
                }
            }
        }
        display += inputField
        largeFontQueue.add(inputField)
        focusManager.requestFocus(inputField)

        launch {
            var from = getRandomCity()
            while (from == null) {
                from = getRandomCity()
            }
            var to = getRandomCity()

            while (to == null || sqrt((to.relX - from.relX).pow(2) + (to.relY - from.relY).pow(2)) < 0.3) {
                to = getRandomCity()
            }

            spec.setRouteCities(from, to)
            map.currentCity = from
            map.endCity = to
        }

        // Set the image
        launch {
            imageLoader.load(RoutleMap.uk.url)?.let {
                map.mapImage = it
            }
        }

        // Layout managing
        display.layout = object: Layout {
            override fun layout(container: PositionableContainer) {
                var y = 0.0
                container.children.forEach { child ->
                    child.x = (container.width - child.width) / 2
                    child.y = y
                    y += child.height + 5.0
                }
            }
        }
    }

    override fun shutdown() {
    }

    override val coroutineContext: CoroutineContext
        get() = Job()

    // Loading fonts
    private suspend fun loadFont(fontName: String, fontSize: Int, urlToFont: String? = null): Font? {

        val fontInfo: FontInfo.() -> Unit = {
            size = fontSize
            families = listOf(fontName, "sans-serif")
        }

        return if (urlToFont == null) {
            fontsLoader(fontInfo)
        } else {
            fontsLoader(urlToFont, fontInfo)
        }
    }
}