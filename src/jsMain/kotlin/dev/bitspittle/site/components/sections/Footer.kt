package dev.bitspittle.site.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaDiscord
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.icons.fa.FaTwitter
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.P

val FooterStyle = ComponentStyle.base("bs-footer") {
    Modifier
        .margin(top = 2.cssRem)
        .borderTop(1.px, LineStyle.Solid, SilkTheme.palettes[colorMode].color.toCssColor())
        .padding(topBottom = .75.cssRem, leftRight = 4.cssRem)
        .transitionProperty("border-color")
}

@Composable
fun Footer(modifier: Modifier = Modifier) {
    Column(FooterStyle.toModifier().then(modifier), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.justifyContent(JustifyContent.SpaceAround).width(100.px)) {
            A("https://twitter.com/bitspittle") { FaTwitter() }
            A("https://github.com/bitspittle/bitspittle.dev/") { FaGithub() }
            A("https://discord.gg/5NZ2GKV5Cs") { FaDiscord() }
        }

        Row {
            Text("This site is ")
            Link("https://github.com/bitspittle/bitspittle.dev", "open source")
            Text(" written using ")
            Link("https://github.com/varabyte/kobweb", "Kobweb")
            Text(".")
        }
    }
}