package dev.bitspittle.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.data.getValue
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.document.Toc
import com.varabyte.kobweb.silk.components.document.TocBorderedVariant
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobwebx.markdown.markdown
import dev.bitspittle.site.components.widgets.blog.ArticleMetadata
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*

@InitSilk
fun initHighlightJs(ctx: InitSilkContext) {
    // Tweaks to make output from highlight.js look softer / better
    ctx.stylesheet.registerStyleBase("code.hljs") { Modifier.borderRadius(8.px) }
}

class BlogData(
    val author: String,
    val date: String,
    val updated: String?,
)

// What heading level to start and stop showing
class TocData(
    val minLevel: Int,
    val maxLevel: Int,
)

@InitRoute
fun initBlogLayout(ctx: InitRouteContext) {
    val fm = ctx.markdown!!.frontMatter
    ctx.data.add(
        PageLayoutData(
            fm["title"]?.singleOrNull() ?: error("Blog should specify 'title'"),
            fm["description"]?.singleOrNull()
        )
    )
    ctx.data.add(
        BlogData(
            fm["author"]?.singleOrNull() ?: error("Blog should specify 'author'"),
            fm["date"]?.singleOrNull() ?: error("Blog should specify 'date'"),
            fm["updated"]?.singleOrNull(),
        )
    )
    ctx.data.add(
        TocData(
            fm["toc-min"]?.singleOrNull()?.toIntOrNull() ?: 2,
            fm["toc-max"]?.singleOrNull()?.toIntOrNull() ?: 3,
        )
    )
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
    val tocData = ctx.data.getValue<TocData>()
    Toc(
        Modifier.fillMaxWidth(),
        variant = TocBorderedVariant,
        minHeaderLevel = tocData.minLevel,
        maxHeaderLevel = tocData.maxLevel,
    )
    content()
}
