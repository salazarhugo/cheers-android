package com.salazar.cheers.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.insets.*
import com.salazar.cheers.R
import com.salazar.cheers.components.ChannelNameBar
import com.salazar.cheers.components.UserInput
import com.salazar.cheers.internal.ImageMessage
import com.salazar.cheers.internal.Message
import com.salazar.cheers.internal.MessageType
import com.salazar.cheers.internal.TextMessage
import com.salazar.cheers.ui.otherprofile.OtherProfileFragmentArgs
import com.salazar.cheers.ui.theme.CheersTheme
import kotlinx.coroutines.launch
import java.util.*

const val ConversationTestTag = "ConversationTestTag"

class ChatFragment : Fragment() {

    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

        val windowInsets = ViewWindowInsetObserver(this)
            .start(windowInsetsAnimationsEnabled = true)

        val channelId = "OEZAbSsTMRlmFVyd2lti"//chatViewModel.channelId.value

        setContent {
            CompositionLocalProvider(
                LocalWindowInsets provides windowInsets,
            ) {
                CheersTheme {
                    ChatScreen(
                        channelId = channelId,
                        modifier = Modifier.navigationBarsPadding(bottom = false)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatScreen(
        channelId: String,
        modifier: Modifier
    ) {
        val messages = chatViewModel.messages(channelId).collectAsState(initial = listOf()).value
        val scrollState = rememberLazyListState()
        val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }
        val scope = rememberCoroutineScope()

        Surface(modifier = modifier, color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize())
            {
                Column(
                    Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    Messages(
                        messages = messages,
                        navigateToProfile = {},
                        modifier = Modifier.weight(1f),
                        scrollState = scrollState,
                    )
                    UserInput(
                        onMessageSent = {
                            val a = TextMessage(
                                "", channelId, it, Date(), "", "", "", "", "",
                                arrayListOf(),
                                arrayListOf(),
                            )
                            chatViewModel.sendMessage(a, channelId)
                        },
                        resetScroll = {
                            scope.launch {
                                scrollState.scrollToItem(0)
                            }
                        },
                        modifier = Modifier.navigationBarsWithImePadding(),
                    )
                }
                ChannelNameBar(
                    channelName = channelId,
                    channelMembers = 2,
                    onNavIconPressed = { findNavController().popBackStack() },
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.statusBarsPadding(),
                )
            }

        }
    }

    @Composable
    fun Messages(
        messages: List<Message>,
        navigateToProfile: (String) -> Unit,
        scrollState: LazyListState,
        modifier: Modifier = Modifier
    ) {
        val scope = rememberCoroutineScope()
        Box(modifier = modifier) {
            LazyColumn(
//                reverseLayout = true,
                state = scrollState,
                contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.statusBars,
                    additionalTop = 90.dp
                ),
                modifier = Modifier
                    .testTag(ConversationTestTag)
                    .fillMaxSize()
            ) {
                for (index in messages.indices) {
                    val prevAuthor = messages.getOrNull(index - 1)?.senderName
                    val nextAuthor = messages.getOrNull(index + 1)?.senderName
                    val content = messages[index]
                    val isFirstMessageByAuthor = prevAuthor != content.senderName
                    val isLastMessageByAuthor = nextAuthor != content.senderName

                    // Hardcode day dividers for simplicity
                    if (index == messages.size - 1) {
                        item {
                            DayHeader("20 Aug")
                        }
                    } else if (index == 2) {
                        item {
                            DayHeader("Today")
                        }
                    }

                    item {
                        Message(
                            onAuthorClick = { name -> navigateToProfile(name) },
                            message = content,
                            isUserMe = content.senderId == "",
                            isFirstMessageByAuthor = isFirstMessageByAuthor,
                            isLastMessageByAuthor = isLastMessageByAuthor
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun Message(
        onAuthorClick: (String) -> Unit,
        isUserMe: Boolean,
        message: Message,
        isFirstMessageByAuthor: Boolean,
        isLastMessageByAuthor: Boolean
    ) {
        val borderColor = if (isUserMe) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.tertiary
        }
        val spaceBetweenAuthors = if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
        Row(modifier = spaceBetweenAuthors) {
            if (isLastMessageByAuthor) {
                // Avatar
                Image(
                    modifier = Modifier
                        .clickable(onClick = { onAuthorClick(message.senderName) })
                        .padding(horizontal = 16.dp)
                        .size(42.dp)
                        .border(1.5.dp, borderColor, CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .clip(CircleShape)
                        .align(Alignment.Top),
                    painter = rememberImagePainter(
                        data = message.authorImage,
                    ),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            } else {
                // Space under avatar
                Spacer(modifier = Modifier.width(74.dp))
            }
        }
        AuthorAndTextMessage(
            msg = message,
            isUserMe = isUserMe,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            authorClicked = onAuthorClick,
            modifier = Modifier
                .padding(end = 16.dp)
        )
    }

    @Composable
    fun AuthorAndTextMessage(
        msg: Message,
        isUserMe: Boolean,
        isFirstMessageByAuthor: Boolean,
        isLastMessageByAuthor: Boolean,
        authorClicked: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier) {
            if (isLastMessageByAuthor) {
                AuthorNameTimestamp(msg)
            }
            ChatItemBubble(msg, isUserMe, authorClicked = authorClicked)
            if (isFirstMessageByAuthor) {
                // Last bubble before next author
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                // Between bubbles
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }

    @Composable
    private fun AuthorNameTimestamp(msg: Message) {
        // Combine author and timestamp for a11y.
        Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
            Text(
                text = msg.senderUsername,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .alignBy(LastBaseline)
                    .paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = msg.time.toString(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alignBy(LastBaseline),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

    @Composable
    fun DayHeader(dayString: String) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .height(16.dp)
        ) {
            DayHeaderLine()
            Text(
                text = dayString,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            DayHeaderLine()
        }
    }

    @Composable
    private fun RowScope.DayHeaderLine() {
        // TODO (M3): No Divider, replace when available
        Divider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }

    @Composable
    fun ChatItemBubble(
        message: Message,
        isUserMe: Boolean,
        authorClicked: (String) -> Unit
    ) {

        val backgroundBubbleColor = if (isUserMe) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }

        Column {
            Surface(
                color = backgroundBubbleColor,
                shape = ChatBubbleShape
            ) {
                ClickableMessage(
                    message = message,
                    isUserMe = isUserMe,
                    authorClicked = authorClicked
                )
            }

            message.authorImage.let {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = backgroundBubbleColor,
                    shape = ChatBubbleShape
                ) {
//                    Image(
//                        painter = rememberImagePainter(
//                            data = it,
//                        ),
//                        contentScale = ContentScale.Fit,
//                        modifier = Modifier.size(160.dp),
//                        contentDescription = stringResource(id = R.string.attached_image)
//                    )
                }
            }
        }
    }

    @Composable
    fun ClickableMessage(
        message: Message,
        isUserMe: Boolean,
        authorClicked: (String) -> Unit
    ) {
        val uriHandler = LocalUriHandler.current

        val textMessage =
            if (message.type == MessageType.TEXT)
                message as TextMessage
            else
                null

        val styledMessage = messageFormatter(
            text = textMessage?.text ?: "Not a text message",
            primary = isUserMe
        )

        ClickableText(
            text = styledMessage,
            style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
            modifier = Modifier.padding(16.dp),
            onClick = {
                styledMessage
                    .getStringAnnotations(start = it, end = it)
                    .firstOrNull()
                    ?.let { annotation ->
                        when (annotation.tag) {
                            SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                            SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
                            else -> Unit
                        }
                    }
            }
        )
    }

}

