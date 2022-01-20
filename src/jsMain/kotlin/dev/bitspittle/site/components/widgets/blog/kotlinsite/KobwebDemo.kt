package dev.bitspittle.site.components.widgets.blog.kotlinsite

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import dev.bitspittle.site.BLOCK_MARGIN
import org.jetbrains.compose.web.dom.Source
import org.jetbrains.compose.web.dom.Video

@Composable
fun KobwebDemo() {
    Box(BLOCK_MARGIN, contentAlignment = Alignment.Center) {
        Video(attrs = {
            attr("width", "100%")
            attr("controls", "")
        }) {
            Source(attrs = {
                attr("src", "https://user-images.githubusercontent.com/43705986/135570277-2d67033a-f647-4b04-aac0-88f8992145ef.mp4")
                attr("type", "video/mp4")
            })
        }
    }
}