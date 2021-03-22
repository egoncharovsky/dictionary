package ru.egoncharovsky.dictionary

import ru.egoncharovsky.dictionary.entity.Article
import ru.egoncharovsky.dictionary.entity.Translation

class Dictionary(
  private val reader: DictionaryReader
) {
    private val index = reader.readArticlePositions()

    private val keyRegex = "${Tags.key}(.*)${Tags.key.closing()}".toRegex()
    private val translationRegex = "[1-9][0-9]*&gt; (.*)".toRegex()
    private val exampleRegex = "_Ex:".toRegex()

    fun getArticles(word: String): List<Article>? {
        return index[word]?.map {
            parseArticle(reader.readArticle(it))
        }
    }

    private fun parseArticle(lines: List<String>): Article {
        val key: String = keyRegex.find(lines[0])!!.groupValues[1]

        val translations = mutableListOf<String>()
        val translationWithExamples = mutableMapOf<String, MutableList<String>>()

        var captureExample = false

        lines.forEach {  line ->
            translationRegex.find(line)?.let {
                translations.add(it.groupValues[1])
                captureExample = false
            }

            if (captureExample) {
                val translation = translations.last()
                val examples = translationWithExamples[translation] ?: mutableListOf<String>().also {
                    translationWithExamples[translation] = it
                }
                examples.add(line.trim())
                captureExample = false
            }
            if (exampleRegex.containsMatchIn(line)) {
                captureExample = true
            }
        }

        return Article(key, translationWithExamples.map { e ->
            val meaning = e.key
            val examples = e.value
            Translation(meaning, examples)
        })
    }
}