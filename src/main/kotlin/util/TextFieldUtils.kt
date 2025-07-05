package util

import androidx.compose.ui.text.input.TextFieldValue

object TextFieldUtils {

    /** Trim leading and trailing spaces on every line */
    fun trimLines(value: TextFieldValue): TextFieldValue {
        val cleaned = value.text
            .lines()
            .joinToString("\n") { it.trim() }
        return value.copy(text = cleaned)
    }

    /** Remove all trailing spaces (end‑of‑line) on every line */
    fun removeTrailingSpaces(value: TextFieldValue): TextFieldValue {
        val cleaned = value.text
            .lines()
            .joinToString("\n") { it.trimEnd() }
        return value.copy(text = cleaned)
    }

    /** Remove all leading spaces (start‑of‑line) on every line */
    fun removeLeadingSpaces(value: TextFieldValue): TextFieldValue {
        val cleaned = value.text
            .lines()
            .joinToString("\n") { it.trimStart() }
        return value.copy(text = cleaned)
    }

    /** Collapse any run of 2+ ASCII spaces into a single space */
    fun collapseSpaces(value: TextFieldValue): TextFieldValue {
        val cleaned = value.text
            .replace(Regex(" {2,}"), " ")
        return value.copy(text = cleaned)
    }

    /** Collapse any run of 2+ whitespace (spaces, tabs, NBSP, etc.) to one space */
    fun collapseAllWhitespace(value: TextFieldValue): TextFieldValue {
        val cleaned = value.text
            .replace(Regex("\\s{2,}"), " ")
        return value.copy(text = cleaned)
    }

    /** Remove any completely blank lines */
    fun removeEmptyLines(value: TextFieldValue): TextFieldValue {
        val cleaned = value.text
            .lines()
            .filter { it.isNotBlank() }
            .joinToString("\n")
        return value.copy(text = cleaned)
    }

    /** Replace all tabs with [tabSize] spaces */
    fun expandTabs(value: TextFieldValue, tabSize: Int = 4): TextFieldValue {
        val cleaned = value.text
            .replace("\t", " ".repeat(tabSize))
        return value.copy(text = cleaned)
    }

    /** Normalize all line endings to `\n` */
    fun normalizeLineEndings(value: TextFieldValue): TextFieldValue {
        val cleaned = value.text
            .replace(Regex("\\r\\n?|\\n"), "\n")
        return value.copy(text = cleaned)
    }

    /** Remove leading and trailing blank lines (extra empty lines at start/end) */
    fun trimEmptyLines(value: TextFieldValue): TextFieldValue {
        val lines = value.text.lines()
        val firstNonBlank = lines.indexOfFirst { it.isNotBlank() }.takeIf { it >= 0 } ?: 0
        val lastNonBlank = lines.indexOfLast { it.isNotBlank() }.takeIf { it >= 0 } ?: lines.lastIndex
        val cleaned = lines.subList(firstNonBlank, lastNonBlank + 1).joinToString("\n")
        return value.copy(text = cleaned)
    }

    /** Apply all of the above: normalize endings, trim lines, collapse spaces, and remove empties */
    fun fullClean(value: TextFieldValue, tabSize: Int = 4): TextFieldValue {
        return value
            .let(::normalizeLineEndings)
            .let(::trimLines)
            .let { collapseSpaces(it) }
            .let(::removeEmptyLines)
            .let { expandTabs(it, tabSize) }
    }
}
