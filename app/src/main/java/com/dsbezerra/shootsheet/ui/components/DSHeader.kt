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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dsbezerra.shootsheet.R
import com.dsbezerra.shootsheet.ui.theme.Accent
import com.dsbezerra.shootsheet.ui.theme.ShootSheetTextStyles
import com.dsbezerra.shootsheet.ui.theme.ShootSheetTheme
import com.dsbezerra.shootsheet.ui.theme.TextPrimary
import com.dsbezerra.shootsheet.ui.theme.TextSub
import com.dsbezerra.shootsheet.ui.theme.spacing

@Composable
fun DSHeader(
  modifier: Modifier = Modifier,
  title: String,
  subtitle: String? = null,
) {
  val spacing = MaterialTheme.spacing
  Column(
    modifier = modifier.padding(
      horizontal = spacing.screenHorizontal,
      vertical = spacing.xxl,
    ),
  ) {
    Text(
      text = stringResource(R.string.app_eyebrow),
      color = Accent,
      style = ShootSheetTextStyles.eyebrow,
    )
    Spacer(Modifier.height(spacing.sm))
    Text(
      text = title,
      color = TextPrimary,
      style = ShootSheetTextStyles.screenTitle,
    )
    Spacer(Modifier.height(spacing.xs))
    if (subtitle != null) {
      Text(
        text = subtitle,
        color = TextSub,
        style = ShootSheetTextStyles.screenSubtitle,
      )
    }
  }
}

@Preview
@Composable
private fun PreviewDSHeader() {
  ShootSheetTheme {
    DSHeader(
      title = stringResource(R.string.favorites_title),
      subtitle = "Subtitle",
    )
  }
}
