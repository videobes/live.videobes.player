package com.videobes.liveplayer.util

class KeyInputDetector(
    private val onCodeDetected: (String) -> Unit
) {

    private var buffer = StringBuilder()

    fun input(char: Char) {
        buffer.append(char)

        // Mantém o buffer curto
        if (buffer.length > 5) {
            buffer.delete(0, buffer.length - 5)
        }

        val code = buffer.toString()

        when (code) {
            "*111" -> onCodeDetected("*111")   // Debug
            "*222" -> onCodeDetected("*222")   // Avançado
            "*999" -> onCodeDetected("*999")   // Unlock kiosk
            "*000" -> onCodeDetected("*000")   // Lock kiosk
        }
    }
}
