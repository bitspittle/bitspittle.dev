package dev.bitspittle.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.InitSilk
import com.varabyte.kobweb.silk.InitSilkContext
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.silk.theme.registerBaseStyle
import com.varabyte.kobwebx.markdown.markdown
import dev.bitspittle.site.components.widgets.blog.ArticleMetadata
import dev.bitspittle.site.components.widgets.blog.Toc
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*

@InitSilk
fun initHighlightJs(ctx: InitSilkContext) {
    // Tweaks to make output from highlight.js look softer / better
    ctx.config.registerBaseStyle("code.hljs") { Modifier.borderRadius(8.px) }
}

@Composable
fun BlogLayout(content: @Composable ColumnScope.() -> Unit) {
    val ctx = rememberPageContext()
    val mdCtx = ctx.markdown ?: error("BlogLayout only expected to be called from a Markdown file")
    val title = mdCtx.frontMatter["title"]?.singleOrNull() ?: error("Blog should specify title")
    val desc = mdCtx.frontMatter["description"]?.singleOrNull() ?: error("Blog should specify description")

    PageLayout(title, desc) {
        val colorMode by rememberColorMode()
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

        LaunchedEffect(window.location.href) {
            // See kobweb config in build.gradle.kts which sets up highlight.js
            js("hljs.highlightAll()")
        }

        ArticleMetadata()
        Toc(
            minHeaderLevel = mdCtx.frontMatter["toc-min"]?.singleOrNull()?.toIntOrNull() ?: 2,
            maxHeaderLevel = mdCtx.frontMatter["toc-max"]?.singleOrNull()?.toIntOrNull() ?: 3,
        )
        content()
    }
}