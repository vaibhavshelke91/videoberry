package com.vaibhav.berryplayer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.vaibhav.videoberry.VideoSurface
import com.vaibhav.videoberry.rememberExoplayer


@Composable
fun PlayerScreen(modifier: Modifier=Modifier){
    
    val player = rememberExoplayer()


    
    VideoSurface(
        modifier = modifier.fillMaxSize(),
        player = player,
        playerListeners = object : Player.Listener {
            // handle all player events
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
            }
        },
        onSwipes = {},
        onNextDoubleClick = {},
        onPrevDoubleClick = {},
        onSeekControllerVisible =  {},
        onClick = { },
        onControllerVisibility = {},
        onTimeLineChange = {current,max,currentString,maxString ->
            
        }
    ) {
        
    }
}