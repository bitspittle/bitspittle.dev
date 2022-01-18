package dev.bitspittle.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
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

    Box(Modifier.fillMaxWidth().minHeight(100.percent).styleModifier {
        // Two rows, the main content) takes and the footer. "auto" means the use the height of the row.
        //  "1fr" means give the rest of the space to that row. Since this box is set to *at least* 100%, the footer
        // will appear at least on the bottom, unless the first row grows beyond the page.
        gridTemplateRows("1fr auto")
    }, contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier.fillMaxSize().maxWidth(800.px).align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavHeader()
            Column(Modifier.fillMaxWidth(75.percent)) {
                H1 { Text(title) }
                content()
            }
        }
        Footer(Modifier.styleModifier {
            // Associate the footer with the row that will get pushed off the bottom of the page if it can't fit.
            gridRowStart(2); gridRowEnd(3)
        })
    }
}