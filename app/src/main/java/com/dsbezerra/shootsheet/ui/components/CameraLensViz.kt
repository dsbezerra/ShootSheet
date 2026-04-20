/*
 * Designed and developed by 2026 dsbezerra (Diego Bezerra)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dsbezerra.shootsheet.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dsbezerra.shootsheet.data.ScenarioSettings
import com.dsbezerra.shootsheet.data.SettingKey
import com.dsbezerra.shootsheet.ui.theme.Accent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.log
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

// ── Camera Lens Visualization ───────────────────────────────────────────────

@Composable
fun CameraLensViz(
  settings: ScenarioSettings,
  highlighted: SettingKey?,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current

  // ── Parse settings ──────────────────────────────────────────────────────
  val fNum = remember(settings.aperture) { parseFStop(settings.aperture) }
  val targetPct = remember(fNum) { calcAperturePct(fNum) }
  val isoNum = remember(settings.iso) { parseIso(settings.iso) }
  val isoGlow = remember(isoNum) { calcIsoGlow(isoNum) }
  val spinMs = remember(settings.shutter) { calcSpinDurationMs(settings.shutter) }
  val wbColor = remember(settings.wbRes) { calcWbColor(settings.getWb(context)) }
  val fStopLabel = remember(settings.aperture) {
    settings.aperture.split(Regex("[–\\-]")).first().trim()
  }

  // ── Animations ──────────────────────────────────────────────────────────
  val animPct by animateFloatAsState(
    targetValue = targetPct,
    animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow),
    label = "aperture",
  )

  val inf = rememberInfiniteTransition(label = "lens")
  val spin by inf.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(tween(spinMs, easing = LinearEasing)),
    label = "spin",
  )
  val pulse by inf.animateFloat(
    initialValue = 0.7f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(tween(2400), repeatMode = RepeatMode.Reverse),
    label = "pulse",
  )

  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {
    // Layer 1 — ISO ambient glow
    val glowAlpha = (isoGlow * (if (highlighted == SettingKey.ISO) 0.55f else 0.28f) * pulse)
      .coerceIn(0f, 1f)
    if (glowAlpha > 0.01f) {
      Canvas(modifier = Modifier.matchParentSize()) {
        drawCircle(
          brush = Brush.radialGradient(
            listOf(Accent.copy(alpha = glowAlpha), Color.Transparent),
            radius = size.minDimension * 0.5f,
          ),
          radius = size.minDimension * 0.5f,
        )
      }
    }

    // Layer 2 — Spinning outer shutter ring
    Canvas(
      modifier = Modifier
        .matchParentSize()
        .graphicsLayer { rotationZ = spin },
    ) {
      drawShutterRing(highlighted == SettingKey.SHUTTER)
    }

    // Layer 3 — Static lens body
    Canvas(modifier = Modifier.matchParentSize()) {
      drawLensBody(
        animPct = animPct,
        isoGlow = isoGlow,
        wbColor = wbColor,
        fStopLabel = fStopLabel,
        highlighted = highlighted,
      )
    }
  }
}

// ── Shutter ring (spins) ────────────────────────────────────────────────────

private fun DrawScope.drawShutterRing(isHighlighted: Boolean) {
  val cx = size.width / 2f
  val cy = size.height / 2f
  val s = size.minDimension / 210f
  val outerR = (74f + 18f) * s

  drawCircle(
    color = if (isHighlighted) Color(0xFF444444) else Color(0xFF282828),
    radius = outerR,
    center = Offset(cx, cy),
    style = Stroke(
      width = 14f * s,
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * s, 4f * s)),
    ),
  )

  drawCircle(
    color = Accent,
    radius = outerR,
    center = Offset(cx, cy),
    style = Stroke(
      width = (if (isHighlighted) 3f else 1.5f) * s,
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f * s, 82f * s)),
    ),
    alpha = if (isHighlighted) 1f else 0.55f,
  )

  repeat(12) { i ->
    val angle = (i / 12f) * 2f * PI.toFloat() - PI.toFloat() / 2f
    val r1 = (74f + 12f) * s
    val r2 = (74f + 22f) * s
    drawLine(
      color = if (isHighlighted) Accent else Color(0xFF333333),
      start = Offset(cx + cos(angle) * r1, cy + sin(angle) * r1),
      end = Offset(cx + cos(angle) * r2, cy + sin(angle) * r2),
      strokeWidth = (if (i % 3 == 0) 1.5f else 0.8f) * s,
    )
  }
}

// ── Lens body (static) ──────────────────────────────────────────────────────

private fun DrawScope.drawLensBody(
  animPct: Float,
  isoGlow: Float,
  wbColor: Color,
  fStopLabel: String,
  highlighted: SettingKey?,
) {
  val cx = size.width / 2f
  val cy = size.height / 2f
  val s = size.minDimension / 210f
  val r = 74f * s
  val irisR = r * 0.86f * animPct

  // Lens housing
  val housingR = r + 3f * s
  drawCircle(
    brush = Brush.radialGradient(
      colorStops = arrayOf(0f to Color(0xFF242428), 1f to Color(0xFF101012)),
      center = Offset(cx, cy * 0.8f),
      radius = housingR,
    ),
    radius = housingR,
    center = Offset(cx, cy),
  )

  // Aperture housing ring
  drawCircle(Color(0xFF080810), r, Offset(cx, cy))
  drawCircle(Color(0xFF0D0D0D), r * 0.88f, Offset(cx, cy))

  // Aperture blade marks
  val isApertureHL = highlighted == SettingKey.APERTURE
  val bladeColor = if (isApertureHL) Accent else Color(0x40FFFFFF)
  repeat(6) { i ->
    val angle = (i / 6f) * 2f * PI.toFloat() - PI.toFloat() / 2f
    drawLine(
      color = bladeColor,
      start = Offset(cx + cos(angle) * r * 0.56f, cy + sin(angle) * r * 0.56f),
      end = Offset(cx + cos(angle) * r * 0.84f, cy + sin(angle) * r * 0.84f),
      strokeWidth = (if (isApertureHL) 2f else 1f) * s,
      cap = StrokeCap.Round,
    )
  }

  // Inner glass + WB tint + reflection
  if (irisR > 1f) {
    drawCircle(
      brush = Brush.radialGradient(
        colorStops = arrayOf(
          0.00f to Color(0x12FFFFFF),
          0.45f to Color(0xEB12121C),
          1.00f to Color(0xFF040408),
        ),
        center = Offset(cx - irisR * 0.26f, cy - irisR * 0.27f),
        radius = irisR * 1.1f,
      ),
      radius = irisR,
      center = Offset(cx, cy),
    )

    if (wbColor.alpha > 0.01f) {
      val tintAlpha = (if (highlighted == SettingKey.WB) wbColor.alpha * 1.8f else wbColor.alpha)
        .coerceIn(0f, 1f)
      drawCircle(wbColor.copy(alpha = tintAlpha), irisR, Offset(cx, cy))
    }

    drawOval(
      color = Color(0x29FFFFFF),
      topLeft = Offset(cx - irisR * 0.47f, cy - irisR * 0.39f),
      size = Size(irisR * 0.42f, irisR * 0.24f),
    )

    // f-stop label via native canvas
    if (irisR > 14f * s) {
      val textSizePx = max(8f * s, min(12f * s, irisR * 0.2f))
      val labelColor = if (isApertureHL) Accent else Color(0x73FFFFFF)
      drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
          color = labelColor.toArgb()
          textSize = textSizePx
          textAlign = android.graphics.Paint.Align.CENTER
          typeface = android.graphics.Typeface.DEFAULT_BOLD
          isAntiAlias = true
        }
        canvas.nativeCanvas.drawText(fStopLabel, cx, cy + textSizePx * 0.35f, paint)
      }
    }

    // Aperture arc label when chip is highlighted
    if (isApertureHL) {
      val arcR = r * 0.82f
      drawIntoCanvas { canvas ->
        val arcPath = android.graphics.Path().apply {
          addArc(cx - arcR, cy - arcR, cx + arcR, cy + arcR, 180f, -180f)
        }
        val arcPaint = android.graphics.Paint().apply {
          color = Accent.toArgb()
          textSize = 9f * s
          textAlign = android.graphics.Paint.Align.CENTER
          isAntiAlias = true
          letterSpacing = 0.05f
        }
        canvas.nativeCanvas.drawTextOnPath(
          "APERTURE  ·  $fStopLabel  ·",
          arcPath,
          0f,
          -4f * s,
          arcPaint,
        )
      }
    }
  }

  // Focus mode ring
  val isFocusHL = highlighted == SettingKey.FOCUS
  drawCircle(
    color = if (isFocusHL) Accent else Color(0x40FFFFFF),
    radius = r * 0.28f,
    center = Offset(cx, cy),
    style = Stroke(
      width = (if (isFocusHL) 1.5f else 0.8f) * s,
      pathEffect = PathEffect.dashPathEffect(
        if (isFocusHL) {
          floatArrayOf(4f * s, 3f * s)
        } else {
          floatArrayOf(2f * s, 4f * s)
        },
      ),
    ),
  )

  // ISO ring glow band
  val isIsoHL = highlighted == SettingKey.ISO
  val isoRingAlpha = (if (isIsoHL) 0.3f + isoGlow * 0.5f else isoGlow * 0.2f).coerceIn(0f, 1f)
  if (isoRingAlpha > 0.01f) {
    drawCircle(
      color = Accent.copy(alpha = isoRingAlpha),
      radius = r * 0.92f,
      center = Offset(cx, cy),
      style = Stroke(width = (if (isIsoHL) 4f else 2f) * s),
    )
  }
}

// ── Math helpers ─────────────────────────────────────────────────────────────

private fun parseFStop(value: String): Float = Regex("""[\d.]+""").find(value)?.value?.toFloatOrNull() ?: 1.8f

private fun calcAperturePct(fNum: Float): Float {
  val lo = log(1.4, Math.E).toFloat()
  val hi = log(22.0, Math.E).toFloat()
  val v = log(fNum.toDouble(), Math.E).toFloat()
  return max(0.12f, min(0.96f, 1f - (v - lo) / (hi - lo)))
}

private fun parseIso(value: String): Int = Regex("""\d+""").find(value)?.value?.toIntOrNull() ?: 100

private fun calcIsoGlow(iso: Int): Float = min(0.9f, (log(iso / 100.0 + 1.0, Math.E) / log(130.0, Math.E)).toFloat())

private fun calcSpinDurationMs(value: String): Int {
  if (Regex("""^\d+\s*sec""").containsMatchIn(value)) return 8_000
  val denom = Regex("""1/(\d+)""").find(value)?.groupValues?.get(1)?.toIntOrNull() ?: 60
  return when {
    denom >= 1_000 -> 250
    denom >= 250 -> 700
    denom >= 60 -> 2_000
    else -> 5_000
  }
}

private fun calcWbColor(wb: String): Color {
  val lower = wb.lowercase()
  return when {
    "tungsten" in lower || "2700" in lower || "3200" in lower -> Color(0x38FF821E)
    "cloudy" in lower || "shade" in lower || "6500" in lower || "7000" in lower -> Color(0x2E8CAAFF)
    "flash" in lower || "5500" in lower -> Color(0x23DCF0FF)
    else -> Color.Transparent
  }
}
