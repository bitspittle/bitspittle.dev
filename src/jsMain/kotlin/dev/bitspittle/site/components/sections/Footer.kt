package dev.bitspittle.site.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.css.*

val FooterStyle = ComponentStyle.base("bs-footer") {
    Modifier
        .margin(top = 2.cssRem)
        .borderTop(1.px, LineStyle.Solid, SilkTheme.palettes[colorMode].color.toCssColor())
        .padding(topBottom = 1.cssRem, leftRight = 4.cssRem)
        .transitionProperty("border-color")
}

val CopyrightStyle = ComponentStyle.base("bs-copyright") {
    Modifier.opacity(0.6).fontSize(0.8.cssRem)
}

@Composable
private fun FooterLink(href: String, content: @Composable () -> Unit) {
    Link(href, variant = UncoloredLinkVariant, content = content)
}

@Composable
fun Footer(modifier: Modifier = Modifier) {
    Column(FooterStyle.toModifier().then(modifier), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.Center) {
            Text("This site is ")
            Link("https://github.com/bitspittle/bitspittle.dev", "open source")
            Text(" written using ")
            Link("https://github.com/varabyte/kobweb", "Kobweb")
            Text(".")
        }

        Row(Modifier.justifyContent(JustifyContent.SpaceAround).width(12.cssRem).margin(top = 1.cssRem, bottom = 1.cssRem)) {
            FooterLink("https://twitter.com/bitspittle") { FaTwitter() }
            FooterLink("https://github.com/bitspittle") { FaGithub() }
            FooterLink("https://www.linkedin.com/in/hermandave") { FaLinkedin() }
            FooterLink("mailto:bitspittle+fromblog@gmail.com") { FaEnvelope() }
        }

        Row {
            Text("© 2022, David Herman", CopyrightStyle.toModifier())
        }
    }
}