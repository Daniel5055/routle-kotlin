@kotlinx.serialization.Serializable
class RoutleMap(val name: String,
                val path: String,
                val longMax: Double,
                val longMin: Double,
                val latMax: Double,
                val latMin: Double,
                val countryCodes: List<String>)