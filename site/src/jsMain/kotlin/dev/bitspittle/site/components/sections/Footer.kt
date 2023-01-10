package dev.bitspittle.site.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.navigation.OpenLinkStrategy
import com.varabyte.kobweb.silk.components.icons.fa.FaEnvelope
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.icons.fa.FaLinkedin
import com.varabyte.kobweb.silk.components.icons.fa.FaMastodon
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

val FooterStyle = ComponentStyle.base("bs-footer") {
    Modifier
        .margin(top = 2.cssRem)
        .borderTop(1.px, LineStyle.Solid, SilkTheme.palettes[colorMode].border)
        .padding(topBottom = 1.cssRem, leftRight = 4.cssRem)
        .transitionProperty("border-color")
}

val CopyrightStyle = ComponentStyle.base("bs-copyright") {
    Modifier.opacity(0.6).fontSize(0.8.cssRem)
}

@Composable
private fun FooterLink(href: String, content: @Composable () -> Unit) {
    Link(href, variant = UncoloredLinkVariant, openExternalLinksStrategy = OpenLinkStrategy.IN_NEW_TAB_FOREGROUND, content = content)
}

@Composable
fun Footer(modifier: Modifier = Modifier) {
    Column(FooterStyle.toModifier().then(modifier), horizontalAlignment = Alignment.CenterHorizontally) {
        Span(Modifier.whiteSpace(WhiteSpace.PreWrap).toAttrs()) {
            Text("This site is ")
            Link("https://github.com/bitspittle/bitspittle.dev", "open source")
            Text(" written using ")
            Link("https://github.com/varabyte/kobweb", "Kobweb")
            Text(".")
        }

        Row(Modifier.justifyContent(JustifyContent.SpaceAround).width(12.cssRem).margin(top = 1.cssRem, bottom = 1.cssRem)) {
            FooterLink("https://fosstodon.org/@bitspittle") { FaMastodon() }
            FooterLink("https://github.com/bitspittle") { FaGithub() }
            FooterLink("https://www.linkedin.com/in/hermandave") { FaLinkedin() }
            FooterLink("mailto:bitspittle+fromblog@gmail.com") { FaEnvelope() }
        }

        Row {
            SpanText("© 2022, David Herman", CopyrightStyle.toModifier())
        }
    }
}