package ru.egoncharovsky.dictionary.entity.text

data class MultipartText(
    val parts: List<Text>
) : Text {

    override fun asPlain(): String = parts.joinToString(" ") { it.asPlain() }
}