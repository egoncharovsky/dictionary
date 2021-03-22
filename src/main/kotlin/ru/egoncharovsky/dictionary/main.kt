package ru.egoncharovsky.dictionary

fun main(args: Array<String>) {

    val reader = DictionaryReader("content/magus/dict.xdxf", "index/magus_dict.xdxf.index")

    val dictionary = Dictionary(reader)
    val articles = dictionary.getArticles("assembly")
    println(articles!![0].raw)
}