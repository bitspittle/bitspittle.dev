package dev.bitspittle.site.components.widgets.date

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.text.Text
import kotlin.js.Date

@Composable
fun DateText(dateStr: String, modifier: Modifier = Modifier) {
    val date = Date(dateStr)
    val options = dateLocaleOptions {
        month = "short"
        day = "numeric"
        year = "numeric"
    }

    Text(date.toLocaleString("en-US", options), modifier)
}