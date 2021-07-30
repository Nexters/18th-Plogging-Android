package com.plogging.ecorun.ui.main.user

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.base.BaseLoadStateAdapter
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.data.model.GlobalRank
import com.plogging.ecorun.databinding.FragmentUserBinding
import com.plogging.ecorun.ui.main.MainViewModel
import com.plogging.ecorun.util.GridSpacingItemDecoration
import com.plogging.ecorun.util.extension.*
import com.plogging.ecorun.util.glide.GlideApp
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UserFragment : BaseFragment<FragmentUserBinding, UserViewModel>() {
    private val fromRankUserData by lazy { arguments?.getSerializable("rankUserData") as GlobalRank? }
    override fun getViewBinding(): FragmentUserBinding = FragmentUserBinding.inflate(layoutInflater)
    private val adapter by lazy { UserPagingAdapter(fromRankUserData) }
    override val viewModel: UserViewModel by viewModels()
    private lateinit var mainViewModel: MainViewModel
    private var moveToZeroPosition = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSharedViewModel()
        initAdapter()
        backPress()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 플로깅이나 프로필의 변경된 사항을 적용하기위해 plogging 가져오는 메서드를 onViewCreated에 구현
        if (fromRankUserData == null) initMyView()
        else initOtherUserView()
        initSpinner()
        getPlogging()
        responseApi()
        customBottom()
        setTooltip()
    }

    private fun initSharedViewModel() {
        parentFragment?.parentFragment?.let {
            mainViewModel = ViewModelProvider(it).get(MainViewModel::class.java)
        }
    }

    private fun initAdapter() {
        binding.rvUserPlogging.adapter = adapter.withLoadStateHeaderAndFooter(
            header = BaseLoadStateAdapter { adapter.retry() },
            footer = BaseLoadStateAdapter { adapter.retry() }
        )
        binding.rvUserPlogging.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(4)
            addItemDecoration(
                GridSpacingItemDecoration(
                    spacing = 4.dpToPx(requireContext()), // 16dp
                    spanCount = 2, // 2columns
                    includeEdge = true // 네 변의 margin 설정 여부
                )
            )
        }
        //로딩이 완료되면 position 0으로 이동
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .filter { moveToZeroPosition }
                .collect { binding.rvUserPlogging.scrollToPosition(0) }
        }
        adapter.addLoadStateListener { loadState ->
            // show empty list
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(isListEmpty)
            // 새로고침이 완료되었을 때 리스트 보여주기
            binding.rvUserPlogging.isVisible = loadState.source.refresh is LoadState.NotLoading
            // 로딩 중일때  프로그래스마 보여주기
            binding.pgUserPlogging.isVisible = loadState.source.refresh is LoadState.Loading
            showErrorState(loadState)
        }
    }

    private fun initSpinner() {
        val array = resources.getStringArray(R.array.order)
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, array)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUserPloggingOrder.adapter = spinnerAdapter
        binding.spinnerUserPloggingOrder.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.searchType.value = position
                    ploggingType = position
                    viewModel.getUserPloggingData()
                        ?.subscribe { adapter.submitData(lifecycle, it) }
                        ?.addTo(disposables)
                    moveToZeroPosition = true
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    @SuppressLint("SetTextI18n")
    private fun responseApi() {
        viewModel.userData.observe(viewLifecycleOwner) {
            binding.tvUserPloggingTotalScore.text = it.scoreMonthly.toShort4() + "점"
            binding.tvUserPloggingDistanceNumber.text =
                it.distanceMonthly.toFloat().meterToKilometer().distanceToShort4() + "km"
            binding.tvUserPloggingScore.text = it.trashMonthly.toShort4() + "개"
        }
    }

    private fun getPlogging() {
        viewModel.userId.value = if (fromRankUserData == null)
            SharedPreference.getUserEmail(requireContext())
        else fromRankUserData?.userId
        viewModel.getUserData()
    }

    private fun showErrorState(loadState: CombinedLoadStates) {
        val errorState = loadState.source.append as? LoadState.Error
            ?: loadState.source.prepend as? LoadState.Error
            ?: loadState.append as? LoadState.Error
            ?: loadState.prepend as? LoadState.Error
        errorState?.let { requireContext().toast("\uD83D\uDE28 Wooops ${it.error}") }
    }

    private fun showEmptyList(isVisible: Boolean) {
        binding.rvUserPlogging.isVisible = !isVisible
    }

    private fun customBottom() {
        if (fromRankUserData == null) { // 자신의 플로깅
            mainViewModel.showBottomNav.value = true
            setMargins(binding.rvUserPlogging, 0, 0, 0, 82.dpToPx(requireContext()))
        } else {
            mainViewModel.showBottomNav.value = null
            setMargins(binding.rvUserPlogging, 0, 0, 0, 82.dpToPx(requireContext()))
        }
    }

    private fun initMyView() {
        moveToZeroPosition = false
        binding.tvUserPloggingName.text = SharedPreference.getUserName(requireContext())
        GlideApp.with(requireContext())
            .load(SharedPreference.getUserImage(requireContext()))
            .into(binding.ivUserPloggingProfile)
    }

    private fun initOtherUserView() {
        moveToZeroPosition = false
        binding.ivUserPloggingSetting.setImageResource(R.drawable.ic_back_arrow_white)
        binding.tvUserPloggingName.text = fromRankUserData?.displayName
        GlideApp.with(requireContext())
            .load(fromRankUserData?.profileImg)
            .into(binding.ivUserPloggingProfile)
    }

    private fun setTooltip() {
        viewModel.userData.observe(viewLifecycleOwner) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.clUserPloggingScore.tooltipText = it.scoreMonthly.inputComma() + "점"
                binding.clUserPloggingDistance.tooltipText = it.distanceMonthly.inputComma() + "m"
                binding.clUserPloggingCount.tooltipText = it.trashMonthly.inputComma() + "개"
            }
        }
    }

    override fun clickListener() {
        binding.ivUserPloggingSetting.onSingleClickListener {
            if (fromRankUserData == null) {
                findNavController().navigate(R.id.action_user_to_setting)
            } else findNavController().popBackStack()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.clUserPloggingScore.tooltipText = viewModel.userData.value?.scoreMonthly + "점"
        }
    }

    // 뒤로가기 클릭 시 라이프 사이클을 해당 프레그먼트에만 뒤로가기를 설정한다.
    private fun backPress() {
        if (fromRankUserData == null) activity
            ?.onBackPressedDispatcher
            ?.addCallback(this) { activity?.finish() }
    }

    companion object {
        var ploggingType = 0
    }
}