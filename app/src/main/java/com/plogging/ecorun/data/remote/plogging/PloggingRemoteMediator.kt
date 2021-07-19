package com.plogging.ecorun.data.remote.plogging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxRemoteMediator
import com.plogging.ecorun.data.local.PloggingDatabase
import com.plogging.ecorun.data.model.MyDatabasePlogging
import com.plogging.ecorun.data.model.PloggingKeys
import com.plogging.ecorun.data.response.PloggingResponse
import com.plogging.ecorun.network.PloggingApiService
import com.plogging.ecorun.ui.main.user.UserFragment.Companion.ploggingType
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalPagingApi::class)
class PloggingRemoteMediator(
    private val ploggingApiService: PloggingApiService,
    private val ploggingDatabase: PloggingDatabase,
    private val userId: String,
    private val searchType: Int
) : RxRemoteMediator<Int, MyDatabasePlogging>() {

    @ExperimentalCoroutinesApi
    override fun loadSingle(
        loadType: LoadType,
        state: PagingState<Int, MyDatabasePlogging>
    ): Single<MediatorResult> {
        return Single.just(loadType)
            .subscribeOn(Schedulers.io())
            .map {
                when (it) {
                    LoadType.REFRESH -> { // 초기화
                        val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                        remoteKeys?.nextKey?.minus(1) ?: 1
                    }
                    LoadType.PREPEND -> { // 각 페이지 시작 부분에서 로드
                        val remoteKeys = getRemoteKeyForFirstItem(state)
                        if (remoteKeys == null) 0 else
                            remoteKeys.prevKey ?: INVALID_PAGE
                    }
                    LoadType.APPEND -> { // 각 페이지 끝 부분에서 로드
                        val remoteKeys = getRemoteKeyForLastItem(state)
                        if (remoteKeys == null) 1 else
                            remoteKeys.nextKey ?: INVALID_PAGE
                    }
                }
            }
            .flatMap { page ->
                if (page == INVALID_PAGE || page == 0 || ploggingType != searchType)
                    Single.just(MediatorResult.Success(true))
                else {
                    ploggingApiService.getUserPloggingData(
                        userId,
                        searchType,
                        state.config.pageSize,
                        page
                    )
                        .observeOn(Schedulers.io())
                        .map { insertDB(page, loadType, it) }
                        .map<MediatorResult> { MediatorResult.Success(false) }
                        .onErrorReturn { MediatorResult.Error(it) }
                }
            }
            .onErrorReturn { MediatorResult.Error(it) }
    }

    private fun insertDB(page: Int, loadType: LoadType, data: PloggingResponse): PloggingResponse {
        val endOfPaginationReached = data.ploggingList.isEmpty()
        if (loadType == LoadType.REFRESH) {
            ploggingDatabase.ploggingKeysDao().clearRemoteKeys(userId)
            ploggingDatabase.myPloggingDao().clearPlogging(userId)
        }
        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (endOfPaginationReached) null else page + 1
        val keys = data.ploggingList.map {
            PloggingKeys(
                ploggingId = it.id,
                prevKey = prevKey,
                nextKey = nextKey,
                userId = it.ploggingDetail.userId
            )
        }
        val ploggings = data.ploggingList.map {
            MyDatabasePlogging(
                id = it.id,
                calories = it.ploggingDetail.calories,
                createdTime = it.ploggingDetail.createdTime,
                distance = it.ploggingDetail.distance,
                ploggingActivityScore = it.ploggingDetail.ploggingActivityScore,
                ploggingEnvironmentScore = it.ploggingDetail.ploggingEnvironmentScore,
                ploggingImg = it.ploggingDetail.ploggingImg,
                ploggingTime = it.ploggingDetail.ploggingTime,
                ploggingTotalScore = it.ploggingDetail.ploggingTotalScore,
                userId = it.ploggingDetail.userId,
                trashList = it.trashList,
                trashSum = it.trashList.sumOf { trash -> trash.pickCount }
            )
        }
        ploggingDatabase.ploggingKeysDao().insertAll(keys)
        ploggingDatabase.myPloggingDao().insertAll(ploggings)
        return data
    }

    private fun getRemoteKeyForLastItem(state: PagingState<Int, MyDatabasePlogging>): PloggingKeys? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { plogging ->
                ploggingDatabase.ploggingKeysDao().remoteKeysPloggingId(plogging.id)
            }

    private fun getRemoteKeyForFirstItem(state: PagingState<Int, MyDatabasePlogging>): PloggingKeys? =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { plogging ->
                ploggingDatabase.ploggingKeysDao().remoteKeysPloggingId(plogging.id)
            }

    private fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, MyDatabasePlogging>): PloggingKeys? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { ploggingId ->
                ploggingDatabase.ploggingKeysDao().remoteKeysPloggingId(ploggingId)
            }
        }

    companion object {
        const val INVALID_PAGE = -1
    }
}
