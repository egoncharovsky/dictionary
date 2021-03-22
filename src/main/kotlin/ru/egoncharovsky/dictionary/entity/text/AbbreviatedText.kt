package ru.egoncharovsky.dictionary.entity.text

import ru.egoncharovsky.dictionary.entity.Abbreviation

data class AbbreviatedText(
    val abbreviations: List<Abbreviation>,
    val text: String
) : Text {

    override fun asPlain(): String = "${abbreviations.joinToString(" ") { it.short }} $text"
}