package dev.bitspittle.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.document.Toc
import com.varabyte.kobweb.silk.components.document.TocBorderedVariant
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import dev.bitspittle.site.components.widgets.blog.ArticleMetadata
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*

@InitSilk
fun initHighlightJs(ctx: InitSilkContext) {
    // Tweaks to make output from highlight.js look softer / better
    ctx.stylesheet.registerStyleBase("code.hljs") { Modifier.borderRadius(8.px) }
}

@Layout(".components.layouts.PageLayout")
@Composable
fun BlogLayout(ctx: PageContext, content: @Composable () -> Unit) {
    val colorMode by ColorMode.currentState
    LaunchedEffect(colorMode) {
        var styleElement = document.querySelector("""link[title="hljs-style"]""")
        if (styleElement == null) {
            styleElement = document.createElement("link").apply {
                setAttribute("type", "text/css")
                setAttribute("rel", "stylesheet")
                setAttribute("title", "hljs-style")
            }.also { document.head!!.appendChild(it) }
        }
        styleElement.setAttribute("href", "/highlight.js/styles/a11y-${colorMode.name.lowercase()}.min.css")
    }

    LaunchedEffect(ctx.route) {
        // See kobweb config in build.gradle.kts which sets up highlight.js
        js("hljs.highlightAll()")
    }

    ArticleMetadata()
    Toc(
        Modifier.fillMaxWidth(),
        variant = TocBorderedVariant,
        minHeaderLevel = (ctx.data["toc-min"] as? String)?.toIntOrNull() ?: 2,
        maxHeaderLevel = (ctx.data["toc-max"] as? String)?.toIntOrNull() ?: 3,
    )
    content()
}
