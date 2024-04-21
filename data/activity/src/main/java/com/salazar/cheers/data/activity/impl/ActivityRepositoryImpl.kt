package com.salazar.cheers.data.activity.impl

import cheers.activity.v1.ActivityServiceGrpcKt
import cheers.activity.v1.ListActivityRequest
import com.salazar.cheers.core.model.Activity
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.db.model.asExternalModel
import com.salazar.cheers.data.activity.ActivityRepository
import com.salazar.cheers.data.activity.toActivity
import com.salazar.common.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityRepositoryImpl @Inject constructor(
    private val activityDao: com.salazar.cheers.core.db.dao.ActivityDao,
    private val service: ActivityServiceGrpcKt.ActivityServiceCoroutineStub,
) : ActivityRepository {

    override suspend fun listActivity(): Flow<Resource<List<Activity>>> = flow {
        emit(Resource.Loading(true))

        val localActivities = activityDao.listActivity("")
        emit(Resource.Success(localActivities.asExternalModel()))


        val remoteActivities = try {
            val request = ListActivityRequest.newBuilder().build()
            val response = service.listActivity(request)

            response.activitiesList.map {
                it.toActivity()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("Couldn't refresh activity"))
            null
        }

        remoteActivities?.let { activities ->
            activityDao.insertActivities(activities.asEntity())
            emit(Resource.Success(activityDao.listActivity("").asExternalModel()))
        }

        emit(Resource.Loading(false))
    }

    override suspend fun countActivity(): Flow<Int> {
//        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        return activityDao.countUnreadActivity("")
    }

    override suspend fun acknowledgeAll() {
        activityDao.acknowledgeAll()
    }
}