package dev.bitspittle.site.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.saturate
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.fa.FaMastodon
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.overlay.PopupPlacement
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.defer.deferRender
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerBaseStyle
import com.varabyte.kobweb.silk.theme.toSilkPalette
import dev.bitspittle.site.SitePalettes
import dev.bitspittle.site.components.widgets.button.ColorModeButton
import dev.bitspittle.site.components.widgets.button.IconButton
import org.jetbrains.compose.web.css.*

@InitSilk
fun initNavHeaderStyles(ctx: InitSilkContext) {
    // Trick to avoid text scrolling under our floating nav header when you click on in-page fragments links like
    // `href="#some-section`.
    // See also: https://developer.mozilla.org/en-US/docs/Web/CSS/scroll-margin-top
    (2..6).forEach { headingLevel ->
        ctx.stylesheet.registerBaseStyle("h${headingLevel}") {
            Modifier.scrollMargin(top = 5.cssRem)
        }
    }
}

val NavHeaderStyle = ComponentStyle.base("bs-nav-header", extraModifiers = { SmoothColorStyle.toModifier() }) {
    Modifier
        .fillMaxWidth()
        .padding(left = 1.cssRem, right = 1.cssRem, top = 1.cssRem, bottom = 1.cssRem)
        .fontSize(1.25.cssRem)
        .position(Position.Fixed)
        .top(0.percent)
        .backgroundColor(colorMode.toSilkPalette().background.toRgb().copyf(alpha = 0.65f))
        .backdropFilter(saturate(180.percent), blur(5.px))
        .borderBottom(width = 1.px, style = LineStyle.Solid, color = colorMode.toSilkPalette().border)
}

val NavLinkStyle = ComponentStyle("bs-nav-link") {
    val linkColor = colorMode.toSilkPalette().color

    base { Modifier.margin(topBottom = 0.px, leftRight = 15.px) }

    link { Modifier.color(linkColor) }
    visited { Modifier.color(linkColor) }
}

val LogoVariant = NavLinkStyle.addVariant("logo") {
    val logoColor = SitePalettes[colorMode].brand

    link { Modifier.color(logoColor) }
    visited { Modifier.color(logoColor) }
}

val NavButtonStyle = ComponentStyle.base("bs-nav-button") {
    Modifier.margin(0.px, 10.px).backgroundColor(colorMode.toSilkPalette().background)
}

@Composable
private fun NavLink(path: String, text: String, linkVariant: ComponentVariant? = null) {
    Link(
        path,
        text,
        NavLinkStyle.toModifier(linkVariant),
        UndecoratedLinkVariant,
    )
}

@Composable
fun NavHeader() {
    deferRender {
        Row(NavHeaderStyle.toModifier()) {
            val ctx = rememberPageContext()

            NavLink("/", "\$bs", LogoVariant)
            NavLink("/blog/", "blog")
            Spacer()
            IconButton(
                onClick = { ctx.router.navigateTo("https://fosstodon.org/@bitspittle") },
                NavButtonStyle.toModifier()
            ) {
                FaMastodon()
            }
            Tooltip(ElementTarget.PreviousSibling, "Mastodon", placement = PopupPlacement.Bottom)
            ColorModeButton(NavButtonStyle.toModifier())
            Tooltip(ElementTarget.PreviousSibling, "Toggle color mode", placement = PopupPlacement.BottomRight)
        }
    }
}