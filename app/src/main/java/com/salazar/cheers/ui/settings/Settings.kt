package com.salazar.cheers.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.internal.User

class SettingsFragment : Fragment() {

//    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SettingsScreen()
            }
        }
    }

    @Composable
    fun SettingsScreen() {
        val settings = listOf("", "", "")
        Settings(settings = settings)
    }

    @Composable
    fun Settings(settings: List<Any>) {
        LazyColumn() {
            items(settings) { setting ->
                Setting(setting)
                DividerM3()
            }
        }
    }

    @Composable
    fun Setting(setting: Any) {
        val checkedState = remember { mutableStateOf(true) }
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
            ) {
                Text(text = "Title")
                Text(text = "Subtitle")
            }
            Switch(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it }
            )
        }
    }
}
