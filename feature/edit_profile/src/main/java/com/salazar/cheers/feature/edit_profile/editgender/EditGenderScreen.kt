package com.salazar.cheers.feature.edit_profile.editgender

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Gender
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.ui.Toolbar

@Composable
fun EditGenderScreen(
    gender: Gender?,
    onGenderClick: (Gender) -> Unit,
    navigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = "Gender",
                onBackPressed = navigateBack,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            EditGenderRadio(
                gender = gender,
                onGenderClick = onGenderClick,
            )
        }
    }
}

@Composable
fun EditGenderRadio(
    gender: Gender?,
    onGenderClick: (Gender) -> Unit,
) {
    val genders = Gender.entries
    genders.forEach {
        ListItem(
            modifier = Modifier.clickable { onGenderClick(it) },
            leadingContent = {
                RadioButton(
                    selected = it == gender,
                    onClick = {
                        onGenderClick(it)
                    },
                )
            },
            headlineContent = {
                Text(text = it.value)
            }
        )
    }
    var sliderPosition by rememberSaveable { mutableFloatStateOf(0f) }
    AnimatedVisibility(
        visible = gender == Gender.OTHER,
        enter = expandVertically(),
        exit = shrinkVertically(animationSpec = tween(durationMillis = 1000))
    ) {
        Slider(
            value = sliderPosition,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onValueChange = {
                sliderPosition = it
            }
        )
    }
}

@Composable
fun EditGenderHeader(
    gender: String,
    onGenderClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        Card() {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Image(
                    painter = painterResource(com.salazar.cheers.core.ui.R.drawable.default_profile_picture),
                    contentDescription = "",
                )
                Text(
                    text = "Male",
                )
            }
        }
        Card() {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Image(
                    painter = painterResource(com.salazar.cheers.core.ui.R.drawable.default_profile_picture),
                    contentDescription = "",
                )
                Text(
                    text = "Female",
                )
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun EditGenderScreenPreview() {
    CheersPreview {
        EditGenderScreen(
            gender = Gender.OTHER,
            onGenderClick = {},
            navigateBack = {},
        )
    }
}