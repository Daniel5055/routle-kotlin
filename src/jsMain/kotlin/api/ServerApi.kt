package api

import City
import RoutleMap
import WebPage
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.browser.window

// Apparently needed due to bug
val endpoint = window.location.origin

val jsonClient = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

suspend fun getRandomCity() : City? {
    val city: City = jsonClient.get("$endpoint/${window.location.pathname}/random")
    if (city == City.nullCity)
    {
        return null
    }
    return city
}

suspend fun getCities(name: String): List<City> {
    return jsonClient.get("$endpoint/${window.location.pathname}/city/$name")
}

suspend fun getMaps(): List<RoutleMap> {
    return jsonClient.get("$endpoint/static/routleMaps.json")
}

suspend fun getMap(): RoutleMap {
    return jsonClient.get("$endpoint/${window.location.pathname}/map")
}
fun goToSingleplayerMap(map: RoutleMap, difficulty: Double) {
    if (difficulty == 1.0) {
        window.location.href = "${WebPage.singleplayerGame.path}/${map.name}"
    } else {
        window.location.href = "${WebPage.singleplayerGame.path}/${map.name}?difficulty=$difficulty"
    }
}

fun goToSingleplayerMenu() {
    window.location.href = WebPage.singleplayerMenu.path
}