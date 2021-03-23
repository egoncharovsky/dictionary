package ru.egoncharovsky.dictionary.entity.example

import ru.egoncharovsky.dictionary.entity.text.Text

data class Idiom(
    override val sample: String,
    override val sampleTranslation: Text
) : Sample