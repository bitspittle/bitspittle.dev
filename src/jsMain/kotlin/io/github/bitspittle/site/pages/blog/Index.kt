package io.github.bitspittle.site.pages.blog

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.Text
import io.github.bitspittle.site.components.layouts.PageLayout

@Page
@Composable
fun BlogPage() {
    PageLayout("Blog Posts") {
        Text("Coming soon")
    }
}