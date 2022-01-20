package dev.bitspittle.site.components.widgets.text

import androidx.compose.runtime.*
import com.varabyte.kobweb.silk.components.text.Text
import org.jetbrains.compose.web.dom.P

@Composable
fun Paragraph(text: String) {
    P {
        Text(text)
    }
}