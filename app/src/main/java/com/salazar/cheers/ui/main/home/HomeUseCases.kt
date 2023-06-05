package com.salazar.cheers.ui.main.home

import com.salazar.cheers.core.domain.usecase.feed_story.ListStoryFeedUseCase
import com.salazar.cheers.core.domain.usecase.get_notification_counter.GetNotificationCounterUseCase
import com.salazar.cheers.domain.send_friend_request.SendFriendRequestUseCase
import com.salazar.cheers.feature.chat.domain.usecase.get_unread_chat_counter.GetUnreadChatCounterUseCase
import com.salazar.cheers.post.domain.usecase.feed_post.ListPostFeedUseCase
import com.salazar.cheers.user.domain.usecase.list_suggestions.ListSuggestionsUseCase
import javax.inject.Inject


data class HomeUseCases @Inject constructor(
    val listStoryFeed: ListStoryFeedUseCase,
    val getUnreadChatCounter: GetUnreadChatCounterUseCase,
    val getNotificationCounter: GetNotificationCounterUseCase,
    val listPostFeed: ListPostFeedUseCase,
    val listSuggestions: ListSuggestionsUseCase,
    val sendFriendRequest: SendFriendRequestUseCase,
)