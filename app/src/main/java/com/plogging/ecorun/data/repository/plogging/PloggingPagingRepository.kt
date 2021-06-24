package com.plogging.ecorun.data.repository.plogging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.flowable
import com.plogging.ecorun.data.local.PloggingDatabase
import com.plogging.ecorun.data.model.MyDatabasePlogging
import com.plogging.ecorun.data.remote.plogging.PloggingRemoteMediator
import com.plogging.ecorun.network.PloggingApiService
import io.reactivex.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class PloggingPagingRepository @Inject constructor(
    private val ploggingApiService: PloggingApiService,
    private val ploggingDatabase: PloggingDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getUserPloggingData(
        userId: String,
        searchType: Int,
    ): Flowable<PagingData<MyDatabasePlogging>> {
        val orderBy = when(searchType){
            0 -> "createdTime"
            1 -> "trashSum"
            2 ->"ploggingTotalScore"
            else ->  "createdTime"
        }
        val pagingSourceFactory =
            { ploggingDatabase.myPloggingDao().getAllPlogging(userId, orderBy) }
        return Pager(
            config = PagingConfig(
                pageSize = 200, // 페이지당 갯수 position에 해당
                enablePlaceholders = false, // 로드되지 않은 것은 null이 아닌 아예 보여지지 않음
//                maxSize = 100, // (prefetchDistance + pageSize * 2) 이상으로 설정
//                prefetchDistance = 4, // 엣지에서 로드될 아이템 갯수
//                initialLoadSize = 10 // 최초 로드 사이즈
            ),
            remoteMediator = PloggingRemoteMediator(
                ploggingApiService,
                ploggingDatabase,
                userId,
                searchType
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flowable
    }
}
