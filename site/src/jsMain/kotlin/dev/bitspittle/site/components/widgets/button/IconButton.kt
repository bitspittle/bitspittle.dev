package dev.bitspittle.site.components.widgets.button

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.web.css.*

@Composable
fun IconButton(onClick: (SyntheticMouseEvent) -> Unit, modifier: Modifier = Modifier, icon: @Composable BoxScope.() -> Unit) {
    Button(onClick, modifier.padding(0.px).clip(Circle())) {
        Box(Modifier.padding(8.px), content = icon)
    }
}