package com.salazar.cheers.core.ui.components.coins

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.ProductDetails
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.text.BottomSheetTopBar
import com.salazar.cheers.shared.util.LocalActivity


@Composable
fun RechargeCoinsBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: RechargeCoinsViewModel = hiltViewModel(),
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit = {},
    onRewardedAdClick: () -> Unit,
) {
    val activity = LocalActivity.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismiss,
    ) {
        RechargeCoinsScreen(
            uiState = uiState,
            modifier = Modifier,
            onProductClick = {
                viewModel.onProductClick(it, activity = activity)
            },
            onRewardedAdClick = onRewardedAdClick
        )
    }
}

@Composable
fun RechargeCoinsScreen(
    uiState: RechargeUiState,
    modifier: Modifier = Modifier,
    onProductClick: (ProductDetails) -> Unit,
    onRewardedAdClick: () -> Unit,
) {
    RechargeScreen(
        modifier = modifier,
        coins = uiState.coins,
        recharges = uiState.productDetails ?: emptyList(),
        onRecharge = onProductClick,
        onRewardedAdClick = onRewardedAdClick
    )
}


@ScreenPreviews
@Composable
private fun RechargeCoinsBottomSheetPreview() {
    CheersPreview {
        RechargeCoinsScreen(
            uiState = RechargeUiState(
                coins = 3151,
                productDetails = listOf(ProductDetails(type = ""))
            ),
            modifier = Modifier,
            onProductClick = {},
            onRewardedAdClick = {},
        )
    }
}

@Composable
fun RechargeScreen(
    coins: Int,
    onRecharge: (ProductDetails) -> Unit,
    recharges: List<ProductDetails>,
    modifier: Modifier = Modifier,
    onRewardedAdClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        BottomSheetTopBar(
            text = "Recharge",
        )
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Balance:",
                style = MaterialTheme.typography.bodyMedium,
            )
            CoinsComponent(
                price = coins,
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        RechargeList(
            recharges = recharges,
            onRecharge = onRecharge,
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = onRewardedAdClick,
        ) {
            Text(text ="Watch Ad for 5 coins")
        }
    }
}

@Composable
fun RechargeList(
    recharges: List<ProductDetails>,
    onRecharge: (ProductDetails) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier,
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = recharges,
        ) {
            RechargeItem(
                recharge = it,
                modifier = Modifier.animateItem(),
                onRecharge = onRecharge
            )
        }
    }
}

@Composable
fun RechargeItem(
    recharge: ProductDetails,
    onRecharge: (ProductDetails) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onRecharge(recharge) }
            .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Outlined.MonetizationOn,
                    contentDescription = null,
                    tint = Color(0xFFFFA500),
                )
                Text(
                    text = recharge.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier,
                )
            }
            Text(
                text = recharge.formattedPrice.orEmpty(),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier,
            )
        }
    }
}