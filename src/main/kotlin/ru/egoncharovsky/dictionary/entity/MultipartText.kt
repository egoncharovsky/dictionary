package ru.egoncharovsky.dictionary.entity

data class MultipartText(
    val parts: List<Text>
) : Text {

    override fun asPlain(): String = parts.joinToString(" ") { it.asPlain() }
}