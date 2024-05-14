package dev.bitspittle.site.components.widgets.code

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

val CodeBlockStyle = CssStyle {
    // For some reason I'm not smart enough to figure out, code blocks are messing up the layout on mobile - they lay
    // themselves out too wide and break out of the central column. Here, we just constrain them to whatever the
    // view width is, until we are on desktop and the column is no longer so small as to cause an issue.
    base { Modifier.maxWidth(90.vw).fillMaxWidth() }
    Breakpoint.MD { Modifier.maxWidth(100.percent) }
}

@Composable
fun CodeBlock(text: String, modifier: Modifier = Modifier, lang: String? = null) {
    Pre(CodeBlockStyle.toModifier().then(modifier).toAttrs()) {
        Code(attrs = {
            classes(lang?.let { "language-$it" } ?: "nohighlight")
        }) {
            Text(text)
        }
    }
}
