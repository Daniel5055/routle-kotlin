package me.dan.application

import RoutleMap
import WebPage
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
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.json.Json
import java.io.File


fun createHTMLPlating(rootId: String): HTML.() -> Unit {
    return {
        head {
            title("Routle")
        }
        body {
            id = rootId
            script(src = "/static/routle.js") {}
        }
    }
}

fun readJSON(fileName: String): String {
    val inputStream = File(fileName).inputStream()
    return inputStream.bufferedReader().use { it.readText() }
}

fun main() {
    val engine = CityEngine()

    val routleMaps = Json.decodeFromString(ListSerializer(RoutleMap.serializer()), readJSON("src/jsMain/resources/routleMaps.json"))

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
            route(WebPage.index.path) {
                get {
                    call.respondHtml(HttpStatusCode.OK, createHTMLPlating(WebPage.index.id))
                }
            }

            route(WebPage.singleplayerMenu.path) {
                get {
                    call.respondHtml(HttpStatusCode.OK, createHTMLPlating(WebPage.singleplayerMenu.id))
                }

                route("{mapName?}") {

                    // For determining if map exists among the maps
                    fun doesMapExist(mapName: String?): RoutleMap? {
                        mapName?.let {
                            routleMaps.forEach {
                                if (mapName == it.name) {
                                    return it
                                }
                            }
                        }

                        return null
                    }

                    get {
                        val mapName: String? = call.parameters["mapName"]

                        if (doesMapExist(mapName) != null) {
                            call.respondHtml(HttpStatusCode.OK, createHTMLPlating(WebPage.singleplayerGame.id))
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }

                    get("map") {
                        val mapName: String? = call.parameters["mapName"]

                        val result = doesMapExist(mapName)
                        if (result != null) {
                            call.respond(result)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }

                    // Querying city names
                    route("/city") {
                        get("{name?}") {
                            val cityName : String = (call.parameters["name"] ?: "o")
                            val mapName: String? = call.parameters["mapName"]

                            println("A user entered $cityName")

                            val map = doesMapExist(mapName)
                            if (map != null) {
                                val cities = engine.getCities(map, cityName)
                                call.respond(cities)
                            } else {
                                call.respond(HttpStatusCode.NotFound)
                            }
                        }
                    }

                    // Querying a random city
                    get("/random") {
                        val mapName: String? = call.parameters["mapName"]
                        val map = doesMapExist(mapName)
                        if (map != null) {
                            val city = engine.getRandomCity(map)
                            call.respond(city)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }
                }
            }

            // Static resource access
            static("/static") {
                resources()
            }
        }
    }.start(wait = true)
}