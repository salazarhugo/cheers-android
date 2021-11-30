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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.salazar.cheers.ui.theme.CheersTheme
import kotlinx.coroutines.launch

const val ConversationTestTag = "ConversationTestTag"

class ChatFragment : Fragment() {

    private val viewModel: ChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

        val windowInsets = ViewWindowInsetObserver(this)
            .start(windowInsetsAnimationsEnabled = true)

        setContent {
            CompositionLocalProvider(
                LocalWindowInsets provides windowInsets,
            ) {
                CheersTheme {
                    ChatScreen(
                        modifier = Modifier.navigationBarsPadding(bottom = false)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatScreen(
        modifier: Modifier
    ) {
        val messages = listOf<Message>(
            TextMessage(text = "@Hugo Hey!"),
            TextMessage("What's up?", senderId = "dw"),
            TextMessage(text = "Not much"),
            TextMessage(text = "Wbu?"),
            TextMessage("Just chilling haha", senderId = "dw"),
            TextMessage("Ainsi, toujours, vers l'azur noir\n" +
                    "Où tremble la mer des topazes,\n" +
                    "Fonctionneront dans ton soir\n" +
                    "Les Lys, ces clystères d'extases !\n" +
                    "\n" +
                    "À notre époque de sagous,\n" +
                    "Quand les Plantes sont travailleuses,\n" +
                    "Le Lys boira les bleus dégoûts\n" +
                    "Dans tes Proses religieuses !\n" +
                    "\n" +
                    "- Le lys de monsieur de Kerdrel,\n" +
                    "Le Sonnet de mil huit cent trente,\n" +
                    "Le Lys qu'on donne au Ménestrel\n" +
                    "Avec l'oeillet et l'amarante !\n" +
                    "\n" +
                    "Des lys ! Des lys ! On n'en voit pas !\n" +
                    "Et dans ton Vers, tel que les manches\n" +
                    "Des Pécheresses aux doux pas,\n" +
                    "Toujours frissonnent ces fleurs blanches !\n" +
                    "\n" +
                    "Toujours, Cher, quand tu prends un bain,\n" +
                    "Ta chemise aux aisselles blondes\n" +
                    "Se gonfle aux brises du matin\n" +
                    "Sur les myosotis immondes !\n" +
                    "\n" +
                    "L'amour ne passe à tes octrois\n" +
                    "Que les Lilas, - ô balançoires !\n" +
                    "Et les Violettes du Bois,\n" +
                    "Crachats sucrés des Nymphes noires !..."),
        )
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
                        onMessageSent = {},
                        resetScroll = {
                            scope.launch {
                                scrollState.scrollToItem(0)
                            }
                        },
                        modifier = Modifier.navigationBarsWithImePadding(),
                    )
                }
                ChannelNameBar(
                    channelName = "",
                    channelMembers = 2,
                    onNavIconPressed = {},
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

            message.authorImage?.let {
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

        val styledMessage = messageFormatter(
            text = message.chatChannelId,
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

