package ru.egoncharovsky.dictionary

class ParseException(raw: String, details: String) : Exception("Can't parse: $details at '$raw'")