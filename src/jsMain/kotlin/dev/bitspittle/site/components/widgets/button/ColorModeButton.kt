package dev.bitspittle.site.components.widgets.button

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.silk.components.icons.fa.FaMoon
import com.varabyte.kobweb.silk.components.icons.fa.FaSun
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import org.jetbrains.compose.web.css.DisplayStyle

@Composable
fun ColorModeButton(modifier: Modifier = Modifier.display(DisplayStyle.InlineBlock)) {
    var colorMode by rememberColorMode()
    IconButton(
        onClick = { colorMode = colorMode.opposite() },
        modifier
    ) {
        when (colorMode) {
            ColorMode.LIGHT -> FaMoon()
            ColorMode.DARK -> FaSun()
        }
    }
}