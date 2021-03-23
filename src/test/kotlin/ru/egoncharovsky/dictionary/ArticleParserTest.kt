package ru.egoncharovsky.dictionary

import org.junit.jupiter.api.Test
import ru.egoncharovsky.dictionary.entity.Article
import ru.egoncharovsky.dictionary.entity.Translation
import ru.egoncharovsky.dictionary.entity.example.Example
import ru.egoncharovsky.dictionary.entity.example.Idiom
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

    @Test
    fun `Idioms should be parsed`() {
        val parser = ArticleParser(TextParser(mapOf()))

        val raw = """<ar><k>run</k>
            |run
            |      121> иметь
            |_Ex:
            |       to run (a) temperature иметь (высокую) температуру
            |_Ex:
            |       I think I am running a temperature мне кажется, что у меня
            |       (поднимается) температура
            |_Ex:
            |       to run a fever лихорадить
            |_Id:
            |       an also ran неудачник
            |_Id:
            |       to run the streets быть беспризорником""".trimMargin()

        val article = parser.parseArticle(raw.split("\n"))

        assertEquals(
            Article(
                "run",
                listOf(
                    Translation(
                        PlainText("иметь"), listOf(
                            Example(
                                "to run (a) temperature",
                                PlainText("иметь (высокую) температуру")
                            ),
                            Example(
                                "I think I am running a temperature",
                                PlainText("мне кажется, что у меня (поднимается) температура")
                            ),
                            Example(
                                "to run a fever",
                                PlainText("лихорадить")
                            ),
                            Idiom("an also ran", PlainText("неудачник")),
                            Idiom("to run the streets", PlainText("быть беспризорником"))
                        )
                    )
                ),
                raw
            ),
            article
        )
    }
}