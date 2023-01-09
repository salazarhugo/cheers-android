package com.salazar.cheers.ui.main.story

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.internal.Beverage
import com.salazar.cheers.ui.carousel
import com.salazar.cheers.ui.compose.post.PostHeader
import com.salazar.cheers.ui.compose.sheets.StoryMoreBottomSheet
import com.salazar.cheers.ui.compose.story.StoryProgressBar
import com.salazar.cheers.ui.compose.utils.PrettyImage
import kotlin.math.absoluteValue


@Composable
fun StoryScreen(
    uiState: StoryUiState,
    onStoryUIAction: (StoryUIAction, String) -> Unit,
    onStoryOpen: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onUserClick: (String) -> Unit,
    onInputChange: (String) -> Unit,
    onSendReaction: (Story, String) -> Unit,
    showInterstitialAd: () -> Unit,
    onPauseChange: (Boolean) -> Unit,
    onCurrentStepChange: (Int) -> Unit,
) {
}
