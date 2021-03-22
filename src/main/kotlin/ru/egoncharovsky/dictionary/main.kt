package ru.egoncharovsky.dictionary

import java.io.File

fun main(args: Array<String>) {
    val dictionaryFile = File(DictionaryReader::class.java.classLoader.getResource("magus/dict.xdxf")!!.toURI())
    val reader = DictionaryReader(dictionaryFile, File("index/magus_dict.xdxf.index"))

    val dictionary = Dictionary(reader)
    val articles = dictionary.getArticles("run")
    println(articles!![0].raw)
}