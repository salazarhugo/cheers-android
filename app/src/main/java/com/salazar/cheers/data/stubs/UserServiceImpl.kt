package com.salazar.cheers.data.stubs

import cheers.type.UserOuterClass
import cheers.user.v1.*
import com.google.protobuf.Empty

class UserService : UserServiceGrpcKt.UserServiceCoroutineImplBase() {
    override suspend fun createUser(request: CreateUserRequest): UserOuterClass.User {
        return super.createUser(request)
    }

    override suspend fun getUser(request: GetUserRequest): GetUserResponse {
        return super.getUser(request)
    }

    override suspend fun updateUser(request: UpdateUserRequest): UserOuterClass.User {
        return super.updateUser(request)
    }

    override suspend fun deleteUser(request: DeleteUserRequest): Empty {
        return super.deleteUser(request)
    }

    override suspend fun searchUser(request: SearchUserRequest): SearchUserResponse {
        return super.searchUser(request)
    }

    override suspend fun followUser(request: FollowUserRequest): Empty {
        return super.followUser(request)
    }

    override suspend fun unfollowUser(request: UnfollowUserRequest): Empty {
        return super.unfollowUser(request)
    }

    override suspend fun blockUser(request: BlockUserRequest): BlockUserResponse {
        return super.blockUser(request)
    }

    override suspend fun unblockUser(request: UnblockUserRequest): UnblockUserResponse {
        return super.unblockUser(request)
    }
}