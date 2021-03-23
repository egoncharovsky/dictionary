package ru.egoncharovsky.dictionary.entity

import ru.egoncharovsky.dictionary.entity.example.Sample
import ru.egoncharovsky.dictionary.entity.text.Text

data class Translation(
    val meaning: Text,
    val samples: List<Sample>
) {

    override fun toString(): String {
        return "Translation(meaning=${meaning.asPlain()}, samples=$samples)"
    }
}
