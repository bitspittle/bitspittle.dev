package dev.bitspittle.site.components.widgets.blog.koverbadge

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.functions.LinearGradient
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import org.jetbrains.compose.web.css.*

@Composable
fun GradientBar() {
    Box(Modifier
        .fillMaxWidth()
        .height(20.px)
        .margin(topBottom = 1.cssRem, leftRight = 0.px)
        .backgroundImage(linearGradient(Colors.Red, Colors.Green, LinearGradient.Direction.ToRight))
    )
}