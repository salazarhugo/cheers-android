package com.salazar.cheers.feature.edit_profile.editjob

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.ui.CheersOutlinedTextField
import com.salazar.cheers.core.ui.ui.Toolbar

@Composable
fun EditJobScreen(
    jobTitle: String,
    jobCompany: String,
    navigateBack: () -> Unit,
    onJobTitleChange: (String) -> Unit,
    onJobCompanyChange: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = "Job",
                onBackPressed = navigateBack,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EditJob(
                jobTitle = jobTitle,
                jobCompany = jobCompany,
                onJobTitleChange = onJobTitleChange,
                onJobCompanyChange = onJobCompanyChange,
            )
        }
    }
}

@Composable
fun EditJob(
    jobTitle: String,
    jobCompany: String,
    onJobTitleChange: (String) -> Unit,
    onJobCompanyChange: (String) -> Unit,
) {
    CheersOutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = "Title",
            )
        },
        value = jobTitle,
        onValueChange = {
            onJobTitleChange(it)
        },
    )

    CheersOutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = "Company",
            )
        },
        value = jobCompany,
        onValueChange = {
            onJobCompanyChange(it)
        },
    )
}

@ScreenPreviews
@Composable
private fun EditJobScreenPreview() {
    CheersPreview {
        EditJobScreen(
            jobTitle = "Software Engineer",
            jobCompany = "Google",
            navigateBack = {},
            onJobCompanyChange = {},
            onJobTitleChange = {},
        )
    }
}