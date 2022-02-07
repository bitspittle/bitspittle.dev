package dev.bitspittle.site.pages.blog

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.style.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLSpanElement

val ArticleListStyle = ComponentStyle.base("bs-article-list") {
    Modifier.listStyle("none")
}

val ArticleSectionStyle = ComponentStyle.base("bs-article-section") {
    Modifier.fillMaxWidth()
}

val ArticleTitleStyle = ComponentStyle.base("bs-article-title") {
    Modifier
}

val ArticleAuthorDateStyle = ComponentStyle.base("bs-article-author-date") {
    Modifier.opacity(0.6)
}

val ArticleAuthorStyle = ComponentStyle.base("bs-article-author") {
    Modifier
}

val ArticleDateStyle = ComponentStyle("bs-article-date") {
    after {
        Modifier.content("\" • \"")
    }
}

val ArticleNameStyle = ComponentStyle.base("bs-article-name") {
    Modifier
}


val ArticleDescStyle = ComponentStyle.base("bs-article-desc") {
    Modifier.margin(top = 0.3.cssRem)
}

class ArticleEntry(val path: String, val author: String, val date: String, val title: String, val desc: String)

@Composable
fun ArticleList(entries: List<ArticleEntry>) {
    Ul(ArticleListStyle.toModifier().asAttributeBuilder()) {
        entries.forEach { entry ->
            Li {
                ArticleSummary(entry)
            }
        }
    }
}

@Composable
private fun StyledDiv(style: ComponentStyle, content: ContentBuilder<HTMLDivElement>) = Div(style.toModifier().asAttributeBuilder(), content)

@Composable
private fun StyledSpan(style: ComponentStyle, content: ContentBuilder<HTMLSpanElement>) = Span(style.toModifier().asAttributeBuilder(), content)

@Composable
private fun ArticleSummary(entry: ArticleEntry) {
    StyledDiv(ArticleSectionStyle) {
        StyledDiv(ArticleTitleStyle) { Link(entry.path, entry.title) }
        StyledDiv(ArticleAuthorDateStyle) {
            StyledSpan(ArticleDateStyle) { Text(entry.date) }
            StyledSpan(ArticleAuthorStyle) { Text(entry.author) }
        }
        StyledDiv(ArticleDescStyle) { Text(entry.desc) }
    }
}
