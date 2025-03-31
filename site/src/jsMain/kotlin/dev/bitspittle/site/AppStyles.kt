package dev.bitspittle.site

import com.varabyte.kobweb.browser.storage.createStorageKey
import com.varabyte.kobweb.browser.storage.getItem
import com.varabyte.kobweb.compose.css.ObjectFit
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.lightened
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.isExporting
import com.varabyte.kobweb.silk.components.graphics.ImageStyle
import com.varabyte.kobweb.silk.components.layout.HorizontalDividerStyle
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.*
import com.varabyte.kobweb.silk.theme.colors.systemPreference
import com.varabyte.kobweb.silk.theme.replaceStyleBase
import com.varabyte.kobweb.silk.theme.shapes.Rect
import com.varabyte.kobweb.silk.theme.shapes.clip
import kotlinx.browser.document
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.*

val COLOR_MODE_KEY = ColorMode.entries.createStorageKey("playground:app:colorMode")

private val HEADER_MARGIN = Modifier.marginBlock(start = 2.cssRem)

private val TEXT_FONT = Modifier.fontFamily("Ubuntu", "Roboto", "Arial", "Helvetica", "sans-serif").fontSize(18.px)
private val CODE_FONT = Modifier.fontFamily("Ubuntu Mono", "Roboto Mono", "Lucida Console", "Courier New", "monospace")

@InitSilk
fun initSilk(ctx: InitSilkContext) {
    ctx.apply {
        config.apply {
            initialColorMode = localStorage.getItem(COLOR_MODE_KEY) ?: ColorMode.systemPreference
            // Script which runs at load time that needs to be kept in sync with `initialColorMode` above. This code checks
            // if the user's local color mode preference is different from what was exported by Kobweb, replacing it if
            // different to prevent a flash of color after the page loads.
            if (AppGlobals.isExporting) {
                document.head!!.appendChild(
                    document.createElement("script").apply {
                        textContent = """
                        {
                            const storedColor = localStorage.getItem('${COLOR_MODE_KEY.name}'); // 'LIGHT', 'DARK', or null
                            const desiredColor = storedColor
                                ? `silk-${'$'}{storedColor.toLowerCase()}`
                                : (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'silk-dark' : 'silk-light');
                            const oppositeColor = desiredColor === 'silk-dark' ? 'silk-light' : 'silk-dark';
                            document.documentElement.classList.replace(oppositeColor, desiredColor);
                        }
                        """.trimIndent()
                    })
            }
        }

        stylesheet.apply {
            registerStyle("html") {
                // Always show a vertical scroller, or else our page content shifts when switching from one page that
                // can scroll to one that can't
                base {
                    Modifier.overflow { y(Overflow.Scroll) }
                }
                cssRule(CSSMediaQuery.MediaFeature("prefers-reduced-motion", StylePropertyValue("no-preference"))) {
                    Modifier.scrollBehavior(ScrollBehavior.Smooth)
                }
            }
            registerStyleBase("body") { TEXT_FONT.lineHeight(1.5) }
            registerStyleBase("code") { CODE_FONT }
            registerStyleBase("h1") {
                HEADER_MARGIN
                    .fontSize(2.5.cssRem)
                    .letterSpacing((-1.5).px)
                    .lineHeight(1.1)
            }
            registerStyleBase("h2") { HEADER_MARGIN.fontSize(2.cssRem) }
            registerStyleBase("h3") { HEADER_MARGIN.fontSize(1.5.cssRem) }
            registerStyleBase("h4") { HEADER_MARGIN.fontSize(1.25.cssRem) }
        }

        // The "link visited" color looks a little garish with this site's theme. Disable "visited" colors for now by
        // just setting them to the same value as the default color. We might revisit this later.
        theme.palettes.apply {
            light.apply {
                color = Colors.Black.lightened(0.2f)
                background = Colors.WhiteSmoke
                border = Colors.DarkSlateGray
                link.visited = ctx.theme.palettes.light.link.default
                brand = Color.rgb(0x009900)
            }

            dark.apply {
                color = Colors.White.darkened(0.1f)
                background = Color.rgb(15, 15, 25)
                border = Colors.LightSlateGray
                link.apply {
                    val linkDark = Color.rgb(0x1a85ff)
                    default = linkDark
                    visited = linkDark
                }
                brand = Color.rgb(0x04f904)
            }
        }

        theme.replaceStyleBase(ImageStyle) {
            Modifier
                .clip(Rect(cornerRadius = 8.px))
                .objectFit(ObjectFit.ScaleDown)
                .maxWidth(100.percent)
        }

        theme.replaceStyleBase(HorizontalDividerStyle) {
            Modifier
                .margin(top = 1.5.cssRem, bottom = 0.5.cssRem)
                .borderTop(1.px, LineStyle.Solid, colorMode.toPalette().border)
                .fillMaxWidth(90.percent)
        }
    }
}

private const val BRAND_KEY = "brand"
val Palette.brand get() = (this as MutablePalette).brand
var MutablePalette.brand: Color
    get() = this.getValue(BRAND_KEY)
    set(value) = this.set(BRAND_KEY, value)
