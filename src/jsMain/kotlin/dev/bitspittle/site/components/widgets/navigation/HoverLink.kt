package dev.bitspittle.site.components.widgets.navigation

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.clearFocus
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.InitSilk
import com.varabyte.kobweb.silk.InitSilkContext
import com.varabyte.kobweb.silk.components.icons.fa.FaLink
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.theme.registerBaseStyle
import com.varabyte.kobweb.silk.theme.toSilkPalette
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*

private val SHOWN_LINK_OPACITY = 80.percent

@InitSilk
fun initHeaderLinkInteraction(ctx: InitSilkContext) {
    (2..6).forEach { headingLevel ->
        // By making the header full width, it means when the user mouses over the entire line they'll see the link
        ctx.config.registerBaseStyle("h${headingLevel}") {
            Modifier.fillMaxWidth()
        }
        ctx.config.registerBaseStyle("h${headingLevel}:hover > .bs-hover-link") {
            Modifier.opacity(SHOWN_LINK_OPACITY)
        }
    }
}


val HoverLinkStyle = ComponentStyle("bs-hover-link") {
    base { Modifier.opacity(0.percent).transition("opacity .15s").fontSize(0.8.em).margin(left = 0.7.em) }
    link { Modifier.color(colorMode.toSilkPalette().color) }
    visited { Modifier.color(colorMode.toSilkPalette().color) }
    focus { Modifier.opacity(SHOWN_LINK_OPACITY) }
}

/**
 * A link icon which appears only when hovered over
 */
@Composable
fun HoverLink(href: String, modifier: Modifier = Modifier) {
    Link(href, HoverLinkStyle.toModifier().onClick {
        document.activeElement?.clearFocus()
    }.then(modifier)) {
        FaLink()
    }
}
