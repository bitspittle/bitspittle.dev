package dev.bitspittle.site.components.widgets.blog

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobwebx.markdown.markdown
import org.jetbrains.compose.web.css.*

@Composable
fun ArticleMetadata() {
    val mdCtx = rememberPageContext().markdown!!
    AuthorDate(
        mdCtx.frontMatter.getValue("author").single(),
        mdCtx.frontMatter.getValue("date").single(),
        mdCtx.frontMatter["updated"]?.singleOrNull(),
        Modifier.margin(top = 0.8.cssRem)
    )
}
