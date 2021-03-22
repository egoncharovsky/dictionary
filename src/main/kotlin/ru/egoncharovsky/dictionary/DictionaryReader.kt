package ru.egoncharovsky.dictionary

import com.twmacinta.util.MD5
import mu.KotlinLogging
import java.io.File
import java.io.RandomAccessFile
import kotlin.system.measureTimeMillis

class DictionaryReader(
    private val dictionaryFile: File,
    private val indexFile: File
) {

    private val keyRegex = "${Tags.key}(.*)${Tags.key.closing()}".toRegex()

    private val indexSeparator = ","
    private val indexKeyValueSeparator = ":"

    private val logger = KotlinLogging.logger { }

    fun readArticlePositions(): Map<String, List<Long>> {
        val positions: Map<String, List<Long>>
        val time = measureTimeMillis {
            val dictionaryHash = MD5.asHex(MD5.getHash(dictionaryFile))
            logger.debug("MD5 dictionary hash: $dictionaryHash")

            positions = if (indexFile.exists()) {
                val lines = indexFile.readLines()
                val indexHash = lines[0]
                if (indexHash == dictionaryHash) {
                    restoreArticlePositions(lines)
                } else {
                    logger.debug("Dictionary changed, rescan: dictionary MD5 $dictionaryHash index MD5 $indexHash")
                    scanArticlePositions().also {
                        saveArticlePositionsIndex(dictionaryHash, it)
                    }
                }
            } else {
                logger.debug("Index doesn't exist")
                scanArticlePositions().also {
                    saveArticlePositionsIndex(dictionaryHash, it)
                }
            }
        }
        logger.debug("Article positions read in ${seconds(time)}")
        return positions
    }

    fun scanArticlePositions(): Map<String, List<Long>> {
        logger.debug("Scanning dictionary...")

        return RandomAccessFile(dictionaryFile, "r").use { raf ->

            val keyPositions = mutableMapOf<String, List<Long>>()
            while (raf.filePointer < raf.length()) {
                val lineStart = raf.filePointer
                val line = raf.readLine(charset("UTF-8"))

                if (line != null && line.startsWith(Tags.article.toString())) {
                    val key = parseKey(line) ?: throw KeyNotFound(lineStart, line)

                    keyPositions[key] = keyPositions[key]?.plus(lineStart) ?: run {
                        listOf(lineStart)
                    }
                }
            }
            logger.debug("Keys found total: ${keyPositions.size}")
            keyPositions
        }.toMap()
    }

    fun clearIndex() = if (indexFile.exists()) indexFile.delete() else true

    fun readArticle(position: Long): List<String> {
        logger.debug("Read article at $position")

        val article: List<String>
        val time = measureTimeMillis {
            article = RandomAccessFile(dictionaryFile, "r").use { readArticleLines(it, position) }
        }
        logger.debug("Article read in ${seconds(time)}")
        return article
    }

    fun readArticles(positions: List<Long>): List<List<String>> {
        logger.debug("Read ${positions.size} articles")

        val articles: List<List<String>>
        val time = measureTimeMillis {
            articles = RandomAccessFile(dictionaryFile, "r").use { raf ->
                positions.map { readArticleLines(raf, it) }
            }
        }
        logger.debug("Articles read in ${seconds(time)}")
        return articles
    }

    private fun readArticleLines(raf: RandomAccessFile, position: Long): List<String> {
        raf.seek(position)
        var articleCounter = 0
        val articleLines = mutableListOf<String>()

        while (raf.filePointer < raf.length()) {
            val line = raf.readLine(charset("UTF-8"))
            if (line != null) {
                if (line.startsWith(Tags.article.toString())) {
                    articleCounter++
                }
                if (articleCounter > 1) {
                    break
                } else {
                    articleLines.add(line)
                }
            }
        }
        return articleLines
    }

    private fun restoreArticlePositions(indexLines: List<String>): Map<String, List<Long>> {
        logger.debug("Restoring index")
        val index = indexLines[1].split(indexSeparator).map {
            val split = it.split(indexKeyValueSeparator)
            val key = split[0]
            val position = split[1].toLong()
            key to position
        }.groupBy({ it.first }, { it.second })

        logger.debug("Restored ${index.size} keys")

        return index
    }

    private fun saveArticlePositionsIndex(hash: String, positions: Map<String, List<Long>>) {
        logger.debug("Saving index")
        val index = positions.flatMap { e ->
            val key = e.key
            e.value.map { position ->
                "$key$indexKeyValueSeparator$position"
            }
        }.joinToString(indexSeparator)

        indexFile.writeText("$hash\n$index")
    }

    private fun parseKey(articleLine: String): String? = keyRegex.find(articleLine)?.groups?.get(1)?.value

    private fun seconds(millis: Long) = "${millis / 1000}.${millis % 1000} s"
}