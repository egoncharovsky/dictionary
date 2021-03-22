package ru.egoncharovsky.dictionary

import ru.egoncharovsky.dictionary.entity.Abbreviation
import ru.egoncharovsky.dictionary.entity.Article
import ru.egoncharovsky.dictionary.entity.Example

class Dictionary(
  private val reader: DictionaryReader
) {
    private val keyRegex = "${Tags.key}(.*)${Tags.key.closing()}".toRegex()
    private val translationRegex = "[1-9][0-9]*&gt; (.*)".toRegex()
    private val exampleMarkerRegex = "_Ex:".toRegex()
    private val exampleLineRegex = "([a-zA-Z ,;'\".?!]+)([а-яА-Я ,;'\".?!_]+)".toRegex()

    private val abbreviationRegex = "([а-я\\-]+)".toRegex()
    private val abbreviationKeyRegex = "(_[а-я\\-]+\\.)".toRegex()

    private val index = reader.readArticlePositions()
    private val textParser = TextParser(getAbbreviations())

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
                val meaning = it.groupValues[1]
                builder.addTranslation(textParser.parse(meaning))
                captureExample = false
            }

            if (captureExample) {
                val found = exampleLineRegex.find(line.trim())!!
                val sample = found.groupValues[1]
                val sampleTranslation = found.groupValues[2]
                builder.addExample(Example(sample, textParser.parse(sampleTranslation)))
                captureExample = false
            }
            if (exampleMarkerRegex.containsMatchIn(line)) {
                captureExample = true
            }
        }

        return builder.build()
    }

    private fun getAbbreviations(): Map<String, Abbreviation> {
        val abbreviationIndex = index.filterKeys { this.abbreviationRegex.matches(it) }
        val abbreviations = reader.readArticles(abbreviationIndex.values.flatten().toList()).map {
            parseAbbreviation(it)
        }
        return abbreviations.toMap()
    }

    private fun parseAbbreviation(lines: List<String>): Pair<String, Abbreviation> {
        val key = abbreviationKeyRegex.find(lines[1])!!.groupValues[1]
        val short = key.drop(1)
        val full = abbreviationRegex.find(lines[2])!!.groupValues[1]
        return Pair(key, Abbreviation(short, full))
    }
}