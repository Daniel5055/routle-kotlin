package singleplayer

import City
import RoutleMap
import common.RoutleTheme
import io.nacular.doodle.core.*
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.drawing.Color
import io.nacular.doodle.drawing.Stroke
import io.nacular.doodle.drawing.paint
import io.nacular.doodle.geometry.Circle
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.geometry.Rectangle
import io.nacular.doodle.image.Image
import io.nacular.doodle.image.ImageLoader
import io.nacular.doodle.image.height
import io.nacular.doodle.image.width
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt

class MapView(private var startHeight: Double, searchRadius: Double, private val imageLoader: ImageLoader, private val appScope: CoroutineScope, private val theme: RoutleTheme) : View() {
    private var circleRadius = 1.0
    private var aspectRatio = 0.0

    var searchRadius by renderProperty(searchRadius)

    private var mapImage: Image? = null
        set(value) {
            if (value != null) {
                field = value

                // Set size and ratio
                aspectRatio = value.height / value.width
                height = startHeight
                width = height / aspectRatio

                // Set search radius
                searchRadius *= height / 8
                circleRadius *= height / 210
                rerender()
            }
        }

    var map: RoutleMap? = null
        set(value) {
            field = value

            if (value != null) {
                // Load the map
                appScope.launch {
                    mapImage = imageLoader.load(value.path)
                }
                // Adjust search radius
                searchRadius *= map!!.searchRadius
                //circleRadius *= map!!.searchRadius
            }
        }

    private var farCities by renderProperty(mutableListOf<City>())
    private var pastCities by renderProperty(mutableListOf<City>())
    private var routes by renderProperty(mutableListOf<Pair<Point, Point>>())
    var currentCity by renderProperty(City.nullCity)
    var endCity by renderProperty(City.nullCity)

    var cityCount = 0

    init {
        width = 500.0
        this.clipCanvasToBounds = false
    }

    override fun render(canvas: Canvas) {
        if (mapImage != null) {
            // Draw map
            canvas.image(mapImage!!, Rectangle(Point.Origin, bounds.size))

            // Draw current city
            canvas.circle(Circle(relToAbs(currentCity.relX, currentCity.relY), circleRadius), theme.grayColor.paint)

            // Draw the routes, past cities, and far cities
            routes.forEach {
                canvas.line(it.first, it.second, Stroke(theme.grayColor))
            }
            pastCities.forEach {
                canvas.circle(Circle(relToAbs(it.relX, it.relY), circleRadius), theme.grayColor.paint)
            }
            farCities.forEach {
                canvas.circle(Circle(relToAbs(it.relX, it.relY), circleRadius), theme.redColor.paint)
            }

            // Draw the search circle
            canvas.circle(Circle(relToAbs(currentCity.relX, currentCity.relY), searchRadius), Stroke(theme.grayColor.paint))

            // Draw the end city
            canvas.circle(Circle(relToAbs(endCity.relX, endCity.relY), circleRadius), theme.greenColor.paint)
        }
    }

    enum class Status {
        Ok,
        Far,
        Nope,
        Win,
        Current,
    }

    // Returns false if not city at all drawn
    fun drawCity(cities: List<City>): Pair<City?, Status> {
        // Iterate through cities and find the closest city not equal to current city
        if (cities.isEmpty()) {
            return Pair(null, Status.Nope)
        }
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
            return Pair(currentCity, Status.Current)
        }

        // if endcity within radius
        if (endCityDistance != null && endCityDistance!! <= searchRadius) {
            farCities.clear()
            routes.add(Pair(Point(currentCity.relX * width, currentCity.relY * height),
                Point(endCity.relX * width, endCity.relY * height)))
            pastCities.add(currentCity)
            currentCity = endCity
            rerender()

            cityCount++

            return Pair(endCity, Status.Win)
        }

        // if within radius
        if (shortestDistance <= searchRadius) {
            farCities.clear()
            routes.add(Pair(Point(currentCity.relX * width, currentCity.relY * height),
                Point(closestCity!!.relX * width, closestCity!!.relY * height)))
            pastCities.add(currentCity)
            currentCity = closestCity!!
            rerender()

            cityCount++
        }
        else {
            farCities.add(closestCity!!)
            rerender()
            return Pair(closestCity!!, Status.Far)
        }

        return Pair(closestCity!!, Status.Ok)
    }

    private fun relToAbs(relX: Double, relY: Double): Point {
        return Point(relX * width, relY * height)
    }
}