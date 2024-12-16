package com.salazar.cheers.feature.parties.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.duplexParty
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.modifier.ShimmerShape

@Composable
fun PartyLineup(
    lineup: List<String>,
    modifier: Modifier = Modifier,
) {
    require(lineup.isNotEmpty())

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PartySectionTitle(
            text = "Lineup",
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(
                items = lineup,
            ) {
                LineupItem(
                    modifier = Modifier
                        .animateItem(),
                    lineup = it
                )
            }
        }
    }
}

@Composable
fun LineupItem(
    lineup: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.widthIn(max = 100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ShimmerShape(
            width = 100.dp,
            height = 100.dp,
            showShimmerAnimation = false,
        )
        Text(
            text = lineup,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Clip,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
private fun PartyLineupPreview() {
    val party = duplexParty
    CheersPreview {
        PartyLineup(
            lineup = party.lineup,
        )
    }
}
