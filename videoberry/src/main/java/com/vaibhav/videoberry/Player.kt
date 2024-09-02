package com.vaibhav.videoberry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.exoplayer.ExoPlayer


/**
 * A Composable function that provides a remembered instance of [ExoPlayer].
 *
 * This function creates an instance of [ExoPlayer] using the provided [ExoPlayer.Builder], or it can use
 * the default builder if none is provided. The player instance is remembered across recompositions, ensuring
 * that the same instance of [ExoPlayer] is reused during the lifecycle of the composable.
 *
 * This is useful in Jetpack Compose when you want to manage the lifecycle of the ExoPlayer and ensure
 * it persists as long as the composable is active.
 *
 * @param player An optional [ExoPlayer] instance that can be passed. If not provided, a new instance is created using the [ExoPlayer.Builder] and the current context.
 * @return A remembered instance of [ExoPlayer] that will persist across recompositions.
 *
 * Example usage:
 * ```
 * @Composable
 * fun VideoPlayerScreen() {
 *     val exoPlayer = rememberExoplayer()
 *
 *     AndroidView(
 *         factory = { context ->
 *             PlayerView(context).apply {
 *                 player = exoPlayer
 *             }
 *         },
 *         modifier = Modifier.fillMaxSize()
 *     )
 *
 *     DisposableEffect(Unit) {
 *         onDispose {
 *             exoPlayer.release() // Release player when composable is disposed
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun rememberExoplayer(
    player: ExoPlayer = ExoPlayer
        .Builder(LocalContext.current)
        .build()
): ExoPlayer {
    return remember {
        player
    }
}