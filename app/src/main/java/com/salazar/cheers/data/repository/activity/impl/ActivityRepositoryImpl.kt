package com.salazar.cheers.data.repository.activity.impl

import cheers.activity.v1.ActivityServiceGrpcKt
import cheers.activity.v1.ListActivityRequest
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.ActivityDao
import com.salazar.cheers.data.mapper.toActivity
import com.salazar.cheers.data.repository.activity.ActivityRepository
import com.salazar.cheers.internal.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityRepositoryImpl @Inject constructor(
    private val activityDao: ActivityDao,
    private val service: ActivityServiceGrpcKt.ActivityServiceCoroutineStub,
) : ActivityRepository {

    override suspend fun listActivity(): Flow<Resource<List<Activity>>> = flow {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!

        emit(Resource.Loading(true))

        val localActivities = activityDao.listActivity(uid)
        emit(Resource.Success(localActivities))


        val remoteActivities = try {
            val request = ListActivityRequest.newBuilder().build()
            val response = service.listActivity(request)

            response.activitiesList.map {
                it.toActivity(uid)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("Couldn't refresh activity"))
            null
        }

        remoteActivities?.let { activities ->
            activityDao.insertActivities(activities)
            emit(Resource.Success(activityDao.listActivity(uid)))
        }

        emit(Resource.Loading(false))
    }

    override suspend fun countActivity(): Flow<Int> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        return activityDao.countUnreadActivity(uid)
    }

    override suspend fun acknowledgeAll() {
        activityDao.acknowledgeAll()
    }
}