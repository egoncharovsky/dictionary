package ru.egoncharovsky.dictionary

import com.twmacinta.util.MD5
import mu.KotlinLogging
import java.io.File
import java.io.RandomAccessFile

class DictionaryReader(
    private val dictionaryPath: String,
    private val indexPath: String
) {
    private object Tag {
        const val article = "<ar>"

        const val key = "<k>"

        fun closing(tag: String) = tag.replace("<", "</")
    }

    private val dictionaryFile = File(dictionaryPath)
    private val indexFile = File(dictionaryPath)

    private val keyRegex = "${Tag.key}(.*)${Tag.closing(Tag.key)}".toRegex()

    private val logger = KotlinLogging.logger {  }

    fun readArticlePositions(): Map<String, List<Long>> {
        val hash = MD5.asHex(MD5.getHash(dictionaryFile))
        logger.debug("MD5 hash: $hash")

        return RandomAccessFile(dictionaryFile, "r").use { raf ->
            val keyPositions = mutableMapOf<String, List<Long>>()
            while (raf.filePointer < 50000) {
                val lineStart = raf.filePointer
                raf.readLine(charset("UTF-8"))?.let { line ->
                    if (line.startsWith(Tag.article)) {

                        val key = parseKey(line) ?: throw KeyNotFound(lineStart, line)

                        keyPositions[key] = keyPositions[key]?.plus(lineStart) ?: run {
                            listOf(lineStart)
                        }

                    }
                }
            }
            logger.debug("Keys found total: ${keyPositions.size}")
            keyPositions
        }.toMap()
    }

    private fun parseKey(articleLine: String): String? = keyRegex.find(articleLine)?.groups?.get(1)?.value
}