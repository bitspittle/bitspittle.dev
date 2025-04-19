package dev.bitspittle.site.components.widgets.blog

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobwebx.markdown.markdown
import org.jetbrains.compose.web.css.*

@Composable
fun ArticleMetadata() {
    val ctx = rememberPageContext()
    AuthorDate(
        (ctx.data["author"] as? String).orEmpty(),
        (ctx.data["date"] as? String).orEmpty(),
        ctx.data["updated"] as? String,
        Modifier.margin(top = 0.8.cssRem, bottom = 1.cssRem)
    )
}
