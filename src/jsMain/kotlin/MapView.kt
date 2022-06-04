import io.nacular.doodle.core.*
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.drawing.Color
import io.nacular.doodle.drawing.Stroke
import io.nacular.doodle.drawing.paint
import io.nacular.doodle.geometry.Circle
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Rectangle
import io.nacular.doodle.image.Image
import io.nacular.doodle.image.height
import io.nacular.doodle.image.width
import kotlin.math.pow
import kotlin.math.sqrt

class MapView(var startHeight: Double, var onWin: () -> Unit) : View() {

    var endCityColour = Color(166u, 242u, 165u)
    var farCityColour = Color(224u, 161u, 161u)
    var pastCityColour = Color(127u, 141u, 137u)

    private val circleRadius = 2.0
    private var searchRadius = 1.0
    private var aspectRatio = 0.0

    private var farCities by renderProperty(mutableListOf<City>())
    private var pastCities by renderProperty(mutableListOf<City>())
    private var routes by renderProperty(mutableListOf<Pair<Point, Point>>())
    var currentCity by renderProperty(City.nullCity)
    var endCity by renderProperty(City.nullCity)

    public override var layout : Layout? = null

    var mapImage: Image? = null
        set(value) {
            if (value != null)
            {
                field = value
                aspectRatio = value.height / value.width
                height = startHeight
                searchRadius = height / 7
                width = height / aspectRatio
                rerender()
            }
        }


    init {
        width = 500.0

        this.clipCanvasToBounds = false
    }

    override fun render(canvas: Canvas) {
        if (mapImage != null) {
            canvas.image(mapImage!!, Rectangle(Point.Origin, bounds.size))
            canvas.circle(Circle(relToAbs(currentCity.relX, currentCity.relY), circleRadius), pastCityColour.paint)
            canvas.circle(Circle(relToAbs(endCity.relX, endCity.relY), circleRadius), endCityColour.paint)

            routes.forEach {
                canvas.line(it.first, it.second, Stroke(pastCityColour))
            }
            pastCities.forEach {
                canvas.circle(Circle(relToAbs(it.relX, it.relY), circleRadius), pastCityColour.paint)
            }

            farCities.forEach {
                canvas.circle(Circle(relToAbs(it.relX, it.relY), circleRadius), farCityColour.paint)
            }



            canvas.circle(Circle(relToAbs(currentCity.relX, currentCity.relY), searchRadius), Stroke(pastCityColour.paint))
        }
    }

    fun changeHeight(value: Double) {
        height += value
        width = height / aspectRatio
        searchRadius = height / 7
        rerender()
    }

    // Returns false if not city at all drawn
    fun drawCity(cities: List<City>): Boolean {
        // Iterate through cities and find the closest city not equal to current city
        var closestCity : City? = null
        var shortestDistance = 1000000.0
        var endCityDistance : Double? = null
        cities.forEach { city ->
            // Skip if current city
            if (city != currentCity) {

                val distance = sqrt(((city.relX - currentCity.relX) * width).pow(2) +
                        ((city.relY - currentCity.relY) * height).pow(2))

                if (city == endCity) {
                    endCityDistance = distance
                }

                if (distance < shortestDistance) {
                    closestCity = city
                    shortestDistance = distance
                }
            }
        }

        if (closestCity == null) {
            return false
        }
        // if endcity within radius
        if (endCityDistance != null && endCityDistance!! <= searchRadius) {
            onWin()
            return true
        }

        // if within radius
        if (shortestDistance <= searchRadius) {
            farCities.clear()
            routes.add(Pair(Point(currentCity.relX * width, currentCity.relY * height),
                Point(closestCity!!.relX * width, closestCity!!.relY * height)))
            pastCities.add(currentCity)
            currentCity = closestCity!!
            rerender()
        }
        else {
            farCities.add(closestCity!!)
            rerender()
        }

        return true
    }

    private fun relToAbs(relX: Double, relY: Double): Point {
        return Point(relX * width, relY * height)
    }
}