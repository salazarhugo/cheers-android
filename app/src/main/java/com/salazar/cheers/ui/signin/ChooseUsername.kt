package com.salazar.cheers.ui.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class ChooseUsernameFragment : Fragment() {

    private val viewModel: ChooseUsernameViewModel by viewModels()
//    private val args: ChooseUsernameFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ChooseUsernameScreen()
            }
        }
    }

    @Composable
    fun ChooseUsernameScreen() {
        val uiState = viewModel.uiState.collectAsState(ChooseUsernameState()).value

        if (uiState.isAvailable == true) {
//            val action = if (args.isFromGoogle)
//                ChooseUsernameFragmentDirections
//                    .actionChooseUsernameFragmentToSignInFragment(username = uiState.username)
//            else
//                ChooseUsernameFragmentDirections
//                    .actionChooseUsernameFragmentToCreatePasswordFragment(username = uiState.username)
//            findNavController().navigate(action)
            viewModel.reset()
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text("Choose username", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("You can't change it later")
            Spacer(Modifier.height(36.dp))
            UsernameTextField(uiState)
            Spacer(Modifier.height(8.dp))
            NextButton(uiState)
        }
    }

    @Composable
    fun NextButton(uiState: ChooseUsernameState) {
        Button(
            shape = RoundedCornerShape(8.dp),
            onClick = {
                viewModel.isUsernameAvailable()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !uiState.isLoading,
        ) {
            if (uiState.isLoading)
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.onSurface,
                    strokeWidth = 1.dp
                )
            else
                Text(text = "Next")
        }
    }

    @Composable
    fun UsernameTextField(uiState: ChooseUsernameState) {
        TextField(
            value = uiState.username,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            onValueChange = {
                viewModel.onUsernameChanged(it)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            keyboardActions = KeyboardActions(onSearch = {
            }),
            placeholder = { Text("Username") },
            trailingIcon = {
                if (uiState.isAvailable == true)
                    Icon(imageVector = Icons.Default.Check, "")
                else if (uiState.username.isNotEmpty())
                    IconButton(onClick = { viewModel.clearUsername() }) {
                        Icon(imageVector = Icons.Default.Close, "")
                    }
            },
            isError = uiState.isAvailable == false,
        )
        if (uiState.errorMessage.isNotEmpty())
            Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
    }

}
