package ru.egoncharovsky.dictionary

import org.junit.jupiter.api.Test
import ru.egoncharovsky.dictionary.entity.Article
import ru.egoncharovsky.dictionary.entity.Example
import ru.egoncharovsky.dictionary.entity.Translation
import ru.egoncharovsky.dictionary.entity.text.PlainText
import kotlin.test.assertEquals


internal class ArticleParserTest {


    @Test
    fun `Multiline elements should be parsed as single`() {
        val parser = ArticleParser(TextParser(mapOf()))

        val raw = """<ar><k>assembly</k>
            |assembly
            |      1&gt; сбор, собрание, общество
            |          _Ex:
            |       to convoke (to summon) an assembly созывать собрание
            |          _Ex:
            |       never had there been so full an assembly никогда еще не было
            |       такого большого сборища
            |      2&gt; ассамблея, собрание
            |          _Ex:
            |       constituent assembly учредительное собрание
            |          _Ex:
            |       United Nations General A. Генеральная Ассамблея Организации
            |       Объединенных Наций
            |      3&gt; законодательное собрание, обыкн нижняя палата
            |      законодательного органа штата (в США)""".trimMargin()

        val article = parser.parseArticle(raw.split("\n"))

        assertEquals(
            Article(
                "assembly",
                listOf(
                    Translation(
                        PlainText("сбор, собрание, общество"), listOf(
                            Example(
                                "to convoke (to summon) an assembly",
                                PlainText("созывать собрание")
                            ),
                            Example(
                                "never had there been so full an assembly",
                                PlainText("никогда еще не было такого большого сборища")
                            )
                        )
                    ),
                    Translation(
                        PlainText("ассамблея, собрание"), listOf(
                            Example("constituent assembly", PlainText("учредительное собрание")),
                            Example(
                                "United Nations General A.",
                                PlainText("Генеральная Ассамблея Организации Объединенных Наций")
                            )
                        )
                    ),
                    Translation(
                        PlainText("законодательное собрание, обыкн нижняя палата законодательного органа штата (в США)"),
                        listOf()
                    )
                ),
                raw
            ),
            article
        )
    }
}