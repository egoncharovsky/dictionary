package ru.egoncharovsky.dictionary

import org.junit.jupiter.api.Test
import ru.egoncharovsky.dictionary.entity.AbbreviatedText
import ru.egoncharovsky.dictionary.entity.Abbreviation
import ru.egoncharovsky.dictionary.entity.MultipartText
import ru.egoncharovsky.dictionary.entity.PlainText
import kotlin.test.assertEquals

internal class TextParserTest {

    @Test
    fun `Text with multiple not sequenced abbreviations should be parsed in multipart text`() {
        val text = "префикс _тех. сборочный стенд; _мор. стапель"
        val abbreviations = mapOf(
            "_тех." to Abbreviation("тех.", "техническое"),
            "_мор." to Abbreviation("мор.", "морское")
        )

        val parser = TextParser(abbreviations)

        val parsed = parser.parse(text) as MultipartText
        assertEquals(
            MultipartText(
                listOf(
                    PlainText("префикс"),
                    AbbreviatedText(listOf(abbreviations["_тех."]!!), "сборочный стенд;"),
                    AbbreviatedText(listOf(abbreviations["_мор."]!!), "стапель")
                )
            ),
            parsed
        )
    }

    @Test
    fun `Sequence of abbreviations should be joined`() {
        val text = "_тех. _мор. стапель"
        val abbreviations = mapOf(
            "_тех." to Abbreviation("тех.", "техническое"),
            "_мор." to Abbreviation("мор.", "морское")
        )

        val parser = TextParser(abbreviations)

        val parsed = parser.parse(text) as AbbreviatedText
        assertEquals(
            AbbreviatedText(listOf(abbreviations["_тех."]!!, abbreviations["_мор."]!!), "стапель"),
            parsed
        )
    }
}