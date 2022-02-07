package dev.bitspittle.site.components.widgets.dom

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.silk.components.style.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLSpanElement

@Composable
fun StyledDiv(style: ComponentStyle, content: ContentBuilder<HTMLDivElement>) = Div(style.toModifier().asAttributesBuilder(), content)

@Composable
fun StyledSpan(style: ComponentStyle, content: ContentBuilder<HTMLSpanElement>) = Span(style.toModifier().asAttributesBuilder(), content)

