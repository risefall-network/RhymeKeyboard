package com.antih3ro.rhymekeyboard

object Utils {
    private val wordRegex = Regex("[A-Za-z']+")

    fun lastWordOfPreviousLine(textBeforeCursor: CharSequence): String? {
        val s = textBeforeCursor.toString()
        val idx = s.lastIndexOf('\n')
        if (idx <= 0) return null // no previous line
        val prevLine = s.substring(0, idx)
        val lastBreak = prevLine.lastIndexOf('\n')
        val line = if (lastBreak >= 0) prevLine.substring(lastBreak + 1) else prevLine
        // grab last word in that line
        val words = wordRegex.findAll(line).toList()
        return words.lastOrNull()?.value?.lowercase()
    }

    fun allLineEndWords(textBeforeCursor: CharSequence): List<String> {
        val s = textBeforeCursor.toString()
        val lines = s.split('\n')
        val ends = mutableListOf<String>()
        val rx = Regex("[A-Za-z']+")
        for (line in lines.dropLast(1)) { // previous lines only
            val words = rx.findAll(line).toList()
            val last = words.lastOrNull()?.value?.lowercase()
            if (!last.isNullOrBlank()) ends.add(last)
        }
        return ends.distinct()
    }
}
