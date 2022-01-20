package dev.bitspittle.site.components.widgets.code

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.shifted
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Text

val InlineCodeStyle = ComponentStyle.base("bs-code") {
    Modifier.backgroundColor(colorMode.toSilkPalette().background.shifted(colorMode, 0.2f)).padding(topBottom = 3.px, leftRight = 5.px).borderRadius(5.px)
}

@Composable
fun InlineCode(text: String, modifier: Modifier = Modifier) {
    Code(attrs = InlineCodeStyle.toModifier().then(modifier).asAttributeBuilder()) {
        Text(text)
    }
}
