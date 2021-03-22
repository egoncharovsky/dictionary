package ru.egoncharovsky.dictionary.entity

data class PlainText(
    val text: String
) : Text {
    override fun asPlain(): String = text

    override fun toString(): String = text
}