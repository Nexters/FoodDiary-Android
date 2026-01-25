import java.io.InputStreamReader

object MockResponseFileReader {
    fun readFile(fileName: String): String {
        val inputStream = javaClass.classLoader?.getResourceAsStream("api-response/$fileName")
        val builder = StringBuilder()
        val reader = InputStreamReader(inputStream ?: throw IllegalArgumentException("File not found: $fileName"))
        reader.readLines().forEach {
            builder.append(it)
        }
        return builder.toString()
    }
}
