package com.salazar.cheers.core.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.modifier.carousel
import com.salazar.common.ui.extensions.noRippleClickable
import kotlinx.coroutines.launch


@Composable
fun CarouselDrinks(
    pagerState: PagerState,
    drinks: List<Drink>,
    modifier: Modifier = Modifier,
    onBeverageClick: (Drink) -> Unit = {},
) {
    if (drinks.isEmpty())
        return
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerState,
            verticalAlignment = Alignment.Top,
            contentPadding = PaddingValues(horizontal = 200.dp),
            pageSpacing = 16.dp,
        ) { page ->
            val drink = drinks[page]
            val pageOffset = pagerState.currentPageOffsetFraction

            VerticalDrink(
                modifier = Modifier
                    .fillMaxWidth()
                    .carousel(pageOffset),
                drink = drink,
                onBeverageClick = {
                    onBeverageClick(it)
                    val index = drinks.indexOf(it)
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = drinks.getOrNull(currentPage)?.name ?: "",
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun VerticalDrink(
    drink: Drink,
    modifier: Modifier = Modifier,
    onBeverageClick: (Drink) -> Unit,
) {
    Box(
        modifier = modifier
            .noRippleClickable { onBeverageClick(drink) }
            .border(2.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(drink.icon)
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
}

@ComponentPreviews
@Composable
fun CarouselDrinkPreview() {
    CheersPreview {
        CarouselDrinks(
            pagerState = rememberPagerState {
                2
            },
            drinks = listOf(
                Drink(0, "Beer", "", ""),
                Drink(1, "Wine", "", ""),
            ),
            onBeverageClick = {},
        )
    }
}
