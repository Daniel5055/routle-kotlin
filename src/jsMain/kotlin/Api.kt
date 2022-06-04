import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.browser.window
import kotlin.js.Json

// Apparently needed due to bug
val endpoint = window.location.origin

val jsonClient = HttpClient {
    // Probably needed to send json back via posts
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

suspend fun getRandomCity() : City? {
    val city: City = jsonClient.get("$endpoint/random")
    if (city == City.nullCity)
    {
        return null
    }
    return city
}

suspend fun getCities(name: String): List<City> {
    return jsonClient.get("$endpoint/city/$name")
}