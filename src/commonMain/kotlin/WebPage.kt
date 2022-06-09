class WebPage(val id: String, val path: String) {
    companion object {
        val index = WebPage(id = "root", path = "/")
        val singleplayerMenu = WebPage(id = "singleplayerRoot", path = "/singleplayer")
        val singleplayerGame = WebPage(id = "singplayerMapRoot", path = "/singleplayer")
    }
}