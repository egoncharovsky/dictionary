package ru.egoncharovsky.dictionary.entity

data class PlainText(
    val string: String
) : Text {
    override fun asPlain(): String = string

    override fun toString(): String = string
}