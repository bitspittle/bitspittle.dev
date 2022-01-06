package io.github.bitspittle.site.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import io.github.bitspittle.site.components.layouts.PageLayout
import io.github.bitspittle.site.components.widgets.Paragraph

private val PARAGRAPHS = listOf(
    """
        Hello! My name is David Herman.
    """.trimIndent(),
    """
        I am a programmer with 20 years of experience, with the first half in the game industry, and the second half at
        Google. I am currently taking a break from the industry to write Kobweb.
    """.trimIndent(),
    """
        My plan with this site is to share both interactive tutorials as well as career advice gleaned from a long,
        long history of mistakes.
    """.trimIndent(),
)

@Page
@Composable
fun HomePage() {
    PageLayout("Welcome to BitSpittle.dev") {
        PARAGRAPHS.forEach { p -> Paragraph(p) }
    }
}