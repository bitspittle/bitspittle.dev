package dev.bitspittle.site.components.widgets.blog.kotlinsite

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.graphics.Canvas2d
import com.varabyte.kobweb.silk.components.graphics.ONE_FRAME_MS_60_FPS
import com.varabyte.kobweb.silk.theme.colors.shifted
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.w3c.dom.CanvasLineCap
import org.w3c.dom.CanvasState
import org.w3c.dom.ROUND
import kotlin.js.Date
import kotlin.math.PI

@Composable
fun DemoWidget() {
    Box(Modifier.fillMaxWidth().padding(topBottom = 1.cssRem, leftRight = 0.px), contentAlignment = Alignment.Center) {
        Clock()
    }
}

fun CanvasState.save(block: () -> Unit) {
    save()
    block()
    restore()
}

@Composable
private fun Clock() {
    // We technically only need to update once per second, but let's be a bit more aggressive to capture color
    // changes faster if they happen
    Canvas2d(300, 300, minDeltaMs = ONE_FRAME_MS_60_FPS * 5) {
        // Adapted from: https://developer.mozilla.org/en-US/docs/Web/API/Canvas_API/Tutorial/Basic_animations#an_animated_clock
        val date = Date()
        val r = 150.0

        // Let's be a little lazy and use some colors from the palette which is already color mode aware
        val palette = colorMode.toSilkPalette()
        val colorBorder = palette.button.pressed
        val colorTicks = palette.color
        val colorHourHand = palette.link.default
        val colorMinuteHand = palette.link.default.shifted(colorMode, byPercent = 0.4f)
        val colorSecondHand = if (colorMode.isDark()) Colors.HotPink else Colors.Red

        ctx.save {
            ctx.strokeStyle = colorTicks.toCssColor()
            ctx.fillStyle = palette.background.toCssColor()

            ctx.clearRect(0.0, 0.0, width.toDouble(), height.toDouble())
            ctx.translate(r, r)
            ctx.rotate(-PI / 2)
            ctx.lineWidth = 8.0
            ctx.lineCap = CanvasLineCap.ROUND

            // Hour marks
            ctx.save {
                for (i in 0 until 12) {
                    ctx.beginPath()
                    ctx.rotate(PI / 6)
                    ctx.moveTo(100.0, 0.0)
                    ctx.lineTo(120.0, 0.0)
                    ctx.stroke()
                }
            }

            // Minute marks
            ctx.save {
                ctx.lineWidth = 5.0
                for (i in 0 until 60) {
                    if (i % 5 != 0) {
                        ctx.beginPath()
                        ctx.moveTo(117.0, 0.0)
                        ctx.lineTo(120.0, 0.0)
                        ctx.stroke()
                    }
                    ctx.rotate(PI / 30)
                }
            }

            val sec = date.getSeconds()
            val min = date.getMinutes()
            val hr = date.getHours() % 12

            // Hour hand
            ctx.save {
                ctx.strokeStyle = colorHourHand.toCssColor()
                ctx.rotate(hr * (PI / 6) + (PI / 360) * min + (PI / 21600) * sec)
                ctx.lineWidth = 14.0
                ctx.beginPath()
                ctx.moveTo(-20.0, 0.0)
                ctx.lineTo(80.0, 0.0)
                ctx.stroke()
            }

            // write Minutes
            ctx.save {
                ctx.strokeStyle = colorMinuteHand.toCssColor()
                ctx.rotate((PI / 30) * min + (PI / 1800) * sec)
                ctx.lineWidth = 10.0
                ctx.beginPath()
                ctx.moveTo(-28.0, 0.0)
                ctx.lineTo(112.0, 0.0)
                ctx.stroke()
            }

            // Write seconds
            ctx.save {
                ctx.rotate(sec * PI / 30)
                ctx.strokeStyle = colorSecondHand.toCssColor()
                ctx.fillStyle = colorSecondHand.toCssColor()
                ctx.lineWidth = 6.0
                ctx.beginPath()
                ctx.moveTo(-30.0, 0.0)
                ctx.lineTo(83.0, 0.0)
                ctx.stroke()
                ctx.beginPath()
                ctx.arc(0.0, 0.0, 10.0, 0.0, PI * 2, true)
                ctx.fill()

                // The loop at the end of the second hand
                ctx.beginPath()
                ctx.arc(95.0, 0.0, 10.0, 0.0, PI * 2, true)
                ctx.stroke()
                ctx.fillStyle = Colors.Transparent.toCssColor()
                ctx.arc(0.0, 0.0, 3.0, 0.0, PI * 2, true)
                ctx.fill()
            }

            // The outer circle that frames the clock
            ctx.beginPath()
            ctx.lineWidth = 14.0
            ctx.strokeStyle = colorBorder.toCssColor()
            ctx.arc(0.0, 0.0, 142.0, 0.0, PI * 2, true)
            ctx.stroke()
        }
    }
}
