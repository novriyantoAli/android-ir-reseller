package id.co.insinyur.irreseller.utils

object Helpers {
    fun buildURL(hostname: String, port: String, path: String): String {
        return "http://${hostname}:${port}/${path}"
    }
}