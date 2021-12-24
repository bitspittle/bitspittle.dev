package io.github.bitspittle.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.Text
import io.github.bitspittle.site.components.sections.Footer
import io.github.bitspittle.site.components.sections.NavHeader
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.H1

@Composable
fun PageLayout(title: String, description: String = "", content: @Composable () -> Unit) {
    LaunchedEffect(title) {
        document.title = "$title - Bitspittle.dev"
        document.querySelector("""meta[name="description"]""")!!.setAttribute("content", description)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavHeader()
        H1 { Text(title) }
        content()
        Footer()

    }
}