package dev.bitspittle.site.components.widgets.blog

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.data.getValue
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobwebx.markdown.markdown
import dev.bitspittle.site.components.layouts.BlogData
import org.jetbrains.compose.web.css.*

@Composable
fun ArticleMetadata() {
    val ctx = rememberPageContext()
    val blogData = ctx.data.getValue<BlogData>()
    AuthorDate(
        blogData.author,
        blogData.date,
        blogData.updated,
        Modifier.margin(top = 0.8.cssRem, bottom = 1.cssRem)
    )
}
