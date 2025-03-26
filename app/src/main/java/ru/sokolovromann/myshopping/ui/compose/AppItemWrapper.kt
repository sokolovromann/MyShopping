package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun AppItemSwipeableWrapper(
    enabled: Boolean = true,
    left: @Composable (() -> Unit)? = null,
    onSwipeLeft: () -> Unit,
    right: @Composable (() -> Unit)? = null,
    onSwipeRight: () -> Unit,
    itemContent: @Composable () -> Unit
) {
    if (enabled) {
        AppItemSwipeableWrapperImpl(
            left = left,
            onSwipeLeft = onSwipeLeft,
            right = right,
            onSwipeRight = onSwipeRight,
            itemContent = itemContent
        )
    } else {
        itemContent()
    }
}

@Composable
private fun AppItemSwipeableWrapperImpl(
    left: @Composable (() -> Unit)?,
    onSwipeLeft: () -> Unit,
    right: @Composable (() -> Unit)?,
    onSwipeRight: () -> Unit,
    itemContent: @Composable () -> Unit
) {
    var itemSize by remember { mutableStateOf(IntSize.Zero) }
    var itemOffset by remember { mutableFloatStateOf(0f) }
    var pointerInputKey by remember { mutableLongStateOf(0L) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { itemSize = it },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (itemOffset > 0) {
                right?.let { it() }
            }
            Spacer(modifier = Modifier.weight(1f))
            if (itemOffset < 0) {
                left?.let { it() }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(itemOffset.roundToInt(), 0) }
                .pointerInput(pointerInputKey) {
                    detectHorizontalDragGestures(
                        onDragStart = { itemOffset = 0f },
                        onDragEnd = {
                            val anchor = itemSize.width.toFloat() / 2
                            if (itemOffset < -anchor) {
                                onSwipeLeft()
                            } else if (itemOffset > anchor) {
                                onSwipeRight()
                            } else {
                                // Nothing
                            }

                            itemOffset = 0f
                            pointerInputKey = System.currentTimeMillis()
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            itemOffset += dragAmount
                        }
                    )
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            content = { itemContent() }
        )
    }
}