package com.salazar.cheers.data.stubs

import cheers.post.v1.*
import cheers.type.PostOuterClass
import com.google.protobuf.Empty

class PostService : PostServiceGrpcKt.PostServiceCoroutineImplBase() {

    override suspend fun feedPost(request: FeedPostRequest): FeedPostResponse {
        return super.feedPost(request)
    }

    override suspend fun createPost(request: CreatePostRequest): PostResponse {
        return super.createPost(request)
    }

    override suspend fun deletePost(request: DeletePostRequest): Empty {
        return super.deletePost(request)
    }
}