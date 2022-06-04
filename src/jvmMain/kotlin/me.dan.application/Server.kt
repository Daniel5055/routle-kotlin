package me.dan.application

import City
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.respondHtml
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.html.*
import java.sql.DriverManager

fun HTML.index() {
    head {
        title("Routle")
    }
    body {
        script(src = "/static/routle.js") {}
    }
}

fun main() {

    val engine = CityEngine(RoutleMap.uk)

    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        // Dependency Injections
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
        }
        install(Compression) {
            gzip()
        }

        // Basic routing
        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }

            // Querying city names
            route("/city") {
                get("{name?}") {
                    val cityName : String = (call.parameters["name"] ?: "o")

                    println("A user entered $cityName")

                    val cities = engine.getCities(cityName)
                    call.respond(cities)
                }
            }

            // Querying a random city
            get("/random") {
                val city = engine.getRandomCity()
                call.respond(city)
            }

            // Static resource access
            static("/static") {
                resources()
            }
        }
    }.start(wait = true)
}