package ru.egoncharovsky.dictionary.entity

data class Article(
    val key: String,
    val translations: List<Translation>
)
