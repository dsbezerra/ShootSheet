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
package com.dsbezerra.shootsheet.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.vector.ImageVector

object ShootSheetIcons {
  val Explore: ImageVector get() = Icons.Outlined.Explore
  val Favorite: ImageVector get() = Icons.Filled.Favorite
  val FavoriteOutline: ImageVector get() = Icons.Outlined.FavoriteBorder
  val Back: ImageVector get() = Icons.AutoMirrored.Filled.ArrowBack
  val Search: ImageVector get() = Icons.Filled.Search
  val Close: ImageVector get() = Icons.Filled.Close
  val Camera: ImageVector get() = Icons.Outlined.CameraAlt
  val Info: ImageVector get() = Icons.Outlined.Info
}
