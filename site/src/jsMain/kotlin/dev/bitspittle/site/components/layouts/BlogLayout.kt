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
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobwebx.markdown.markdown
import dev.bitspittle.site.components.widgets.blog.ArticleMetadata
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Document
import org.w3c.dom.DocumentReadyState
import org.w3c.dom.LOADING
import kotlin.js.json

class BlogData(
    val author: String,
    val date: String,
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
        if (document.querySelector("""link[title="hljs-style"]""") == null) {
            val styleElement = document.createElement("link").apply {
                setAttribute("type", "text/css")
                setAttribute("rel", "stylesheet")
                setAttribute("title", "hljs-style")
            }
            document.head!!.appendChild(styleElement)
        }
    }

    LaunchedEffect(ctx.route.path) {
        // See kobweb config in build.gradle.kts which sets up Prism
        js("Prism.highlightAll()")
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

    // We notice weird issues with the page not scrolling to the right section if the URL has a fragment included, due
    // to dynamic content still being rendered / processed after the browser has decided where to jump to. So as a
    // workaround, we temporarily remove the id from any matching element and then add it back in only after the page
    // finishes loading.
    LaunchedEffect(Unit) {
        val hash = window.location.hash
        if (hash.isBlank()) return@LaunchedEffect

        val targetId = hash.removePrefix("#")
        val element = document.getElementById(targetId) ?: return@LaunchedEffect

        // Temporarily remove ID to block browser's default jump
        element.id = ""

        // Add this into Kobweb?
        fun Document.onDomReady(block: () -> Unit) {
            if (readyState != DocumentReadyState.LOADING) {
                block()
            } else {
                addEventListener("DOMContentLoaded", { block() }, options = json("once" to true))
            }
        }

        document.onDomReady {
            element.id = targetId
            element.scrollIntoView()
        }
    }
}
