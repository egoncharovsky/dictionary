package ru.egoncharovsky.dictionary.entity

import ru.egoncharovsky.dictionary.entity.text.Text

data class Example(
    val sample: String,
    val sampleTranslation: Text
)