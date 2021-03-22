package ru.egoncharovsky.dictionary

object Tags {
    val article = Tag("ar")
    val key = Tag("k")

    data class Tag(val value: String) {
        override fun toString(): String = "<$value>"
        fun closing() = "</$value>"
    }
}