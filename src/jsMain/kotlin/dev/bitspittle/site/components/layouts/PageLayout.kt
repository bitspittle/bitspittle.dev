package dev.bitspittle.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.Text
import dev.bitspittle.site.components.sections.Footer
import dev.bitspittle.site.components.sections.NavHeader
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.H1

@Composable
fun PageLayout(title: String, description: String = "", content: @Composable ColumnScope.() -> Unit) {
    LaunchedEffect(title) {
        document.title = "$title - Bitspittle.dev"
        document.querySelector("""meta[name="description"]""")!!.setAttribute("content", description)
    }

    Box(Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxSize().maxWidth(800.px).align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavHeader()
            Column(Modifier.fillMaxWidth(75.percent)) {
                H1 { Text(title) }
                content()
            }
            Footer()
        }
    }
}