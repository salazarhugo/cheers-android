package com.salazar.cheers.data.friendship

import cheers.friendship.v1.AcceptFriendRequestRequest
import cheers.friendship.v1.CreateFriendRequestRequest
import cheers.friendship.v1.DeleteFriendRequest2
import cheers.friendship.v1.DeleteFriendRequestRequest
import cheers.friendship.v1.FriendshipServiceGrpcKt
import cheers.friendship.v1.ListFriendRequestsRequest
import com.salazar.cheers.core.db.model.FriendRequestEntity
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.shared.data.mapper.toUserItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class FriendshipRepositoryImpl @Inject constructor(
    private val friendRequestDao: com.salazar.cheers.core.db.dao.FriendRequestDao,
//    private val dataStoreRepo
    private val service: FriendshipServiceGrpcKt.FriendshipServiceCoroutineStub,
) : FriendshipRepository {
    override suspend fun createFriendRequest(userId: String): Result<Unit> {
        val request = CreateFriendRequestRequest.newBuilder()
            .setUserId(userId)
            .build()

        return try {
            service.createFriendRequest(request = request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun listFriend(): Flow<List<String>> {
        return flow {
            emit(listOf("friend1", "friend2"))
        }
    }


    override suspend fun listFriendRequest(): Flow<List<FriendRequestEntity>> {
        return friendRequestDao.listFriendRequests()
    }

    override suspend fun fetchFriendRequest(userId: String): Result<List<UserItem>> {
        val request = ListFriendRequestsRequest.newBuilder()
            .setUserId(userId)
            .build()

        val remoteFriendRequests = try {
            val response = service.listFriendRequests(request = request)
            val users = response.itemsList.map {
                it.toUserItem()
            }
//                userItemDao.insertAll(users)
            friendRequestDao.insertFriendRequests(
                friendRequests = response.itemsList.map {
                    FriendRequestEntity(id = it.id)
                }
            )
            users
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }

        return Result.success(remoteFriendRequests)
    }

    override suspend fun acceptFriendRequest(userId: String): Result<Unit> {
        val request = AcceptFriendRequestRequest.newBuilder()
            .setUserId(userId)
            .build()

        return try {
            service.acceptFriendRequest(request = request)
            friendRequestDao.delete(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelFriendRequest(userId: String): Result<Unit> {
        val request = DeleteFriendRequestRequest.newBuilder()
            .setUserId(userId)
            .build()

        return try {
            service.deleteFriendRequest(request = request)
            friendRequestDao.delete(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFriend(userId: String): Result<Unit> {
        val request = DeleteFriendRequest2.newBuilder()
            .setUserId(userId)
            .build()

        return try {
            service.deleteFriend(request = request)
//            userDao.deleteFriend(userId = userId)
//            userItemDao.deleteFriend(userId = userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFriendRequestCount(): Flow<Int> {
        return friendRequestDao.listFriendRequests()
            .map { it.size }
    }
}