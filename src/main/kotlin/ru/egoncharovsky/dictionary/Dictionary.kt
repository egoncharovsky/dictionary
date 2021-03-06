package ru.egoncharovsky.dictionary

import mu.KotlinLogging
import ru.egoncharovsky.dictionary.entity.Abbreviation
import ru.egoncharovsky.dictionary.entity.Article

class Dictionary(
  private val reader: DictionaryReader
) {

    private val logger = KotlinLogging.logger {  }

    private val abbreviationRegex = "([а-я\\-]+)".toRegex()
    private val abbreviationKeyRegex = "(_[а-я\\-]+\\.)".toRegex()

    private val index = reader.readArticlePositions()
    private val articleParser = ArticleParser(TextParser(getAbbreviations()))

    fun getArticles(word: String): List<Article>? {
        logger.debug("Getting articles for '$word'")
        return index[word]?.map { reader.readArticle(it) }?.map { articleParser.parseArticle(it) }
    }

    private fun getAbbreviations(): Map<String, Abbreviation> {
        val abbreviationIndex = index.filterKeys { this.abbreviationRegex.matches(it) }
        logger.debug("Read articles for ${abbreviationIndex.size} abbreviations")
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