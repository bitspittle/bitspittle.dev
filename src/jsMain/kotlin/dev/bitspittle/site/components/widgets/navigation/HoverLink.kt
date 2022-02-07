package dev.bitspittle.site.components.widgets.navigation

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.clearFocus
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaLink
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.toSilkPalette
import kotlinx.browser.document
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.vw
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text

val HoverLinkeStyle = ComponentStyle("bs-hover-link") {
    base { Modifier.opacity(0.percent).transition("opacity .15s").fontSize(0.8.em).margin(left = 0.7.em) }
    link { Modifier.color(colorMode.toSilkPalette().color) }
    visited { Modifier.color(colorMode.toSilkPalette().color) }
    hover { Modifier.opacity(80.percent) }
    focus { Modifier.opacity(80.percent) }
}

/**
 * A link icon which appears only when hovered over
 */
@Composable
fun HoverLink(href: String, modifier: Modifier = Modifier) {
    Link(href, HoverLinkeStyle.toModifier().onClick {
        document.activeElement?.clearFocus()
    }.then(modifier)) {
        FaLink()
    }
}
