/*
 * Designed and developed by 2022 skydoves (Jaewoong Eum)
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

package com.skydoves.orbital

import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember

/**
 * Remember [movableContentWithReceiverOf] with the content that has [OrbitalScope] as a receiver.
 *
 * @param content A Composable that has [OrbitalScope] as a receiver.
 */
@Composable
fun rememberContentWithOrbitalScope(
    content: @Composable OrbitalScope.() -> Unit
): @Composable OrbitalScope.() -> Unit {
    return remember {
        movableContentWithReceiverOf(content = content)
    }
}
