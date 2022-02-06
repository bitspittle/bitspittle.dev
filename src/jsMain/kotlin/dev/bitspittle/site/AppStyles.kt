package dev.bitspittle.site

import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.InitSilk
import com.varabyte.kobweb.silk.InitSilkContext
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.SilkPalette
import com.varabyte.kobweb.silk.theme.colors.SilkPalettes
import com.varabyte.kobweb.silk.theme.registerBaseStyle
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.fontFamily
import org.jetbrains.compose.web.css.px

const val COLOR_MODE_KEY = "bitspittledev:app:colorMode"

val BLOCK_MARGIN = Modifier.margin(top = 1.cssRem)
private val HEADER_MARGIN = Modifier.margin(top = 1.5.em)

private val TEXT_FONT = Modifier.fontFamily("Ubuntu", "Roboto", "Arial", "Helvetica", "sans-serif")
private val CODE_FONT = Modifier.fontFamily("Ubuntu Mono", "Roboto Mono", "Lucida Console", "Courier New", "monospace")

@InitSilk
fun initSilk(ctx: InitSilkContext) {
    ctx.apply {
        config.apply {
            initialColorMode = localStorage.getItem(COLOR_MODE_KEY)?.let { ColorMode.valueOf(it) } ?: ColorMode.DARK

            registerBaseStyle("html") { Modifier.scrollBehavior(ScrollBehavior.Smooth) }
            registerBaseStyle("body") { TEXT_FONT.lineHeight(1.5) }
            registerBaseStyle("code") { CODE_FONT }
            registerBaseStyle("canvas") { BLOCK_MARGIN }

            registerBaseStyle("p") { BLOCK_MARGIN }
            registerBaseStyle("pre") { BLOCK_MARGIN }
            registerBaseStyle("h1") { HEADER_MARGIN.fontSize(2.5.cssRem) }
            registerBaseStyle("h2") { HEADER_MARGIN.fontSize(2.cssRem) }
            registerBaseStyle("h3") { HEADER_MARGIN.fontSize(1.5.cssRem) }
            registerBaseStyle("h4") { HEADER_MARGIN.fontSize(1.25.cssRem) }
        }

        theme.palettes = SilkPalettes(
            light = ctx.theme.palettes.light.copy(
                color = Colors.CornflowerBlue.darkened(),
                background = Colors.WhiteSmoke,
                link = SilkPalette.Link(
                    default = Colors.DarkTurquoise,
                    visited = Colors.MediumOrchid,
                ),
                button = ctx.theme.palettes.light.button.copy(default = Colors.WhiteSmoke)
            ),
            dark = ctx.theme.palettes.dark.copy(
                color = Colors.CornflowerBlue,
                link = SilkPalette.Link(
                    default = Colors.Cyan,
                    visited = Colors.Thistle,
                )
            )
        )
    }
}


class SitePalette(
    val brand: Color
)

object SitePalettes {
    private val sitePalettes = mapOf(
        ColorMode.LIGHT to SitePalette(brand = Color.rgb(0x009900)),
        ColorMode.DARK to SitePalette(brand = Color.rgb(0x04f904)),
    )

    operator fun get(colorMode: ColorMode) = sitePalettes.getValue(colorMode)
}