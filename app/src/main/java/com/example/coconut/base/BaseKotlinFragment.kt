package com.example.coconut.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.coconut.util.showToast

abstract class BaseKotlinFragment<T : ViewDataBinding, R : BaseKotlinViewModel>  : Fragment() {

    lateinit var viewDataBinding: T

    /**
     * setContentView로 호출할 Layout의 리소스 Id.
     * ex) R.layout.activity_sbs_main
     */
    abstract val layoutResourceId: Int

    /**
     * viewModel 로 쓰일 변수.
     */
    abstract val viewModel: R

    abstract val baseToolBar : Toolbar?

    fun setToolbarTitle(title : String) { baseToolBar?.let { it.title = title } }

    fun showToast(message : String) { activity?.showToast(message) }


    /**
     * 레이아웃을 띄운 직후 호출.
     * 뷰나 액티비티의 속성 등을 초기화.
     * ex) 리사이클러뷰, 툴바, 드로어뷰..
     */
    abstract fun initStartView()

    /**
     * 두번째로 호출.
     * 데이터 바인딩 및 rxjava 설정.
     * ex) rxjava observe, databinding observe..
     */
    abstract fun initDataBinding()

    /**
     * 바인딩 이후에 할 일을 여기에 구현.
     * 그 외에 설정할 것이 있으면 이곳에서 설정.
     * 클릭 리스너도 이곳에서 설정.
     */
    abstract fun initAfterBinding()


    abstract fun setBaseToolbarItemClickListener(itemId: Int)

    abstract fun setMenuVisibilityOf(menu: Menu)


    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewDataBinding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)

        initStartView()
        /**
         * setHasOptionsMenu(true)를 통해
         * Activity.onCreateOptionsMenu 와 Activity.onOptionsItemSelected가
         * Fragment.onCreateOptionsMenu 와 Fragment.onOptionsItemSelected를
         * 다시 호출한다
         * */
        setHasOptionsMenu(true)
        initDataBinding()
        initAfterBinding()

        return viewDataBinding.root
    }


    override fun onResume() {
        super.onResume()

        /**
         * MainActivity의 toolbar 메뉴를
         * fragment에서 커스텀 하기 위함이다
         * (destroy all menu and re-call onCreateOptionsMenu)
         * */
        activity?.invalidateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setBaseToolbarItemClickListener(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        setMenuVisibilityOf(menu)
        super.onPrepareOptionsMenu(menu)
    }
}