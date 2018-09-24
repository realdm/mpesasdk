package mz.co.moovi.mpesalibui.utils

import java.io.InputStream

object TestFixtureLoader {

    fun getFixtureJson(fileName: String): String {
        val classLoader = javaClass.classLoader
        val fileInputStream = classLoader.getResourceAsStream("$fileName.json")
        return readJsonFromStream(fileInputStream)
    }

    private fun readJsonFromStream(inputStream: InputStream): String {
        return with(ByteArray(inputStream.available())) {
            inputStream.read(this)
            inputStream.close()
            String(this)
        }
    }
}
