package com.salazar.cheers.feature.map.ui.annotations

//@Composable
//fun PostAnnotation(
//    modifier: Modifier = Modifier,
//    post: Post,
//    isSelected: Boolean = false,
//    onClick: () -> Unit,
//) {
//    val color = when(isSelected) {
//        true -> com.salazar.cheers.core.ui.theme.getBlueCheers
//        false -> Color.White
//    }
//
//    val size = when(isSelected) {
//        true -> 140.dp
//        false -> 100.dp
//    }
//
//    val picture = when(post.photos.isEmpty()) {
//        true -> post.profilePictureUrl
//        false -> post.photos.first()
//    }
//
//    Column(
//        modifier = modifier
//            .size(size),
//    ) {
//        Box(
//            contentAlignment = Alignment.BottomEnd
//        ) {
//            Bounce(
//                onBounce = onClick,
//            ) {
//                AsyncImage(
//                    model = picture,
//                    contentDescription = null,
//                    modifier = modifier
//                        .border(2.dp, color, MaterialTheme.shapes.medium)
//                        .clip(MaterialTheme.shapes.medium),
//                    contentScale = ContentScale.Crop
//                )
//            }
//            Image(
//                painter = painterResource(R.drawable.ic_beer),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(42.dp)
//                    .offset(x = 8.dp, y = 8.dp)
//                ,
//            )
//        }
//        Text(
//            text = post.username,
//            modifier = Modifier
//                .background(Color.White)
//                .padding(),
//            color = Color.Black,
//            style = MaterialTheme.typography.bodyMedium,
//            overflow = TextOverflow.Ellipsis,
//            maxLines = 1,
//        )
//    }
//}