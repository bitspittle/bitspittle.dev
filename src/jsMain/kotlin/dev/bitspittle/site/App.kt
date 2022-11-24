package dev.bitspittle.site

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.AnimatedColorSurfaceVariant
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.*

@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
    SilkApp {
        val colorMode = getColorMode()
        LaunchedEffect(colorMode) {
            localStorage.setItem(COLOR_MODE_KEY, colorMode.name)
        }

        Surface(Modifier.minHeight(100.vh), variant = AnimatedColorSurfaceVariant) {
            content()
        }
    }
}