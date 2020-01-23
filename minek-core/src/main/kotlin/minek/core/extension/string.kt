package minek.core.extension

import java.util.*

fun String.uuid(): UUID? = try {
    UUID.fromString(this)
} catch (e: IllegalArgumentException) {
    null
}

fun String.truncate(length: Int, truncateString: String = Typography.ellipsis.toString()): String =
    if (this.length <= length) {
        this
    } else {
        this.substring(0, length) + truncateString
    }

fun String.base64Encoded(): String = Base64.getEncoder().encodeToString(toByteArray())
fun String.base64Decoded(): String? = try {
    String(Base64.getDecoder().decode(toByteArray()))
} catch (e: IllegalArgumentException) {
    null
}

fun String.isNumeric(): Boolean = this.matches("-?\\d+(\\.\\d+)?".toRegex())
fun String.isDigit(): Boolean = this.matches("^[0-9]*\$".toRegex())
