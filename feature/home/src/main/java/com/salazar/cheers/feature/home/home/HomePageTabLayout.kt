package com.salazar.cheers.feature.home.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import kotlinx.coroutines.launch

@Composable
fun HomePageTabLayout(
    modifier: Modifier = Modifier,
    tabs: List<String>,
    pagerState: PagerState,
) {
    val coroutineScope = rememberCoroutineScope()

    fun changeTab(newTab: String) {
        val newTabIndex = tabs.indexOf(newTab)
        coroutineScope.launch {
            pagerState.animateScrollToPage(newTabIndex)
        }
    }

    HomePageTabLayout(
        modifier = modifier,
        tabs = tabs,
        selectedTab = tabs.getOrNull(pagerState.currentPage),
        onTabSelected = ::changeTab,
    )
}

@Composable
private fun HomePageTabLayout(
    modifier: Modifier = Modifier,
    tabs: List<String>,
    selectedTab: String?,
    onTabSelected: (String) -> Unit,
) {
    val selectedTabIndex = tabs.indexOf(selectedTab)
    val backgroundIndicator = MaterialTheme.colorScheme.onBackground
    val density = LocalDensity.current
    val tabWidths = remember { mutableMapOf<Int, Dp>() }

    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        tabs = {
            tabs.forEachIndexed { tabIndex, tabTitle ->
                val a =
                    if (tabTitle == selectedTab) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
                val textColor =
                    animateColorAsState(targetValue = a, label = "")

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .height(48.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (tabTitle == selectedTab) backgroundIndicator else MaterialTheme.colorScheme.background)
                        .clickable { onTabSelected.invoke(tabTitle) }
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                        text = tabTitle,
                        color = textColor.value,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        onTextLayout = { textLayoutResult ->
                            tabWidths[tabIndex] =
                                with(density) { textLayoutResult.size.width.toDp() }
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (tabIndex == 1) {
                        Text(
                            modifier = Modifier,
                            text = "BETA",
                            color = textColor.value.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.labelSmall.copy(
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            onTextLayout = { textLayoutResult ->
                                tabWidths[tabIndex] =
                                    with(density) { textLayoutResult.size.width.toDp() }
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        },
        indicator = {},
        divider = {}
    )
}

@ComponentPreviews
@Composable
private fun TabLayoutPreview() {
    val tabs = listOf("Drive & Livraison", "Magasin")
    CheersPreview {
        Column {
            Box(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                HomePageTabLayout(
                    tabs = tabs,
                    selectedTab = tabs.first(),
                    onTabSelected = {},
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                HomePageTabLayout(
                    tabs = tabs,
                    selectedTab = tabs[1],
                    onTabSelected = {},
                )
            }
        }
    }
}
