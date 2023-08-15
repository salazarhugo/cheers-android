package com.salazar.cheers.core.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
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
import com.salazar.common.ui.extensions.noRippleClickable
import kotlinx.coroutines.launch


@Composable
fun CarouselDrinks(
    pagerState: PagerState,
    drinks: List<Drink>,
    onBeverageClick: (Drink) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    HorizontalPager(
        modifier = Modifier.fillMaxWidth(),
        state = pagerState,
        verticalAlignment = Alignment.Top,
        contentPadding = PaddingValues(horizontal = 150.dp),
    ) { page ->
        val drink = drinks[page]
        val pageOffset = pagerState.currentPageOffsetFraction

        VerticalDrink(
            modifier = Modifier
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

    Spacer(Modifier.height(8.dp))

    Text(
        modifier = Modifier.fillMaxWidth(),
        text = drinks.getOrNull(currentPage)?.name ?: "",
        textAlign = TextAlign.Center,
    )
}

@Composable
fun VerticalDrink(
    drink: Drink,
    modifier: Modifier = Modifier,
    onBeverageClick: (Drink) -> Unit,
) {
    Column(
        modifier = modifier
            .noRippleClickable { onBeverageClick(drink) }
            .border(2.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
            .padding(8.dp),
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
