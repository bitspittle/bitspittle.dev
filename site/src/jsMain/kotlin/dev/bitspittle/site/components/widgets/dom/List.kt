package dev.bitspittle.site.components.widgets.dom

import com.varabyte.kobweb.compose.css.ListStyleType
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import org.jetbrains.compose.web.css.*

// Remove list indentation
// See also: https://stackoverflow.com/a/13939142/17966710
val NoListIndentationModifier = Modifier.listStyle(ListStyleType.None).padding(left = 0.px)
