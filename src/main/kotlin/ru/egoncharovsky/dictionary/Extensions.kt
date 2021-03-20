package ru.egoncharovsky.dictionary

import java.io.ByteArrayOutputStream
import java.io.RandomAccessFile
import java.nio.charset.Charset

fun RandomAccessFile.readLine(charset: Charset): String? {
    val bytes = ByteArrayOutputStream()

    var c = -1
    var eol = false

    while (!eol) {
        when (read().also { c = it }) {
            -1, '\n'.toInt() -> eol = true
            '\r'.toInt() -> {
                eol = true
                val cur = filePointer
                if (read() != '\n'.toInt()) {
                    seek(cur)
                }
            }
            else -> bytes.write(c)
        }
    }

    return if (c == -1 && bytes.size() == 0) {
        null
    } else {
        bytes.toString(charset)
    }
}