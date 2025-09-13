package com.antih3ro.rhymekeyboard

object Utils {
    private val wordRegex = Regex("[a-zA-Z']+")

    fun lastWordOfPreviousLine(text: CharSequence): String? {
        val lines = text.split('\n')
        if (lines.size < 2) return null // Need at least two lines
        val previousLine = lines[lines.size - 2].trim()
        return wordRegex.findAll(previousLine).lastOrNull()?.value
    }

    fun allLineEndWords(text: CharSequence): List<String> {
        return text.split('\n')
            .mapNotNull { line ->
                wordRegex.findAll(line.trim()).lastOrNull()?.value
            }
            .filter { it.isNotBlank() }
            .distinct()
    }
}