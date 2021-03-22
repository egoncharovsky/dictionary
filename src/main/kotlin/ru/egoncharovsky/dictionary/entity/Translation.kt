package ru.egoncharovsky.dictionary.entity

data class Translation(
    val meaning: Text,
    val examples: List<Example>
)
