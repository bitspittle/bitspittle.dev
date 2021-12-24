package io.github.bitspittle.site.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.FaMoon
import com.varabyte.kobweb.silk.components.icons.fa.FaSun
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.link
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.style.visited
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.*

val NavHeaderStyle = ComponentStyle.base("bs-nav-header") {
    Modifier
        .fillMaxWidth()
        .height(3.cssRem)
        // Intentionally invert the nav header color so that it is distinct from the page
        .backgroundColor(colorMode.toSilkPalette().color)
}

private val NAV_ITEM_MARGIN = Modifier.margin(0.px, 15.px)

val NavLinkStyle = ComponentStyle("bs-nav-link") {
    // Intentionally invert the color for nav links since we inverted the nav header
    val linkColor = colorMode.toSilkPalette().background

    base { NAV_ITEM_MARGIN }
    link { Modifier.color(linkColor) }
    visited { Modifier.color(linkColor) }
}

val NavButtonStyle = ComponentStyle.base("bs-nav-button") {
    NAV_ITEM_MARGIN.clip(Circle())
}

@Composable
private fun NavLink(path: String, text: String) {
    Link(
        path,
        text,
        NavLinkStyle.toModifier(),
        UndecoratedLinkVariant,
    )
}

@Composable
fun NavHeader() {
    var colorMode by rememberColorMode()
    Box(
        NavHeaderStyle.toModifier()
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavLink("/", "HOME")
            NavLink("/about", "ABOUT")
            NavLink("/markdown", "MARKDOWN")
            Spacer()
            Button(
                onClick = { colorMode = colorMode.opposite() },
                NavButtonStyle.toModifier()
            ) {
                Box(Modifier.margin(6.px)) {
                    when (colorMode) {
                        ColorMode.LIGHT -> FaMoon()
                        ColorMode.DARK -> FaSun()
                    }
                }
            }
        }
    }
}