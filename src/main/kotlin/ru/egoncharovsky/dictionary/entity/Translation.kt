package ru.egoncharovsky.dictionary.entity

data class Translation(
    val meaning: Text,
    val examples: List<Example>
) {

    override fun toString(): String {
        return "Translation(meaning=${meaning.asPlain()}, examples=$examples)"
    }
}
