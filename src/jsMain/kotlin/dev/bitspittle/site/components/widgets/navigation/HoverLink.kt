package dev.bitspittle.site.components.widgets.navigation

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaLink
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.vw
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text

val HoverLinkeStyle = ComponentStyle("bs-hover-link") {
    base { Modifier.opacity(20.percent).transition("opacity .15s").fontSize(1.cssRem).margin(left = 0.5.cssRem) }
    hover { Modifier.opacity(80.percent) }
    link { Modifier.color(colorMode.toSilkPalette().color) }
    visited { Modifier.color(colorMode.toSilkPalette().color) }
}

/**
 * A link icon which appears only when hovered over
 */
@Composable
fun HoverLink(href: String, modifier: Modifier = Modifier) {
    Link(href, HoverLinkeStyle.toModifier().then(modifier)) {
        FaLink()
    }
}
