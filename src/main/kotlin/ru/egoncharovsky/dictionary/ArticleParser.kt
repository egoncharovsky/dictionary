package ru.egoncharovsky.dictionary

import ru.egoncharovsky.dictionary.entity.Article
import ru.egoncharovsky.dictionary.entity.Translation
import ru.egoncharovsky.dictionary.entity.example.Example
import ru.egoncharovsky.dictionary.entity.example.Idiom

class ArticleParser(
    private val textParser: TextParser
) {

    private val key = "${Tags.key}(.*)${Tags.key.closing()}".toRegex()
    private val sampleAndTranslation = "([a-zA-Z ,;'\".?!()]+)([а-яА-Я ,;'\".?!()_]+)".toRegex()

    private val translationMarker = "[1-9][0-9]*(&gt;|>)".toRegex()
    private val exampleMarker = "_(Ex|Id):".toRegex()

    fun parseArticle(lines: List<String>): Article {
        val key: String = key.find(lines[0])!!.groupValues[1]

        val raw = lines.joinToString("\n")

        val normalized = normalize(raw)

        val translations = translationMarker.split(normalized).drop(1).map { rawTranslation ->
            val split = exampleMarker.split(rawTranslation)
            val meaning = textParser.parse(split[0])

            val exampleMarkers = exampleMarker.findAll(rawTranslation).map { it.groupValues[1] }.toList()

            val examples = split.drop(1).zip(exampleMarkers).map { p ->
                val rawExample = p.first
                val type = p.second

                sampleAndTranslation.find(rawExample)!!.let {
                    val sample = it.groupValues[1].trim()
                    val sampleTranslation = textParser.parse(it.groupValues[2])

                    when(type) {
                        "Ex" -> Example(sample, sampleTranslation)
                        "Id" -> Idiom(sample, sampleTranslation)
                        else -> throw ParseException("unknown example type $type", rawExample)
                    }
                }
            }

            Translation(meaning, examples)
        }

        return Article(key, translations, raw)
    }

    private fun normalize(string: String) = string.replace("\\s+\n*".toRegex(), " ")
}