package dev.bitspittle.site.components.widgets.dom

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Hr as JbHr

val HrStyle = ComponentStyle.base("bs-hr") {
    Modifier
        .margin(top = 1.5.cssRem, bottom = 0.5.cssRem)
        .borderColor(colorMode.toSilkPalette().border)
        .fillMaxWidth(90.percent)
}

@Composable
fun Hr() {
    JbHr(HrStyle.toModifier().asAttributesBuilder())
}