import kotlinx.serialization.Serializable

@Serializable
class City(val name: String, val relX: Double, val relY: Double) {
    companion object {
        val nullCity = City("NULL", -1.0, -1.0)
    }

    override fun equals(other: Any?): Boolean {
        if (other is City) {
            if (name == other.name && relX == other.relX && relY == other.relY) {
                return true
            }
        }

        return false
    }

    override fun toString(): String {
        return "$name at $relX, $relY"
    }

}