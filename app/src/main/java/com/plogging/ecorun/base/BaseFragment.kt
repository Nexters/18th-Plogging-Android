package com.plogging.ecorun.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import java.util.concurrent.TimeUnit

abstract class BaseFragment<B : ViewBinding, VM : ViewModel> : Fragment() {

    protected var disposables = CompositeDisposable()
    protected abstract fun getViewBinding(): B
    protected abstract fun clickListener()
    protected abstract val viewModel: VM
    protected lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        clickListener()
        return binding.root
    }

    protected fun View?.onSingleClickListener(
        block: () -> Unit
    ): Disposable = this?.clicks()?.throttleFirst(1000L, TimeUnit.MILLISECONDS)
        ?.observeOn(AndroidSchedulers.mainThread())?.subscribe({ block() }, {})!!
        .addTo(disposables)

    fun hideKeyboard(v: View) {
        val mInputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mInputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun onDestroy() {
        if (!disposables.isDisposed) {
            disposables.clear()
        }
        super.onDestroy()
    }
}