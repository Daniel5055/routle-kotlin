package me.dan.application

import City
import RoutleMap
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import kotlin.math.ln
import kotlin.math.tan

class CityEngine(private var map: RoutleMap) {

    private var connection : Connection? = null

    init {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:db/cities.db")
        }
        catch (e: SQLException) {
            println(e.message)
        }

    }

    fun getCities(name: String) : List<City> {
        val cities = mutableListOf<City>()

        try {
            val statement = connection!!.prepareStatement("SELECT AsciiName, Lat, Long FROM cities WHERE ( CC = 'GB' OR CC = 'IE' OR CC2 = 'GB' )" +
                    "AND ( LOWER(RealName) = LOWER(?) OR LOWER(AsciiName) = LOWER(?) OR LOWER(AltNames) LIKE '%:' || ? || ':%' )")
            statement.setString(1, name)
            statement.setString(2, name)
            statement.setString(3, name)

            val coords = statement.executeQuery()


            while (coords.next())
            {
                // Quick function to turn latitude into linear y coord
                val latToY = { lat: Double ->
                    ln(tan(Math.PI/4 + lat * Math.PI / 360))
                }

                val relY = (latToY(map.lat_max) - latToY(coords.getDouble(2))) /
                        (latToY(map.lat_max) - latToY(map.lat_min))

                val relX = (coords.getDouble(3) - map.long_min) / (map.long_max - map.long_min)

                // If within bounds
                if (relX >= 0 || relX <= 1 || relY >= 0 || relY <= 1 ) {
                    cities.add(City(coords.getString(1), relX, relY))
                }
            }
        }
        catch(e: SQLException) {
            print(e.message)
        }

        println(cities.toString())
        return cities
    }

    fun getRandomCity(): City {
        try {
            val statement = connection!!.prepareStatement("SELECT AsciiName, Lat, Long FROM cities WHERE ( CC = 'GB' ) ORDER BY random() LIMIT 1")

            // Until good random gotten
            while (true)
            {
                val coords = statement.executeQuery()

                // Quick function to turn latitude into linear y coord
                val latToY = { lat: Double ->
                    ln(tan(Math.PI/4 + lat * Math.PI / 360))
                }

                val relY = (latToY(map.lat_max) - latToY(coords.getDouble(2))) /
                        (latToY(map.lat_max) - latToY(map.lat_min))

                val relX = (coords.getDouble(3) - map.long_min) / (map.long_max - map.long_min)

                // If within range
                if (relX >= 0 || relX <= 1 || relY >= 0 || relY <= 1 ) {
                    return City(coords.getString(1), relX, relY)
                }
            }
        }
        catch(e: SQLException) {
            print(e.message)
        }

        return City.nullCity
    }
}