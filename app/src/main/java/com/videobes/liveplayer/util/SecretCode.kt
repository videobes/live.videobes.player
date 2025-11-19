package com.videobes.liveplayer.util

class SecretCode(
    private val callback: (String) -> Unit
) {
    private var buffer = ""

    fun receive(char: Char) {
        buffer += char

        if (buffer.length > 4) {
            buffer = buffer.takeLast(4)
        }

        if (buffer == "*111" || buffer == "*222" || buffer == "*999" || buffer == "*000") {
            callback(buffer)
        }
    }
}
