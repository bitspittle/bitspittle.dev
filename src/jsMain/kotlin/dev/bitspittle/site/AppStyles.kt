package dev.bitspittle.site

import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.lightened
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.InitSilk
import com.varabyte.kobweb.silk.InitSilkContext
import com.varabyte.kobweb.silk.components.graphics.ImageStyle
import com.varabyte.kobweb.silk.components.layout.DividerStyle
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.SilkPalette
import com.varabyte.kobweb.silk.theme.colors.SilkPalettes
import com.varabyte.kobweb.silk.theme.registerBaseStyle
import com.varabyte.kobweb.silk.theme.replaceComponentStyleBase
import com.varabyte.kobweb.silk.theme.shapes.Rect
import com.varabyte.kobweb.silk.theme.shapes.clip
import com.varabyte.kobweb.silk.theme.toSilkPalette
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.*

const val COLOR_MODE_KEY = "bitspittledev:app:colorMode"

val BLOCK_MARGIN = Modifier.margin(top = 1.cssRem)
private val HEADER_MARGIN = Modifier.margin(top = 2.cssRem)

private val TEXT_FONT = Modifier.fontFamily("Ubuntu", "Roboto", "Arial", "Helvetica", "sans-serif").fontSize(18.px)
private val CODE_FONT = Modifier.fontFamily("Ubuntu Mono", "Roboto Mono", "Lucida Console", "Courier New", "monospace")

@InitSilk
fun initSilk(ctx: InitSilkContext) {
    ctx.apply {
        config.apply {
            initialColorMode = localStorage.getItem(COLOR_MODE_KEY)?.let { ColorMode.valueOf(it) } ?: ColorMode.DARK

            registerBaseStyle("html") {
                // Always show a vertical scroller, or else our page content shifts when switching from one page that
                // can scroll to one that can't
                Modifier
                    .scrollBehavior(ScrollBehavior.Smooth)
                    .overflowY(Overflow.Scroll)
            }
            registerBaseStyle("body") { TEXT_FONT.lineHeight(1.5) }
            registerBaseStyle("code") { CODE_FONT }
            registerBaseStyle("canvas") { BLOCK_MARGIN }

            registerBaseStyle("p") { BLOCK_MARGIN }
            registerBaseStyle("pre") { BLOCK_MARGIN }
            registerBaseStyle("h1") {
                HEADER_MARGIN
                    .fontSize(2.5.cssRem)
                    .letterSpacing((-1.5).px)
                    .lineHeight(1.1)
            }
            registerBaseStyle("h2") { HEADER_MARGIN.fontSize(2.cssRem) }
            registerBaseStyle("h3") { HEADER_MARGIN.fontSize(1.5.cssRem) }
            registerBaseStyle("h4") { HEADER_MARGIN.fontSize(1.25.cssRem) }
        }

        // The "link visited" color looks a little garish in dark mode. Disable "visited" colors for now by just setting
        // them to the same value as the default color. We might revisit this later.
        val linkDark = Color.rgb(0x1a85ff)
        theme.palettes = SilkPalettes(
            light = ctx.theme.palettes.light.copy(
                color = Colors.Black.lightened(0.2f),
                background = Colors.WhiteSmoke,
                border = Colors.DarkSlateGray,
                link = ctx.theme.palettes.light.link.copy(
                    visited = ctx.theme.palettes.light.link.default
                ),
            ),
            dark = ctx.theme.palettes.dark.copy(
                color = Colors.White.darkened(0.1f),
                background = Color.rgb(15, 15, 25),
                border = Colors.LightSlateGray,
                link = SilkPalette.Link(
                    default = linkDark,
                    visited = linkDark,
                )
            )
        )

        theme.replaceComponentStyleBase(ImageStyle) {
            Modifier
                .clip(Rect(8.px))
                .width(100.percent)
                .styleModifier {
                    property("object-fit", "scale-down")
                }
        }

        theme.replaceComponentStyleBase(DividerStyle) {
            Modifier
                .margin(top = 1.5.cssRem, bottom = 0.5.cssRem)
                .borderTop(1.px, LineStyle.Solid, colorMode.toSilkPalette().border)
                .fillMaxWidth(90.percent)
        }
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