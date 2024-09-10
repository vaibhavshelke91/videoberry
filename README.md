
# VideoBerry

A fully customizable video surface for Android built using Jetpack Compose and ExoPlayer. This composable integrates ExoPlayer for smooth video playback, custom gestures, and controls for a seamless user experience.

## Features

- Auto Play and Stop : Automatically starts playing the video when ready and stops during lifecycle changes or interruptions.

- Lifecycle Management : Handles video playback across different lifecycle states, ensuring proper resource management and playback control.

- Customizable Controllers: Offers complete control over player UI, allowing developers to define custom controllers, gestures, and visibility settings.

- Swipe Gestures: Supports custom swipe gestures for video seeking, with adjustable thresholds and timeouts.

- Double Tap Controls: Implements double-tap gestures for skipping forward and backward with custom actions.

- Timeline Updates: Provides real-time feedback on video timeline, duration, and current position.

## API Reference

#### Binding 

```kotlin
  VideoSurface(
    modifier = Modifier.fillMaxSize(),
    playerListeners = MyPlayerListener(),
    onSwipes = { swipes -> /* handle swipe gestures */ },
    onNextDoubleClick = { /* handle next double tap */ },
    onPrevDoubleClick = { /* handle previous double tap */ },
    onSeekControllerVisible = { visible -> /* handle seek controller visibility */ },
    onClick = { /* handle single tap */ },
    onControllerVisibility = { visible -> /* handle controller visibility */ },
    onTimeLineChange = { currentPosition, duration, currentTime, endTime ->
        // Update UI with timeline information
    }
) {
    // Additional UI content overlaid on the video
}

```


