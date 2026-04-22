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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dsbezerra.shootsheet.R
import com.dsbezerra.shootsheet.data.Category
import com.dsbezerra.shootsheet.ui.theme.Bg
import com.dsbezerra.shootsheet.ui.theme.Shapes
import com.dsbezerra.shootsheet.ui.theme.ShootSheetTextStyles
import com.dsbezerra.shootsheet.ui.theme.ShootSheetTheme
import com.dsbezerra.shootsheet.ui.theme.SurfaceCard
import com.dsbezerra.shootsheet.ui.theme.TextMuted
import com.dsbezerra.shootsheet.ui.theme.TextPrimary
import com.dsbezerra.shootsheet.ui.theme.spacing

@Composable
fun CategoryCard(
  category: Category,
  scenarioCount: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val spacing = MaterialTheme.spacing
  val composeColor = category.composeColor

  Box(
    modifier = modifier
      .aspectRatio(1f)
      .clip(Shapes.card)
      .clickable(onClick = onClick),
  ) {
    // ── Background: photo or bokeh fallback ─────────────────────────
    if (category.imageUrl != null) {
      AsyncImage(
        model = ImageRequest.Builder(context)
          .data(category.imageUrl)
          .crossfade(true)
          .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
      )
    } else {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.radialGradient(
              colorStops = arrayOf(
                0.00f to composeColor.copy(alpha = 0.55f),
                0.55f to SurfaceCard,
                1.00f to Bg,
              ),
            ),
          ),
      )
    }

    // ── Scrim for text legibility ────────────────────────────────────
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(Color.Transparent, Bg.copy(alpha = 0.85f)),
          ),
        ),
    )

    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(spacing.lg)
        .size(6.dp)
        .background(composeColor, CircleShape),
    )

    Box(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .offset(x = spacing.lg, y = spacing.lg)
        .size(80.dp)
        .background(composeColor.copy(alpha = 0.08f), CircleShape),
    )

    Column(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .fillMaxWidth()
        .padding(spacing.lg),
    ) {
      Text(
        text = category.getLabel(context),
        color = TextPrimary,
        style = ShootSheetTextStyles.categoryLabel,
      )
      Spacer(Modifier.height(spacing.xxs))
      Text(
        text = pluralStringResource(
          R.plurals.category_scenario_count,
          scenarioCount,
          scenarioCount,
        ),
        color = TextMuted,
        style = ShootSheetTextStyles.categoryCount,
      )
    }
  }
}

@Preview
@Composable
private fun PreviewCategoryCard() {
  ShootSheetTheme {
    CategoryCard(
      category = Category("portrait", "Portrait", "#FF821E"),
      scenarioCount = 12,
      onClick = {},
    )
  }
}
