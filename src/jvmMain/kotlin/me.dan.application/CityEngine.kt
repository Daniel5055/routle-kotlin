package me.dan.application

import City
import RoutleMap
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import kotlin.math.ln
import kotlin.math.tan

class CityEngine {

    private var connection : Connection? = null

    init {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:db/cities.db")
        }
        catch (e: SQLException) {
            println(e.message)
        }

    }

    fun getCities(map: RoutleMap, name: String) : List<City> {
        val cities = mutableListOf<City>()

        try {
            var queryString = "SELECT AsciiName, Lat, Long FROM cities WHERE ("
            for ( i in 0 until map.countryCodes.size) {
                queryString += " CC = '${map.countryCodes[i]}' OR CC2 = '${map.countryCodes[i]}'"

                if (i != map.countryCodes.size - 1) {
                    queryString += " OR"
                }
            }
            queryString += " ) AND ( LOWER(RealName) = LOWER(?) OR LOWER(AsciiName) = LOWER(?) OR LOWER(AltNames) LIKE '%:' || ? || ':%' )"

            val statement = connection!!.prepareStatement(queryString)
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

                val relY = (latToY(map.latMax) - latToY(coords.getDouble(2))) /
                        (latToY(map.latMax) - latToY(map.latMin))

                val relX = (coords.getDouble(3) - map.longMin) / (map.longMax - map.longMin)

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

    fun getRandomCity(map: RoutleMap): City {
        try {
            var queryString = "SELECT AsciiName, Lat, Long FROM cities WHERE ("
            for ( i in 0 until map.countryCodes.size) {
                queryString += " CC = '${map.countryCodes[i]}' OR CC2 = '${map.countryCodes[i]}'"

                if (i != map.countryCodes.size - 1) {
                    queryString += " OR"
                }
            }
            queryString += " ) ORDER BY random() LIMIT 1"

            val statement = connection!!.prepareStatement(queryString)

            // Until good random gotten
            while (true)
            {
                val coords = statement.executeQuery()

                // Quick function to turn latitude into linear y coord
                val latToY = { lat: Double ->
                    ln(tan(Math.PI/4 + lat * Math.PI / 360))
                }

                val relY = (latToY(map.latMax) - latToY(coords.getDouble(2))) /
                        (latToY(map.latMax) - latToY(map.latMin))

                val relX = (coords.getDouble(3) - map.longMin) / (map.longMax - map.longMin)

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