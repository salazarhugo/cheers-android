package com.salazar.cheers.data.activity.impl

import cheers.activity.v1.ActivityServiceGrpcKt
import cheers.activity.v1.ListActivityRequest
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.db.model.asExternalModel
import com.salazar.cheers.core.model.Activity
import com.salazar.cheers.core.model.ActivityType
import com.salazar.cheers.data.activity.ActivityRepository
import com.salazar.cheers.data.activity.toActivity
import com.salazar.cheers.shared.data.toDataError
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityRepositoryImpl @Inject constructor(
    private val activityDao: com.salazar.cheers.core.db.dao.ActivityDao,
    private val service: ActivityServiceGrpcKt.ActivityServiceCoroutineStub,
) : ActivityRepository {

    override val filtersFlow: MutableStateFlow<ActivityType> = MutableStateFlow(ActivityType.NONE)

    override fun updateFilter(filter: ActivityType) {
        filtersFlow.update { filter }
    }

    override fun listActivityFlow(): Flow<List<Activity>> {
        return activityDao.listActivityFlow().map { it.asExternalModel() }
    }

    override suspend fun listActivity(): Result<Unit, DataError> {
        try {
            val request = ListActivityRequest.newBuilder().build()
            val response = service.listActivity(request)

            val activities = response.activitiesList.map {
                it.toActivity()
            }
            activityDao.insertActivities(activities.asEntity())

            return Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.Error(e.toDataError())
        }
    }

    override suspend fun countActivity(): Flow<Int> {
//        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        return activityDao.countUnreadActivity("")
    }

    override suspend fun acknowledgeAll() {
        activityDao.acknowledgeAll()
    }
}