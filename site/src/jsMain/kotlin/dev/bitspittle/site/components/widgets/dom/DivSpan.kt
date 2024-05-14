package dev.bitspittle.site.components.widgets.dom

import androidx.compose.runtime.*
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.GeneralKind
import com.varabyte.kobweb.silk.style.toAttrs
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLSpanElement

@Composable
fun StyledDiv(style: CssStyle<GeneralKind>, content: ContentBuilder<HTMLDivElement>) = Div(style.toAttrs(), content)

@Composable
fun StyledSpan(style: CssStyle<GeneralKind>, content: ContentBuilder<HTMLSpanElement>) = Span(style.toAttrs(), content)
