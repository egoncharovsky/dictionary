package ru.egoncharovsky.dictionary

class KeyNotFound(position: Long, articleLine: String) : Exception("Key not found for $articleLine at $position")