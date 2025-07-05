package util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration

class WhitespaceAndUnicodeVisualTransformation(
    private val tabSize: Int = 4
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val transformed = StringBuilder()
        val originalToTransformedMap = mutableListOf<Int>()
        val transformedToOriginalMap = mutableListOf<Int>()

        var transformedIndex = 0
        for ((i, char) in original.withIndex()) {
            when {
                char == '\t' -> {
                    val spaces = " ".repeat(tabSize)
                    transformed.append(spaces)
                    repeat(tabSize) {
                        transformedToOriginalMap.add(i)
                    }
                    originalToTransformedMap.add(transformedIndex)
                    transformedIndex += tabSize
                }

                char.code > 127 -> {
                    // Non-ASCII char, add with style
                    transformed.append(char)
                    transformedToOriginalMap.add(i)
                    originalToTransformedMap.add(transformedIndex)
                    transformedIndex += 1
                }

                else -> {
                    transformed.append(char)
                    transformedToOriginalMap.add(i)
                    originalToTransformedMap.add(transformedIndex)
                    transformedIndex += 1
                }
            }
        }

        val styled = buildAnnotatedString {
            original.forEachIndexed { i, char ->
                when {
                    char == '\t' -> append(" ".repeat(tabSize))
                    char.code > 127 -> {
                        pushStyle(
                            SpanStyle(color = Color.Red, textDecoration = TextDecoration.Underline)
                        )
                        append(char)
                        pop()
                    }

                    else -> append(char)
                }
            }
        }

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset >= originalToTransformedMap.size) return transformed.length
                return originalToTransformedMap[offset]
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset >= transformedToOriginalMap.size) return original.length
                return transformedToOriginalMap[offset]
            }
        }

        return TransformedText(styled, mapping)
    }
}

