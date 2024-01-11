package com.salazar.cheers.core.ui.components.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.PostText
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.data.post.repository.Post
import java.util.Date

@Composable
fun PostComponent(
    post: Post,
    modifier: Modifier = Modifier,
    onUserClick: (String) -> Unit = {},
    onMoreClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onLikeCountClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
) {
    val pagerState = rememberPagerState(
        pageCount = { post.photos.size },
    )

    Column(
        modifier = modifier,
    ) {
        PostHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp),
            username = post.username,
            verified = post.verified,
            avatar = post.profilePictureUrl,
            locationName = post.locationName,
            createTime = post.createTime,
            isPublic = post.privacy == Privacy.PUBLIC.name,
            onUserClick = { onUserClick(post.authorId) },
            onMoreClick = onMoreClick,
        )
        PostText(
            caption = post.caption,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, end = 16.dp, start = 16.dp),
            onUserClicked = { },
            onPostClicked = { },
        )
        PostBody(
            post = post,
            pagerState = pagerState,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            onPostClick = { },
            onDoubleTap = {
                if (post.liked.not()) {
                    onLikeClick()
                }
            },
        )
        Spacer(modifier = Modifier.height(16.dp))
        PostDrink(
            drink = post.drinkName,
            picture = post.drinkPicture,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        PostFooter(
            photoCount = post.photos.size,
            likeCount = post.likes,
            drunkenness = post.drunkenness,
            commentCount = post.comments,
            hasViewerLiked = post.liked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            pagerState = pagerState,
            onLikeClick = onLikeClick,
            onLikeCountClick = onLikeCountClick,
            onCommentClick = onCommentClick,
            onShareClick = { },
        )
    }
}

@ComponentPreviews
@Composable
private fun PostComponentPreview() {
    CheersPreview {
        PostComponent(
            modifier = Modifier.padding(16.dp),
            post = Post(
                username = "cheers",
                isAuthor = true,
                caption = "\uD83D\uDCE2 Invest in a warehouse with showroom in Agios Nicolaos, Larnaka!",
                drinkName = "Beer",
                createTime = Date().time / 1000,
                likes = 346334,
                comments = 325,
//                photos = listOf("https://scontent.cdninstagram.com/v/t51.2885-15/405504793_330833509699453_1944996773569160338_n.jpg?stp=dst-jpg_e35&efg=eyJ2ZW5jb2RlX3RhZyI6ImltYWdlX3VybGdlbi4xMzQ5eDE2ODcuc2RyIn0&_nc_ht=scontent.cdninstagram.com&_nc_cat=1&_nc_ohc=vlLRm-duywYAX9m_2aj&edm=APs17CUBAAAA&ccb=7-5&ig_cache_key=MzI0NjgxMzMxMDYwNjc0MDQ1Mg%3D%3D.2-ccb7-5&oh=00_AfDB7QL18GNUxiNeNkW5GKme6HSRMPEhlzY5MFkZr6UVBw&oe=656E951B&_nc_sid=10d13b")
            ),
        )
    }
}

@ComponentPreviews
@Composable
private fun PostComponentPreviewImages() {
    CheersPreview {
        PostComponent(
            modifier = Modifier.padding(16.dp),
            post = Post(
                username = "cheers",
                isAuthor = true,
                drinkName = "Beer",
                createTime = Date().time / 1000,
                likes = 346334,
                comments = 325,
                photos = listOf("", "", "", "", ""),
            ),
        )
    }
}
