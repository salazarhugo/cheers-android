package com.salazar.cheers.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.salazar.cheers.ui.theme.Roboto

class ActivityFragment : Fragment() {

//    private val viewModel: ActivityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ActivityScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ActivityScreen() {
        Scaffold(
            topBar = { Toolbar() }
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
            }
        }
    }

    @Composable
    fun Toolbar() {
        SmallTopAppBar(
            title = {
                Text(
                    text = "Activity",
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto,
                )
            },
            navigationIcon = {
                Row {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.ArrowBack, "")
                    }
                    Spacer(Modifier.width(8.dp))
                }
            }
        )
    }
}
