package dev.bitspittle.site.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.ElementTarget
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.icons.fa.FaEnvelope
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.icons.fa.FaLinkedin
import com.varabyte.kobweb.silk.components.icons.fa.FaMastodon
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.overlay.PopupPlacement
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.palette.border
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

val FooterStyle = CssStyle.base {
    Modifier
        .margin(top = 2.cssRem)
        .borderTop(1.px, LineStyle.Solid, colorMode.toPalette().border)
        .padding(topBottom = 1.cssRem, leftRight = 4.cssRem)
}

val CopyrightStyle = CssStyle.base {
    Modifier.opacity(0.6).fontSize(0.8.cssRem)
}

@Composable
private fun FooterLink(href: String, content: @Composable () -> Unit) {
    Link(href, variant = UncoloredLinkVariant, content = content)
}

@Composable
fun Footer(modifier: Modifier = Modifier) {
    Column(FooterStyle.toModifier().then(modifier), horizontalAlignment = Alignment.CenterHorizontally) {
        Span(Modifier.whiteSpace(WhiteSpace.PreWrap).textAlign(TextAlign.Center).toAttrs()) {
            Text("This site is ")
            Link("https://github.com/bitspittle/bitspittle.dev", "open source")
            Text(" written using ")
            Link("https://github.com/varabyte/kobweb", "Kobweb")
            Text(".")
        }

        Row(Modifier.justifyContent(JustifyContent.SpaceAround).width(12.cssRem).margin(top = 1.cssRem, bottom = 1.cssRem)) {
            FooterLink("https://fosstodon.org/@bitspittle") { FaMastodon() }
            Tooltip(ElementTarget.PreviousSibling, "Mastodon", placement = PopupPlacement.Top)
            FooterLink("https://github.com/bitspittle") { FaGithub() }
            Tooltip(ElementTarget.PreviousSibling, "GitHub", placement = PopupPlacement.Top)
            FooterLink("https://www.linkedin.com/in/hermandave") { FaLinkedin() }
            Tooltip(ElementTarget.PreviousSibling, "LinkedIn", placement = PopupPlacement.Top)
            FooterLink("mailto:bitspittle+fromblog@gmail.com") { FaEnvelope() }
            Tooltip(ElementTarget.PreviousSibling, "Email", placement = PopupPlacement.Top)
        }

        Row {
            SpanText("© 2025, David Herman", CopyrightStyle.toModifier())
        }
    }
}
