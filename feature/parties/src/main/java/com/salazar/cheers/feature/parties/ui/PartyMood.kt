package com.salazar.cheers.feature.parties.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.duplexParty
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.Chip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PartyMood(
    musicGenres: List<String>,
    modifier: Modifier = Modifier,
) {
    require(musicGenres.isNotEmpty())

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PartySectionTitle(
            text = "Mood",
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        FlowRow(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            musicGenres.forEach {
                Chip(
                    name = it.uppercase(),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PartyMoodPreview() {
    val party = duplexParty
    CheersPreview {
        PartyMood(
            musicGenres = party.musicGenres,
        )
    }
}
