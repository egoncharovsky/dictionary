package ru.egoncharovsky.dictionary.entity

data class Article(
    val key: String,
    val translations: List<Translation>,

    val raw: String
) {

    class Builder(
        private val key: String,
        private val raw: String
    ) {
        private val translations: MutableList<Translation> = mutableListOf()
        private var translation: TranslationBuilder? = null

        class TranslationBuilder(
            private val meaning: Text
        ) {
            val examples: MutableList<Example> = mutableListOf()

            fun build() = Translation(meaning, examples)
        }

        fun addTranslation(meaning: Text) {
            flushTranslation()
            translation = TranslationBuilder(meaning)
        }

        fun addExample(example: Example) {
            translation!!.examples.add(example)
        }

        private fun flushTranslation() {
            translation?.build()?.let { translations.add(it) }
        }

        fun build(): Article {
            flushTranslation()
            return Article(key, translations, raw)
        }
    }
}
