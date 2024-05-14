package dev.bitspittle.site.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.ElementTarget
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.saturate
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
import com.varabyte.kobweb.silk.defer.deferRender
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.style.*
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.selectors.link
import com.varabyte.kobweb.silk.style.selectors.visited
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.border
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import dev.bitspittle.site.brand
import dev.bitspittle.site.components.widgets.button.ColorModeButton
import dev.bitspittle.site.components.widgets.button.IconButton
import org.jetbrains.compose.web.css.*

@InitSilk
fun initNavHeaderStyles(ctx: InitSilkContext) {
    // Trick to avoid text scrolling under our floating nav header when you click on in-page fragments links like
    // `href="#some-section`.
    // See also: https://developer.mozilla.org/en-US/docs/Web/CSS/scroll-margin-top
    (2..6).forEach { headingLevel ->
        ctx.stylesheet.registerStyleBase("h${headingLevel}") {
            Modifier.scrollMargin(top = 5.cssRem)
        }
    }
}

val NavHeaderStyle = CssStyle.base(extraModifier = { SmoothColorStyle.toModifier() }) {
    Modifier
        .fillMaxWidth()
        .padding(left = 1.cssRem, right = 1.cssRem, top = 1.cssRem, bottom = 1.cssRem)
        .fontSize(1.25.cssRem)
        .position(Position.Fixed)
        .top(0.percent)
        .backgroundColor(colorMode.toPalette().background.toRgb().copyf(alpha = 0.65f))
        .backdropFilter(saturate(180.percent), blur(5.px))
        .borderBottom(width = 1.px, style = LineStyle.Solid, color = colorMode.toPalette().border)
}

sealed interface NavLinkKind : ComponentKind
val NavLinkStyle = CssStyle<NavLinkKind> {
    val linkColor = colorMode.toPalette().color

    base { Modifier.margin(topBottom = 0.px, leftRight = 15.px) }

    link { Modifier.color(linkColor) }
    visited { Modifier.color(linkColor) }
}

val LogoVariant = NavLinkStyle.addVariant {
    val logoColor = colorMode.toPalette().brand

    link { Modifier.color(logoColor) }
    visited { Modifier.color(logoColor) }
}

val NavButtonStyle = CssStyle.base {
    Modifier.margin(0.px, 10.px).backgroundColor(colorMode.toPalette().background)
}

@Composable
private fun NavLink(path: String, text: String, linkVariant: CssStyleVariant<NavLinkKind>? = null) {
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
