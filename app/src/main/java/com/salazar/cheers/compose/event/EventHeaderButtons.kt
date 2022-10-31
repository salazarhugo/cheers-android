package com.salazar.cheers.compose.event

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EventHeaderButtons(
    hostId: String,
    going: Boolean,
    interested: Boolean,
    onManageClick: () -> Unit,
    onInviteClick: () -> Unit,
    onInterestedClick: () -> Unit,
    onGoingClick: () -> Unit,
) {
    val uid = remember { FirebaseAuth.getInstance().currentUser?.uid!! }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (uid == hostId) {
            Button(
                onClick = onManageClick,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.Handyman, null)
                Spacer(Modifier.width(4.dp))
                Text("Manage")
                Icon(Icons.Default.ArrowDropDown, null)
            }
            Spacer(Modifier.width(8.dp))
            FilledTonalButton(
                onClick = onInviteClick,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.Email, null)
                Spacer(Modifier.width(4.dp))
                Text("Invite")
            }
        } else {
            EventGoingButton(
                going = going,
                onGoingToggle = onGoingClick,
//                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            EventInterestButton(
                interested = interested,
                modifier = Modifier.weight(1f),
                onInterestedToggle = { onInterestedClick() },
            )
        }
    }
}
