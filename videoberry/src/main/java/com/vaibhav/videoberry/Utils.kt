package com.vaibhav.videoberry

import androidx.media3.common.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Converts a [Long] value representing a duration in milliseconds to a formatted time string.
 *
 * This extension function formats a duration in milliseconds into a string representation
 * in either `HH:mm:ss` or `mm:ss` format, depending on the length of the duration.
 *
 * The function calculates the hours, minutes, and seconds from the given duration and formats
 * the time accordingly:
 *
 * - If the duration includes hours (i.e., greater than or equal to 1 hour), the formatted
 * string will be in the format `HH:mm:ss`.
 * - If the duration is less than 1 hour, the formatted string will be in the format `mm:ss`.
 *
 * @receiver The duration in milliseconds as a [Long].
 * @return A [String] representing the formatted time, either in `HH:mm:ss` or `mm:ss` format.
 *
 * Example usage:
 * ```
 * val timeInMillis: Long = 3600000 // 1 hour
 * val formattedTime: String = timeInMillis.toFormattedTime() // "01:00:00"
 * ```
 */

internal fun Long.toFormattedTime(): String {
    val hours = this / (1000 * 60 * 60)
    val minutes = (this / (1000 * 60)) % 60
    val seconds = (this / 1000) % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}


/**
 * Creates a [Flow] that emits the current playback position of the [Player] at a specified frequency.
 *
 * This extension function on the [Player] class continuously monitors the player's playback position
 * and emits the current position whenever the player is actively playing. The emission occurs at a
 * defined update frequency, which can be customized.
 *
 * The flow is emitted on the main thread using the `Dispatchers.Main` context.
 *
 * @param updateFrequency The frequency at which the current position of the player is emitted.
 * The default value is 1 second. This should be provided as a [Duration] value.
 * @return A [Flow] emitting the current playback position of the [Player] as a [Long] in milliseconds.
 *
 * Usage:
 * ```
 * val player: Player = ExoPlayer.Builder(context).build()
 * player.currentPositionFlow(updateFrequency = 500.milliseconds)
 *     .onEach { currentPosition ->
 *         // Handle current playback position
 *     }
 *     .launchIn(lifecycleScope) // Using a CoroutineScope to collect the flow
 * ```
 */
internal fun Player.currentPositionFlow(
    updateFrequency: Duration = 1.seconds,
) = flow {
    while (true) {
        // Log.d("Flow","Running..")
        if (isPlaying) emit(currentPosition)
        delay(updateFrequency)
    }
}.flowOn(Dispatchers.Main)