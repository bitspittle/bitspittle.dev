package dev.bitspittle.site.components.widgets.blog.koverbadge

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

@Composable
fun GradientBar() {
    Box(Modifier
        .fillMaxWidth()
        .height(20.px)
        .margin(topBottom = 1.cssRem, leftRight = 0.px)
        .styleModifier {
            backgroundImage("linear-gradient(to right, red, green)")
        }
    )
}