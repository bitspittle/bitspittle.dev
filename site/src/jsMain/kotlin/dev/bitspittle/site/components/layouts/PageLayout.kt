package dev.bitspittle.site.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.data.addIfAbsent
import com.varabyte.kobweb.core.data.getValue
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toAttrs
import com.varabyte.kobwebx.markdown.markdown
import dev.bitspittle.firebase.analytics.Analytics
import dev.bitspittle.firebase.app.FirebaseApp
import dev.bitspittle.firebase.app.FirebaseOptions
import dev.bitspittle.site.components.sections.Footer
import dev.bitspittle.site.components.sections.NavHeader
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

val CenteredSectionStyle = CssStyle {
    base { Modifier.fillMaxWidth(90.percent) }
    Breakpoint.MD { Modifier.fillMaxWidth(80.percent) }
}

class PageLayoutData(
    val title: String,
    val desc: String? = null,
)

@InitRoute
fun initPageLayout(ctx: InitRouteContext) {
    val fm = ctx.markdown?.frontMatter
    ctx.data.addIfAbsent {
        PageLayoutData(fm?.get("title")?.singleOrNull() ?: "Bitspittle.dev")
    }
}

@Composable
@Layout
fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {
    val app = remember {
        FirebaseApp.initialize(
            FirebaseOptions(
                apiKey = "AIzaSyBxcyLIO6QhZWGdAKoOEpqHytpXpVCc1Tc",
                authDomain = "bitspittle-site.firebaseapp.com",
                databaseURL = "https://bitspittle-site-default-rtdb.firebaseio.com",
                projectId = "bitspittle-site",
                storageBucket = "bitspittle-site.appspot.com",
                messagingSenderId = "554672278802",
                appId = "1:554672278802:web:4f39bc1bfd7b15b19337cb",
                measurementId = "G-MEKV58Q308",
            ),
        )
    }
    val analytics = remember { app.getAnalytics() }

    val layoutData = ctx.data.getValue<PageLayoutData>()
    val title = layoutData.title
    LaunchedEffect(title) {
        val description = layoutData.desc ?: "Tech chatter, tutorials, and career advice"
        document.title = "$title - Bitspittle.dev"
        document.querySelector("""meta[name="description"]""")!!.setAttribute("content", description)
    }

    if (window.location.hostname != "localhost") {
        val context = rememberPageContext()
        LaunchedEffect(context) { // Context changing means we definitely visited a new page
            analytics.log(Analytics.Event.PageView())
        }
    }

    Box(Modifier
        .fillMaxWidth()
        .minHeight(100.vh)
        // Create a box with two rows: the main content (fills as much space as it can) and the footer (which reserves
        // space at the bottom). "auto" means the use the height of the row. "1fr" means give the rest of the space to
        // that row. Since this container is set to *at least* 100%, the footer will always appear at least on the
        // bottom but can be pushed further down if the first row (main content) grows beyond the page.
        .gridTemplateRows { size(1.fr); size(auto) }
    , contentAlignment = Alignment.TopCenter) {
        Column(
            // Add some top margin to give some space for where the nav header will appear
            modifier = Modifier.fillMaxSize().maxWidth(800.px).align(Alignment.TopCenter).margin(top = 4.cssRem),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavHeader()
            Div(CenteredSectionStyle.toAttrs()) {
                H1 { SpanText(title) }
                content()
            }
        }
        // Associate the footer with the row that will get pushed off the bottom of the page if it can't fit.
        Footer(Modifier.gridRow(2, 3))
    }
}
