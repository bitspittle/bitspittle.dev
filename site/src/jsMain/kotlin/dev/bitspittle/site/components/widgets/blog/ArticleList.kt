package dev.bitspittle.site.components.widgets.blog

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontStyle
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.palette.border
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import dev.bitspittle.site.components.widgets.date.DateText
import dev.bitspittle.site.components.widgets.dom.NoListIndentationModifier
import dev.bitspittle.site.components.widgets.dom.StyledDiv
import dev.bitspittle.site.components.widgets.dom.StyledSpan
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

val ArticleListStyle = ComponentStyle.base("bs-article-list") {
    NoListIndentationModifier
}

val ArticleSectionStyle = ComponentStyle.base("bs-article-section") {
    Modifier
        .fillMaxWidth()
        .margin(top = 1.5.cssRem)
        .padding(1.cssRem)
        .border(1.px, LineStyle.Solid, colorMode.toPalette().border)
        .borderRadius(5.px)
}

val ArticleTitleStyle = ComponentStyle.base("bs-article-title") {
    Modifier.fontWeight(FontWeight.Bold)
}

val ArticleMetaStyle = ComponentStyle.base("bs-article-meta") {
    Modifier.opacity(0.6)
}

val ArticleAuthorStyle = ComponentStyle.base("bs-article-author") {
    Modifier
}

val ArticleDateStyle by ComponentStyle(prefix = "bs") {
    after {
        Modifier.content(" • ")
    }
}

val ArticleNameStyle = ComponentStyle.base("bs-article-name") {
    Modifier
}

val ArticleUpdatedStyle = ComponentStyle.base("bs-article-updated") {
    Modifier.fontStyle(FontStyle.Italic)
}

val ArticleDescStyle = ComponentStyle.base("bs-article-desc") {
    Modifier.margin(top = 0.3.cssRem)
}

class ArticleEntry(val path: String, val author: String, val date: String, val title: String, val desc: String)

@Composable
fun ArticleList(entries: List<ArticleEntry>) {
    Ul(ArticleListStyle.toAttrs()) {
        entries.forEach { entry ->
            Li {
                ArticleSummary(entry)
            }
        }
    }
}

@Composable
fun AuthorDate(author: String, date: String, updated: String? = null, modifier: Modifier = Modifier) {
    Div(attrs = ArticleMetaStyle.toModifier().then(modifier).toAttrs()) {
        StyledSpan(ArticleDateStyle) {
            DateText(date)
        }
        StyledSpan(ArticleAuthorStyle) { SpanText(author) }
        if (updated != null) {
            StyledSpan(ArticleUpdatedStyle) {
                Br()
                SpanText("Updated ")
                DateText(updated)
            }
        }
    }
}

@Composable
private fun ArticleSummary(entry: ArticleEntry) {
    StyledDiv(ArticleSectionStyle) {
        StyledDiv(ArticleTitleStyle) { Link(entry.path, entry.title) }
        AuthorDate(entry.author, entry.date)
        StyledDiv(ArticleDescStyle) { SpanText(entry.desc) }
    }
}
