package com.salazar.cheers.ui.sheets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.salazar.cheers.components.DividerM3

data class Sticker(
    val name: String,
    @DrawableRes val icon: Int,
    val price: Int,
    val onClick: (Sticker) -> Unit,
)

@Composable
fun SendGiftSheet(
    name: String,
    onStickerClick: (Sticker) -> Unit,
    bottomSheetNavigator: BottomSheetNavigator,
) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(16.dp),
        color = MaterialTheme.colorScheme.onBackground,
    )
    DividerM3()
    val items = listOf(
        Sticker(
            name = "Cheers",
            icon = 0,
            onClick = onStickerClick,
            price = 1,
        ),
        Sticker(
            name = "Shot",
            icon = com.salazar.cheers.R.drawable.ic_tequila_shot,
            onClick = onStickerClick,
            price = 5,
        ),
        Sticker(
            name = "Pint",
            icon = com.salazar.cheers.R.drawable.ic_beer,
            onClick = onStickerClick,
            price = 10,
        ),
        Sticker(
            name = "Pint",
            icon = 0,
            onClick = onStickerClick,
            price = 25,
        ),
        Sticker(
            name = "Bottle",
            icon = 0,
            onClick = onStickerClick,
            price = 2020,
        ),
        Sticker(
            name = "Magnum",
            icon = 0,
            onClick = onStickerClick,
            price = 5000,
        ),
        Sticker(
            name = "Methuselah",
            icon = 0,
            onClick = onStickerClick,
            price = 13299,
        ),
        Sticker(
            name = "Salmanazar",
            onClick = onStickerClick,
            icon = 0,
            price = 27800,
        ),
        Sticker(
            name = "Balthazar",
            onClick = onStickerClick,
            icon = 0,
            price = 50000,
        )
    )
    LazyVerticalGrid(columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(4)) {
        items(items) {
            Item(
                text = it.name,
                price = it.price,
                onClick = { it.onClick(it) },
            )
        }
    }
}

@Composable
fun Item(
    text: String,
    price: Int = 50,
    @DrawableRes icon: Int = com.salazar.cheers.R.drawable.ic_beer,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 0.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = rememberImagePainter(data = icon),
            contentDescription = null,
            modifier = Modifier.size(44.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "$price",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
