package datasource.microchaos

class Url(val url: String) {

    fun extractServiceName(): String {
        val matchResult = Regex("""http[s]?:\/\/([^\:\/]+)(\:(\d*))?\/.*""").find(this.url)
        require(matchResult?.groupValues?.isNotEmpty() ?: false) { "Invalid service url" }
        return matchResult?.groupValues?.get(1)
            ?: throw NoSuchElementException("Invalid service url")
    }
}