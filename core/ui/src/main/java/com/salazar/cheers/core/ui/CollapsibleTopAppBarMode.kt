package com.salazar.cheers.core.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.max
import kotlin.math.absoluteValue
import kotlin.math.min

@Preview
@Composable
private fun PreviewTopAppBarLazyColumn() {
    MaterialTheme {
        val listState = rememberLazyListState()
        CollapsibleScaffold(
            state = listState,
            topBar = {
                TopBar(
                    modifier = Modifier.background(Color.Red),
                    onBack = {},
                    actions = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Blue)
                        )
                    }
                ) {
                    val fraction = this.fraction
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Green)
                    ) {
                        Text(
                            text = fraction.toString(),
                            modifier = Modifier
                                .align(Alignment.BottomStart)

                        )
                    }
                }
            }
        ) { insets ->
            LazyColumn(state = listState, contentPadding = insets) {
                item {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.Yellow)
                    )
                }
                items(100) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(80.dp)
                    ) {
                        Text(
                            text = "Item $it",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewTopAppBarColumn() {
    MaterialTheme {
        val scrollState = rememberScrollState()
        CollapsibleScaffold(
            state = scrollState,
            topBar = {
                TopBar(
                    modifier = Modifier.background(Color.Red),
                    onBack = {},
                    actions = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Blue)
                        )
                    }
                ) {
                    val fraction = this.fraction
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Green)
                    ) {
                        Text(
                            text = fraction.toString(),
                            modifier = Modifier
                                .align(Alignment.BottomStart)

                        )
                    }
                }
            }
        ) { insets ->
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Spacer(modifier = Modifier.padding(insets))
                repeat(100) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(80.dp)
                    ) {
                        Text(
                            text = "Item $it",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

object CollapsibleScaffoldTopBarScope

object CollapsibleTopAppBarDefaults {
    // Replicating the value in androidx.compose.material.AppBar.AppBarHeight which is private
    val minHeight = 56.dp
    val maxHeightSmall = 198.dp
    val maxHeightLarge = 280.dp

    /**
     *  When content height reach this point we start applying padding start and end
     */
    const val startScalingFraction = 0.3f
}

enum class CollapsibleTopAppBarMode {
    Default,
    Collapsed,
    Expanded;

    @Composable
    internal fun offset(): Int = when (this) {
        Default -> LocalScrollOffset.current.value ?: Int.MAX_VALUE
        Collapsed -> Int.MAX_VALUE
        Expanded -> 0
    }

    @Composable
    internal fun insets(): PaddingValues = when (this) {
        Default -> LocalInsets.current
        Collapsed,
        Expanded -> remember { PaddingValues(0.dp) }
    }
}

private val LocalScrollOffset = compositionLocalOf<State<Int?>> {
    mutableStateOf(null)
}

private val LocalInsets = compositionLocalOf {
    PaddingValues(0.dp)
}

private val LocalMaxHeight = compositionLocalOf {
    CollapsibleTopAppBarDefaults.maxHeightLarge
}

@Composable
fun CollapsibleScaffold(
    state: ScrollState,
    modifier: Modifier = Modifier,
    topBarMaxHeight: Dp = CollapsibleTopAppBarDefaults.maxHeightLarge,
    topBar: @Composable CollapsibleScaffoldTopBarScope.() -> Unit = {},
    content: @Composable (insets: PaddingValues) -> Unit
) {
    CollapsibleScaffoldInternal(
        offsetState = rememberOffsetScrollState(state),
        modifier = modifier,
        topBarMaxHeight = topBarMaxHeight,
        topBar = topBar,
        content = content
    )
}

@Composable
fun CollapsibleScaffold(
    state: LazyListState,
    modifier: Modifier = Modifier,
    topBarMaxHeight: Dp = CollapsibleTopAppBarDefaults.maxHeightLarge,
    topBar: @Composable CollapsibleScaffoldTopBarScope.() -> Unit = {},
    content: @Composable (insets: PaddingValues) -> Unit
) {
    CollapsibleScaffoldInternal(
        offsetState = rememberOffsetScrollState(state),
        modifier = modifier,
        topBarMaxHeight = topBarMaxHeight,
        topBar = topBar,
        content = content
    )
}

@Composable
private fun CollapsibleScaffoldInternal(
    offsetState: State<Int?>,
    modifier: Modifier = Modifier,
    topBarMaxHeight: Dp,
    topBar: @Composable CollapsibleScaffoldTopBarScope.() -> Unit = {},
    content: @Composable (insets: PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        backgroundColor = Color.Transparent
    ) { insets ->
        Box {
            Box(
                modifier = Modifier.padding(top = CollapsibleTopAppBarDefaults.minHeight)
            ) {
                content(
                    PaddingValues(
                        top = topBarMaxHeight - CollapsibleTopAppBarDefaults.minHeight,
                        bottom = 16.dp
                    )
                )
            }
            CompositionLocalProvider(
                LocalScrollOffset provides offsetState,
                LocalInsets provides insets,
                LocalMaxHeight provides topBarMaxHeight
            ) {
                CollapsibleScaffoldTopBarScope.topBar()
            }
        }
    }
}

@Composable
fun CollapsibleScaffoldTopBarScope.TopBar(
    modifier: Modifier = Modifier,
    mode: CollapsibleTopAppBarMode = CollapsibleTopAppBarMode.Default,
    onBack: () -> Unit = {},
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: (@Composable CollapsibleTopAppBarScope.() -> Unit) = { }
) {
    TopBar(
        modifier = modifier,
        mode = mode,
        actions = actions,
        content = content,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        },
    )
}

@Composable
fun CollapsibleScaffoldTopBarScope.TopBar(
    modifier: Modifier = Modifier,
    mode: CollapsibleTopAppBarMode = CollapsibleTopAppBarMode.Default,
    actions: (@Composable RowScope.() -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    content: (@Composable CollapsibleTopAppBarScope.() -> Unit) = { }
) {
    TopBarInternal(
        scrollOffset = mode.offset(),
        insets = mode.insets(),
        modifier = modifier.background(Color.Transparent),
        navigationIcon = navigationIcon,
        actions = actions,
        content = content
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun TopBarInternal(
    scrollOffset: Int,
    insets: PaddingValues,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    maxHeight: Dp = LocalMaxHeight.current,
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable CollapsibleTopAppBarScope.() -> Unit
) {
    val density = LocalDensity.current
    val actionsSize = remember { mutableStateOf(IntSize.Zero) }
    val navIconSize = remember { mutableStateOf(IntSize.Zero) }
    val actionWidth = with(density) { actionsSize.value.width.toDp() }
    val backWidth = with(density) { navIconSize.value.width.toDp() }
    val bodyHeight = maxHeight - CollapsibleTopAppBarDefaults.minHeight
    val maxOffset = with(density) {
        bodyHeight.roundToPx() - insets.calculateTopPadding().roundToPx()
    }

    val offset = min(scrollOffset, maxOffset)
    val fraction = 1f - kotlin.math.max(0f, offset.toFloat()) / maxOffset
    val currentMaxHeight = bodyHeight * fraction

    BoxWithConstraints(modifier = modifier) {
        val maxWidth = maxWidth
        Row(
            modifier = Modifier
                .height(CollapsibleTopAppBarDefaults.minHeight)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.onGloballyPositioned {
                    navIconSize.value = it.size
                }
            ) {
                if (navigationIcon != null) {
                    navigationIcon()
                }
            }

            Spacer(
                modifier = Modifier.weight(1f),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .widthIn(0.dp, maxWidth / 2)
                    .onGloballyPositioned { actionsSize.value = it.size }
            ) {
                if (actions != null) {
                    actions()
                }
            }
        }

        val scaleFraction = (fraction / CollapsibleTopAppBarDefaults.startScalingFraction).coerceIn(0f, 1f)
        val paddingStart = if (fraction > CollapsibleTopAppBarDefaults.startScalingFraction) {
            0.dp
        } else {
            lerp(backWidth, 0.dp, scaleFraction)
        }

        val paddingEnd = if (fraction > CollapsibleTopAppBarDefaults.startScalingFraction) {
            0.dp
        } else {
            lerp(actionWidth, 0.dp, scaleFraction)
        }

        /**
         *  When content height reach minimum size, we start translating it to fit the toolbar
         */
        val minHeightDiff = currentMaxHeight - CollapsibleTopAppBarDefaults.minHeight
        val paddingTop = if (minHeightDiff > 0.dp) {
            CollapsibleTopAppBarDefaults.minHeight
        } else {
            CollapsibleTopAppBarDefaults.minHeight + minHeightDiff
        }

        BoxWithConstraints(
            modifier = Modifier
                .padding(top = paddingTop, start = paddingStart, end = paddingEnd)
                .height(max(CollapsibleTopAppBarDefaults.minHeight, currentMaxHeight))
                .fillMaxWidth()
                .align(Alignment.BottomStart),
        ) {
            val scope = remember(fraction, this) {
                CollapsibleTopAppBarScope(fraction = fraction, scope = this)
            }
            content(scope)
        }
    }
}

class CollapsibleTopAppBarScope(
    val fraction: Float,
    scope: BoxWithConstraintsScope
) : BoxWithConstraintsScope by scope

@Composable
private fun rememberOffsetScrollState(state: ScrollState): MutableState<Int?> {
    val offsetState = rememberSaveable { mutableStateOf<Int?>(null) }
    offsetState.value = state.value
    return offsetState
}

@Composable
private fun rememberOffsetScrollState(state: LazyListState): MutableState<Int?> {
    val offsetState = rememberSaveable { mutableStateOf<Int?>(null) }
    val firstItem = remember(state) {
        derivedStateOf {
            val firstItem = state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == 0 }
            firstItem?.offset?.absoluteValue
        }
    }
    offsetState.value = firstItem.value
    return offsetState
}