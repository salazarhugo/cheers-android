package com.salazar.cheers.ui.event.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.salazar.cheers.components.Username
import com.salazar.cheers.internal.EventUi
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class EventDetailFragment : Fragment() {

    private val args: EventDetailFragmentArgs by navArgs()

    @Inject
    lateinit var eventDetailViewModelFactory: EventDetailViewModel.EventDetailViewModelFactory

    private val viewModel: EventDetailViewModel by viewModels {
        EventDetailViewModel.provideFactory(eventDetailViewModelFactory, eventId = args.eventId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                EventDetailScreen()
            }
        }
    }

    @Composable
    fun EventDetailScreen() {
        val uiState = viewModel.uiState.collectAsState().value

        Scaffold() {
            when (uiState) {
                is EventDetailUiState.HasEvent -> Event(eventUi = uiState.eventUi)
                is EventDetailUiState.NoEvents -> {
                    Text("No event")
                }
            }
        }
    }

    @Composable
    fun Event(eventUi: EventUi) {
        Column {
            EventHeader(eventUi = eventUi)
            EventBody(event = eventUi)
        }
    }

    @Composable
    fun EventHeader(eventUi: EventUi) {
        val event = eventUi.event
        Image(
            painter = rememberImagePainter(
                data = event.imageUrl,
                builder = {
                    error(com.salazar.cheers.R.drawable.image_placeholder)
                }
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            val d = remember { ZonedDateTime.parse(event.startDate) }
            Text(d.toLocalDateTime().format(DateTimeFormatter.ofPattern("E, d MMM hh:mm a")), style = MaterialTheme.typography.bodyMedium)
            if (event.name.isNotBlank())
                Text(event.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Text("${event.type.toLowerCase().capitalize()} event")
            if (event.description.isNotBlank())
                Text(event.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 8.dp))
            if (event.locationName.isNotBlank())
                Text(text = event.locationName, style = Typography.labelSmall)
            Text("4.8k interested - 567 going", modifier = Modifier.padding(vertical = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FilledTonalButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.StarBorder, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Interested")
                }
                FilledTonalButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Going")
                }
            }
        }
    }

    @Composable
    fun EventBody(event: EventUi) {
    }
}