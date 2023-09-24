package ru.avem.stand.view.composables

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Vertical
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun ScrollableLazyColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Vertical = Arrangement.Top,
    content: LazyListScope.() -> Unit
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Box(modifier) {
        LazyColumn(
            state = scrollState, modifier = Modifier.fillMaxSize().draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState {
                    scope.launch {
                        scrollState.scrollBy(-it)
                    }
                }
            ), verticalArrangement = verticalArrangement) {
            content()
        }
        VerticalScrollbar(
            rememberScrollbarAdapter(scrollState),
            modifier = Modifier.align(Alignment.CenterEnd),
            style = LocalScrollbarStyle.current.copy(
                unhoverColor = MaterialTheme.colors.primary.copy(alpha = .7f),
                hoverColor = MaterialTheme.colors.primary
            )
        )
    }
}