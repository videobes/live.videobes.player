package com.videobes.liveplayer.util

/**
 * Detecta sequências numéricas do tipo:
 *  - *111 (debug)
 *  - *222 (abrir overlay de setup)
 *  - *999 (menu admin)
 *  - *000 (forçar relock no kiosk)
 *
 * PlayerActivity apenas recebe o código “limpo”.
 */
class KeyInputDetector(
    private val onDetected: (String) -> Unit
) {

    private val buffer = StringBuilder()

    fun input(char: Char) {
        buffer.append(char)

        // Limita o tamanho do buffer para não crescer infinitamente
        if (buffer.length > 5) {
            buffer.delete(0, buffer.length - 5)
        }

        val seq = buffer.toString()

        when {
            seq.endsWith("*111") -> {
                buffer.clear()
                onDetected("*111")
            }
            seq.endsWith("*222") -> {
                buffer.clear()
                onDetected("*222")
            }
            seq.endsWith("*999") -> {
                buffer.clear()
                onDetected("*999")
            }
            seq.endsWith("*000") -> {
                buffer.clear()
                onDetected("*000")
            }
        }
    }
}
