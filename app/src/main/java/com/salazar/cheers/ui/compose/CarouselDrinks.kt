package com.salazar.cheers.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.salazar.cheers.internal.Beverage
import com.salazar.cheers.ui.carousel
import com.salazar.cheers.ui.compose.extensions.noRippleClickable
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@Composable
fun CarouselDrinks(
    pagerState: PagerState,
    drinks: List<Beverage>,
    onBeverageClick: (Beverage) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    HorizontalPager(
        modifier = Modifier.fillMaxWidth(),
        count = drinks.size,
        state = pagerState,
        verticalAlignment = Alignment.Top,
        contentPadding = PaddingValues(horizontal = 150.dp),
        itemSpacing = 0.dp,
    ) { page ->
        val drink = drinks[page]
        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

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
        text = drinks[currentPage].displayName,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun VerticalDrink(
    drink: Beverage,
    modifier: Modifier = Modifier,
    onBeverageClick: (Beverage) -> Unit,
) {
    Column(
        modifier = modifier
            .noRippleClickable { onBeverageClick(drink) }
            .border(2.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
            .padding(8.dp),
    ) {
        Image(
            painter = rememberAsyncImagePainter(drink.icon),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
}
