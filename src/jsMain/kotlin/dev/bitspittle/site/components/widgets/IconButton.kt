package dev.bitspittle.site.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import org.jetbrains.compose.web.css.*

@Composable
fun IconButton(onClick: () -> Unit, modifier: Modifier = Modifier, icon: @Composable BoxScope.() -> Unit) {
    Button(onClick, modifier) {
        Box(Modifier.padding(8      .px), content = icon)
    }
}