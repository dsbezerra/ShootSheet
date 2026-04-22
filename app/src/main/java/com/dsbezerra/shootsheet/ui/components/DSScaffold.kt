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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dsbezerra.shootsheet.ui.theme.Bg

/**
 * Lightweight design-system scaffold. Keeps [topBar] pinned at the top while
 * [content] fills the remaining height — preventing lazy lists from receiving
 * unbounded height constraints and accidentally scrolling the header.
 */
@Composable
fun DSScaffold(
  modifier: Modifier = Modifier,
  topBar: @Composable () -> Unit = {},
  content: @Composable BoxScope.() -> Unit,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Bg)
      .statusBarsPadding(),
  ) {
    topBar()
    Box(modifier = Modifier.weight(1f), content = content)
  }
}
