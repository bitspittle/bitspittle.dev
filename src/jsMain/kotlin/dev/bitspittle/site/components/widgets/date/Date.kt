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
        // Incoming date timezone is assumed to be UTC, so set it explicitly here to prevent toLocalString from using
        // the local timezone (which could cause off-by-one day errors).
        // See also: https://stackoverflow.com/questions/32877278/tolocaledatestring-is-subtracting-a-day/32877402
        timeZone = "UTC"
    }

    Text(date.toLocaleString("en-US", options), modifier)
}