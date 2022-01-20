package dev.bitspittle.site.components.widgets.blog.kotlinsite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.Text
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import com.varabyte.kobweb.silk.theme.colors.shifted
import com.varabyte.kobweb.silk.theme.toSilkPalette
import dev.bitspittle.site.BLOCK_MARGIN
import org.jetbrains.compose.web.css.px

@Composable
fun ArticleSummary() {
    val colorMode by rememberColorMode()

    Column(BLOCK_MARGIN
        .backgroundColor(colorMode.toSilkPalette().background.shifted(0.1f))
        .borderRadius(5.px)
        .padding(5.px)
        .fillMaxWidth()
    ) {
        Row {
            Text("David Herman")
            Text(" • ")
            Link( "https://bitspittle.dev", "Author's webpage")
        }
        Text("This is a pretend article just to showcase this ex...")
    }
}
