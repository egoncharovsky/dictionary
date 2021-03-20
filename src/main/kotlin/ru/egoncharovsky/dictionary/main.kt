package ru.egoncharovsky.dictionary

fun main(args: Array<String>) {

    val reader = DictionaryReader("content/magus/dict.xdxf", "index/magus_dict.xdxf.index")

    val articlePositions = reader.readArticlePositions()
    println()
}