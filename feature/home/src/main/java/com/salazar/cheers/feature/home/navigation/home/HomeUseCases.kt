package com.salazar.cheers.feature.home.navigation.home

import com.salazar.cheers.domain.feed_note.ListNoteFeedUseCase
import com.salazar.cheers.domain.feed_post.ListPostFeedUseCase
import com.salazar.cheers.domain.feed_story.ListStoryFeedUseCase
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.cheers.domain.get_notification_counter.GetNotificationCounterUseCase
import com.salazar.cheers.domain.get_unread_chat_counter.GetUnreadChatCounterUseCase
import com.salazar.cheers.domain.list_suggestions.ListSuggestionsUseCase
import com.salazar.cheers.domain.send_friend_request.SendFriendRequestUseCase
import javax.inject.Inject


data class HomeUseCases @Inject constructor(
    val listStoryFeed: ListStoryFeedUseCase,
    val getUnreadChatCounter: GetUnreadChatCounterUseCase,
    val getNotificationCounter: GetNotificationCounterUseCase,
    val listPostFeed: ListPostFeedUseCase,
    val listNoteFeed: ListNoteFeedUseCase,
    val listSuggestions: ListSuggestionsUseCase,
    val sendFriendRequest: SendFriendRequestUseCase,
    val getAccountUseCase: GetAccountUseCase,
)