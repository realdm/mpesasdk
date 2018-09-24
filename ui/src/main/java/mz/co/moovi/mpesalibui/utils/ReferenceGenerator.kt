package mz.co.moovi.mpesalibui.utils

import java.util.*

object ReferenceGenerator {
    private val random by lazy {
        Random()
    }
    private const val VALID_CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890"
    fun generateReference(prefix: String, length: Int = 5): String {
        val sb = StringBuilder()
        sb.append(prefix)
        for(i in 1..length) {
            sb.append(VALID_CHARS[random.nextInt(VALID_CHARS.length)])
        }
        return sb.toString()
    }
}