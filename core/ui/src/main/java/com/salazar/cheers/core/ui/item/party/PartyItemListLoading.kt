package com.salazar.cheers.core.ui.item.party

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun PartyItemListLoading(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        repeat(20) {
            PartyItemLoading()
        }
    }
}

@ComponentPreviews
@Composable
private fun PartItemListLoadingPreview() {
    CheersPreview {
        PartyItemListLoading()
    }
}