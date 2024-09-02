package com.vaibhav.videoberry


/**
 * Sealed class representing different types of swipe gestures.
 *
 * This class is used to define various swipe directions that can be detected
 * in a UI. The `Swipes` class contains different swipe actions, each of which
 * is represented as an object that extends from the sealed class.
 *
 * The supported swipe gestures include:
 *
 * - **SwipeUpRight**: Represents an upward swipe towards the right.
 * - **SwipeUpLeft**: Represents an upward swipe towards the left.
 * - **SwipeDownRight**: Represents a downward swipe towards the right.
 * - **SwipeDownLeft**: Represents a downward swipe towards the left.
 * - **SwipeLeft**: Represents a horizontal swipe towards the left.
 * - **SwipeRight**: Represents a horizontal swipe towards the right.
 *
 * Usage:
 * This sealed class can be used in gesture detection logic to represent and
 * handle various swipe actions within a user interface.
 */
sealed class Swipes {
    /** Represents an upward swipe towards the right. */
    data object SwipeUpRight : Swipes()
    /** Represents an upward swipe towards the left. */
    data object SwipeUpLeft : Swipes()
    /** Represents a downward swipe towards the right. */
    data object SwipeDownRight : Swipes()
    /** Represents a downward swipe towards the left. */
    data object SwipeDownLeft :Swipes()
    /** Represents a horizontal swipe towards the left. */
    data object SwipeLeft : Swipes()
    /** Represents a horizontal swipe towards the right. */
    data object SwipeRight : Swipes()
}