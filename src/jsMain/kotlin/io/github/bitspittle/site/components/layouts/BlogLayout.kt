package io.github.bitspittle.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobwebx.markdown.markdown

@Composable
fun BlogLayout(content: @Composable ColumnScope.() -> Unit) {
    val ctx = rememberPageContext()
    val title = ctx.markdown.frontMatter["title"]?.singleOrNull() ?: error("Blog should specify title")
    val desc = ctx.markdown.frontMatter["description"]?.singleOrNull() ?: error("Blog should specify description")

    PageLayout(title, desc) {
        content()
    }
}