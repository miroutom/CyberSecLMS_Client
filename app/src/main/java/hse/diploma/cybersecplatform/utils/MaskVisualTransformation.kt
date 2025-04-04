package hse.diploma.cybersecplatform.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MaskVisualTransformation(private val mask: String) : VisualTransformation {
    private val specialSymbolsIndices = mask.indices.filter { mask[it] != '#' }

    override fun filter(text: AnnotatedString): TransformedText {
        var out = ""
        var maskIndex = 0
        text.forEach { char ->
            while (maskIndex < mask.length && mask[maskIndex] != '#') {
                out += mask[maskIndex]
                maskIndex++
            }
            out += char
            maskIndex++
        }
        return TransformedText(AnnotatedString(out), offsetTranslator)
    }

    private val offsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            val offsetValue = offset.coerceAtMost(mask.length)
            return offset + specialSymbolsIndices.count { it < offsetValue }
        }

        override fun transformedToOriginal(offset: Int): Int {
            return offset - specialSymbolsIndices.count { it < offset }
        }
    }
}
