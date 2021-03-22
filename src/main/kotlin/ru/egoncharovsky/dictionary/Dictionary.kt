package ru.egoncharovsky.dictionary

import ru.egoncharovsky.dictionary.entity.Article
import ru.egoncharovsky.dictionary.entity.Example

class Dictionary(
  private val reader: DictionaryReader
) {
    private val index = reader.readArticlePositions()

    private val keyRegex = "${Tags.key}(.*)${Tags.key.closing()}".toRegex()
    private val translationRegex = "[1-9][0-9]*&gt; (.*)".toRegex()
    private val exampleMarkerRegex = "_Ex:".toRegex()
    private val exampleLineRegex = "([a-zA-Z ]+)([а-яА-Я ]+)".toRegex()

    fun getArticles(word: String): List<Article>? {
        return index[word]?.map {
            parseArticle(reader.readArticle(it))
        }
    }

    private fun parseArticle(lines: List<String>): Article {
        val key: String = keyRegex.find(lines[0])!!.groupValues[1]

        val builder = Article.Builder(key, lines.joinToString("\n"))
        var captureExample = false

        lines.forEach {  line ->
            translationRegex.find(line)?.let {
                builder.addTranslation(it.groupValues[1])
                captureExample = false
            }

            if (captureExample) {
                val found = exampleLineRegex.find(line.trim())!!
                val sample = found.groupValues[1]
                val sampleTranslation = found.groupValues[2]
                builder.addExample(Example(sample, sampleTranslation))
                captureExample = false
            }
            if (exampleMarkerRegex.containsMatchIn(line)) {
                captureExample = true
            }
        }

        return builder.build()
    }
}