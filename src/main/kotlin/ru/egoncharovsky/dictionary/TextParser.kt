package ru.egoncharovsky.dictionary

import ru.egoncharovsky.dictionary.entity.Abbreviation
import ru.egoncharovsky.dictionary.entity.text.AbbreviatedText
import ru.egoncharovsky.dictionary.entity.text.MultipartText
import ru.egoncharovsky.dictionary.entity.text.PlainText
import ru.egoncharovsky.dictionary.entity.text.Text

class TextParser(
    private val abbreviations: Map<String, Abbreviation>
) {
    private val abbreviationPlaceholder = "(_[а-я\\-]+\\.)".toRegex()

    fun parse(raw: String): Text {
        if (!abbreviationPlaceholder.containsMatchIn(raw)) {
            return PlainText(raw.trim())
        }
        val parts = mutableListOf<Text>()

        val matches = abbreviationPlaceholder.findAll(raw).toList()
        val split = abbreviationPlaceholder.split(raw)

        split.first().trim().let {
            if (it.isNotBlank()) parts.add(PlainText(it))
        }

        val abbrJoin = mutableListOf<Abbreviation>()
        split.drop(1).zip(matches).forEach { p ->
            val text = p.first.trim()
            val match = p.second
            val abbreviation = abbreviations[match.groupValues[1]]!!

            abbrJoin.add(abbreviation)

            if(text.isNotBlank()) {
                parts.add(AbbreviatedText(abbrJoin.toList(), text))
                abbrJoin.clear()
            }
        }

        return if (parts.size > 1) {
            MultipartText(parts)
        } else {
            parts.first()
        }
    }
}