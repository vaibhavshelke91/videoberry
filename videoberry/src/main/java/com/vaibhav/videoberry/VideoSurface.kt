package com.vaibhav.videoberry

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
import androidx.media3.ui.AspectRatioFrameLayout.ResizeMode
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * A custom composable function that displays a video surface with player controls and gesture handling.
 *
 * This composable function creates a video player surface that integrates with the provided [ExoPlayer]
 * instance. It includes various customization options for player behavior, gesture handling, and UI interactions.
 *
 * @param modifier A [Modifier] to apply to the [VideoSurface]. This can be used to set layout properties like size, padding, etc.
 * @param playerListeners An instance of [Player.Listener] to receive events from the [ExoPlayer] player. This allows handling player state changes and events.
 * @param player An [ExoPlayer] instance used for video playback. By default, a remembered instance is created using [rememberExoplayer()].
 * @param startIndex The initial index of the video to start playback from. Default is `0`.
 * @param controllerVisibilityTime The duration in milliseconds for which the player controls are visible. Default is `3000L` (3 seconds).
 * @param swipeControllerTimeout The timeout duration in milliseconds for swipe gestures to be recognized. Default is `600L` (600 milliseconds).
 * @param swipesThresholds The threshold value in pixels for detecting swipe gestures. Default is `20f` (20 pixels).
 * @param onSwipes A lambda function that is called when a swipe gesture is detected. It receives a [Swipes] instance representing the type of swipe.
 * @param onNextDoubleClick A lambda function that is called when the next button is double-clicked. It receives the current playback position in milliseconds.
 * @param onPrevDoubleClick A lambda function that is called when the previous button is double-clicked. It also receives the current playback position in milliseconds.
 * @param onSeekControllerVisible A lambda function that is called to notify when the seek controller visibility changes. It receives a boolean indicating visibility.
 * @param onClick A lambda function that is called when the video surface is clicked. This can be used to toggle controls or perform other actions.
 * @param onControllerVisibility A lambda function that is called to notify when the controller visibility changes. It receives a boolean indicating visibility.
 * @param onTimeLineChange A lambda function that is called when the timeline changes. It receives the current time, duration, and formatted start and end times.
 * @param content A composable lambda that allows adding custom UI content on top of the video surface, such as overlays or additional controls.
 *
 * Example usage:
 * ```
 * @Composable
 * fun MyVideoPlayer() {
 *     VideoSurface(
 *         modifier = Modifier.fillMaxSize(),
 *         playerListeners = myPlayerListener,
 *         player = rememberExoplayer(),
 *         onSwipes = { swipe ->
 *             // Handle swipe gestures
 *         },
 *         onNextDoubleClick = { position ->
 *             // Handle double-click on the next button
 *         },
 *         onPrevDoubleClick = { position ->
 *             // Handle double-click on the previous button
 *         },
 *         onSeekControllerVisible = { isVisible ->
 *             // Handle seek controller visibility
 *         },
 *         onClick = {
 *             // Handle surface click
 *         },
 *         onControllerVisibility = { isVisible ->
 *             // Handle controller visibility
 *         },
 *         onTimeLineChange = { currentTime, duration, startTime, endTime ->
 *             // Handle timeline changes
 *         }
 *     ) {
 *         // Custom content to overlay on top of the video surface
 *         Text("Overlay Content", modifier = Modifier.align(Alignment.Center))
 *     }
 * }
 * ```
 */
@Composable
fun VideoSurface(
    modifier: Modifier = Modifier,
    playerListeners: Player.Listener,
    player: ExoPlayer = rememberExoplayer(),
    startIndex: Int = 0,
    controllerVisibilityTime: Long = 3000L,
    swipeControllerTimeout: Long = 600,
    swipesThresholds: Float = 20f,
    onSwipes: (Swipes) -> Unit,
    onNextDoubleClick: (Long) -> Unit,
    onPrevDoubleClick: (Long) -> Unit,
    onSeekControllerVisible: (Boolean) -> Unit,
    onClick: () -> Unit,
    onControllerVisibility: (Boolean) -> Unit,
    onTimeLineChange: (Long, Long, String, String) -> Unit,
    content: @Composable () -> Unit
) {

    val context = LocalContext.current
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    val interactionSource = remember { MutableInteractionSource() }
    var clickCount by remember { mutableStateOf(0) }
    var showClickEffect by remember {
        mutableStateOf(true)
    }
    var lastClickTime by remember { mutableStateOf(0L) }
    var isControllerVisible by remember {
        mutableStateOf(false)
    }
    val clickThreshold = 300L
    var rightClick by remember {
        mutableStateOf(0L)
    }

    var leftClick by remember {
        mutableStateOf(0L)
    }
    var isSeekControllerVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isControllerVisible) {
        onControllerVisibility.invoke(isControllerVisible)
    }
    LaunchedEffect(key1 = isSeekControllerVisible) {
        if (!isSeekControllerVisible) {
            delay(swipeControllerTimeout)
        }
        onSeekControllerVisible.invoke(isSeekControllerVisible)
    }

    LaunchedEffect(clickCount) {
        if (clickCount > 0 && clickCount != 1) {
            delay(swipeControllerTimeout)
            Log.d("ClickEffect", "Ended")
            isSeekControllerVisible = false
            rightClick = 0
            leftClick = 0
        }
    }

    LaunchedEffect(key1 = isControllerVisible) {
        if (isControllerVisible && player.isPlaying) {
            delay(controllerVisibilityTime)
            isControllerVisible = !isControllerVisible
        }
    }

    val currentProgress = remember {
        mutableStateOf("00:00")
    }
    val finalProgress = remember {
        mutableStateOf("00:00")
    }
    val progress = remember {
        mutableStateOf(0L)
    }
    val maxProgress = remember {
        mutableStateOf(0L)
    }

    DisposableEffect(key1 = Unit) {
        lifecycleOwner.value.lifecycleScope.launch {
            player.currentPositionFlow().collect {
                progress.value = it
                currentProgress.value = it.toFormattedTime()
                maxProgress.value = player.duration
                finalProgress.value = player.duration.toFormattedTime()
                onTimeLineChange.invoke(
                    progress.value,
                    maxProgress.value,
                    currentProgress.value,
                    finalProgress.value
                )
            }
        }
        onDispose {

        }
    }

    var thresholds by remember {
        mutableStateOf(swipesThresholds)
    }
    var isVerticalSwiping by remember {
        mutableStateOf(false)
    }
    var isHorizontalSwiping by remember {
        mutableStateOf(false)
    }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current

    // Convert dp to px for accurate calculations
    val screenWidthPx = with(density) { screenWidth.toPx() }
    val screenHeightPx = with(density) { screenHeight.toPx() }

    AndroidView(factory = {
        PlayerView(context).apply {
            this.player = player
            keepScreenOn = true
            layoutParams =
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            useController = false
        }

    }, modifier = modifier
        .indication(interactionSource, rememberRipple())
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { offset ->
                    val press = PressInteraction.Press(offset)
                    interactionSource.emit(press)
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime > clickThreshold) {
                        clickCount = 1
                    } else {
                        clickCount++
                    }
                    lastClickTime = currentTime
                    interactionSource.emit(PressInteraction.Release(press))
                    delay(200)
                    when (clickCount) {
                        1 -> {
                            if (showClickEffect) {
                                isControllerVisible = !isControllerVisible
                                onClick.invoke()
                            }
                        }

                        else -> {
                            isControllerVisible = false
                            //isSubtitleControllerVisible = false
                            isSeekControllerVisible = true
                            val left = size.width / 2
                            if (offset.x > left) {
                                rightClick += 10000
                                onNextDoubleClick.invoke(rightClick)
                            } else {
                                leftClick += 10000
                                onPrevDoubleClick.invoke(leftClick)

                            }
                        }
                    }
                }
            )
        }
        .pointerInput(Unit) {
            coroutineScope {
                detectVerticalDragGestures(
                    onDragEnd = {
                        showClickEffect = true
                        isControllerVisible = false
                        isSeekControllerVisible = false
                        isVerticalSwiping = false
                    },
                    onDragStart = {
                        isControllerVisible = false
                        isSeekControllerVisible = true
                        showClickEffect = false
                        isVerticalSwiping = true
                    }) { change, dragAmount ->


                    val dragAmountX = change.positionChange().x
                    val dragAmountY = change.positionChange().y
                    val touchPositionX = change.position.x
                    val touchPositionY = change.position.y

                    val isLeftHalf = touchPositionX < screenWidthPx / 2

                    if (!isHorizontalSwiping) {
                        if (kotlin.math.abs(dragAmountY) > thresholds || kotlin.math.abs(
                                dragAmountX
                            ) > thresholds
                        ) {
                            when {
                                // Upward swipe from the left half
                                dragAmountY < 0 && isLeftHalf -> {
                                    onSwipes.invoke(Swipes.SwipeUpLeft)
                                }
                                // Upward swipe from the right half
                                dragAmountY < 0 && !isLeftHalf -> {
                                    onSwipes.invoke(Swipes.SwipeUpRight)
                                }
                                // Downward swipe from the left half
                                dragAmountY > 0 && isLeftHalf -> {
                                    onSwipes.invoke(Swipes.SwipeDownLeft)
                                }
                                // Downward swipe from the right half
                                dragAmountY > 0 && !isLeftHalf -> {
                                    onSwipes.invoke(Swipes.SwipeDownRight)
                                }
                            }
                        }
                    }

                }

            }
        }
        .pointerInput(Unit) {
            coroutineScope {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        showClickEffect = true
                        isControllerVisible = false
                        isSeekControllerVisible = false
                        isHorizontalSwiping = false
                    },
                    onDragStart = {
                        isControllerVisible = false
                        isSeekControllerVisible = true
                        showClickEffect = false
                        isHorizontalSwiping = true
                    }) { change, dragAmount ->

                    if (!isVerticalSwiping) {
                        change.consume()
                        if (dragAmount > thresholds) {
                            onSwipes.invoke(Swipes.SwipeRight)
                        } else if (dragAmount < -thresholds) {
                            onSwipes.invoke(Swipes.SwipeLeft)
                        }
                    }

                }

            }
        }
    )

    var autoPlay by rememberSaveable { mutableStateOf(true) }
    var currentIndex by rememberSaveable { mutableStateOf(startIndex) }
    var position by rememberSaveable { mutableStateOf(0L) }
    var isFirstTime by rememberSaveable {
        mutableStateOf(true)
    }

    fun updateState() {
        autoPlay = player.playWhenReady
        currentIndex = player.currentMediaItemIndex
        position = player.contentPosition
        isFirstTime = false
    }

    DisposableEffect(
        key1 = player,
        effect = {

            player.apply {
                playWhenReady = autoPlay
                seekTo(currentIndex, position)
                prepare()
            }

            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        player.play()
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        updateState()
                        player.pause()
                    }

                    else -> {}
                }
            }

            val lifecycle = lifecycleOwner.value.lifecycle
            lifecycle.addObserver(observer)
            player.addListener(playerListeners)

            onDispose {
                updateState()
                player.removeListener(playerListeners)
                player.release()
                lifecycle.removeObserver(observer)
            }
        })

    if (isControllerVisible) {
        content()
    }


}