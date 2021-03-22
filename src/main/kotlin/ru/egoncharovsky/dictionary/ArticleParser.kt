package ru.egoncharovsky.dictionary

import ru.egoncharovsky.dictionary.entity.Article
import ru.egoncharovsky.dictionary.entity.Example
import ru.egoncharovsky.dictionary.entity.Translation

class ArticleParser(
    private val textParser: TextParser
) {

    private val key = "${Tags.key}(.*)${Tags.key.closing()}".toRegex()
    private val example = "([a-zA-Z ,;'\".?!()]+)([а-яА-Я ,;'\".?!()_]+)".toRegex()

    private val translationMarker = "[1-9][0-9]*&gt;".toRegex()
    private val exampleMarker = "_Ex:".toRegex()

    fun parseArticle(lines: List<String>): Article {
        val key: String = key.find(lines[0])!!.groupValues[1]

        val raw = lines.joinToString("\n")

        val normalized = normalize(raw)

        val translations = translationMarker.split(normalized).drop(1).map { rawTranslation ->
            val split = exampleMarker.split(rawTranslation)
            val meaning = textParser.parse(split[0])

            val examples = split.drop(1).map { rawExample ->
                example.find(rawExample)!!.let {
                    Example(it.groupValues[1].trim(), textParser.parse(it.groupValues[2]))
                }
            }

            Translation(meaning, examples)
        }

        return Article(key, translations, raw)
    }

    private fun normalize(string: String) = string.replace("\\s+\n*".toRegex(), " ")
}