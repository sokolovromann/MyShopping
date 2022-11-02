package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppItem(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    body: @Composable (() -> Unit)? = null,
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    dropdownMenu: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 48.dp)
        .clickable { onClick() }
        .then(modifier)
    ) {
        AppItemImpl(
            modifier = Modifier.padding(all = 8.dp),
            before = before,
            title = title,
            body = body,
            after = after,
            dropdownMenu = dropdownMenu
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun AppItem(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    body: @Composable (() -> Unit)? = null,
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    dropdownMenu: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 48.dp)
        .combinedClickable(onClick = onClick, onLongClick = onLongClick)
        .then(modifier)
    ) {
        AppItemImpl(
            modifier = Modifier.padding(all = 8.dp),
            before = before,
            title = title,
            body = body,
            after = after,
            dropdownMenu = dropdownMenu
        )
    }
}

@Composable
fun AppSurfaceItem(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    body: @Composable (() -> Unit)? = null,
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    dropdownMenu: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .padding(all = 4.dp)
            .clickable { onClick() }
            .then(modifier),
        shape = MaterialTheme.shapes.medium,
        elevation = 1.dp
    ) {
        AppItemImpl(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            before = before,
            title = title,
            body = body,
            after = after,
            dropdownMenu = dropdownMenu
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun AppSurfaceItem(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    body: @Composable (() -> Unit)? = null,
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    dropdownMenu: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .padding(all = 4.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .then(modifier),
        shape = MaterialTheme.shapes.medium,
        elevation = 1.dp
    ) {
        AppItemImpl(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            before = before,
            title = title,
            body = body,
            after = after,
            dropdownMenu = dropdownMenu
        )
    }
}

@Composable
private fun AppItemImpl(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    body: @Composable (() -> Unit)? = null,
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    dropdownMenu: @Composable (() -> Unit)? = null
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        before?.let { it() }
        Column(modifier = Modifier.weight(1f)) {
            title()
            body?.let { it() }
            dropdownMenu?.let { it() }
        }
        after?.let { it() }
    }
}