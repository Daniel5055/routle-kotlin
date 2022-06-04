class RoutleMap(val url: String,
                val long_max: Double,
                val long_min: Double,
                val lat_max: Double,
                val lat_min: Double)
{
    companion object {
        val uk = RoutleMap("/static/uk.png", 2.2, -10.7618, 59.157, 49.86)
    }
}