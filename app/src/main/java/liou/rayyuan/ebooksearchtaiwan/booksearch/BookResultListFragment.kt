
package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rayliu.commonmain.domain.model.Book
import com.rayliu.commonmain.domain.model.SearchRecord
import java.util.Arrays
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.BaseFragment
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.arch.IView
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.BookResultViewState
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.ScreenState
import liou.rayyuan.ebooksearchtaiwan.databinding.FragmentSearchListBinding
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentArgumentsDelegate
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentViewBinding
import liou.rayyuan.ebooksearchtaiwan.utils.showToastOn
import liou.rayyuan.ebooksearchtaiwan.view.ViewEffectObserver
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookResultListFragment :
    BaseFragment(R.layout.fragment_search_list),
    View.OnClickListener,
    BookResultClickHandler,
    SearchRecordAdapter.OnSearchRecordsClickListener,
    IView<BookResultViewState> {

    companion object {
        fun newInstance(defaultKeyword: String?, defaultSnapshotSearchId: String?) = BookResultListFragment().apply {
            this.defaultSearchKeyword = defaultKeyword.orEmpty()
            this.defaultSnapshotSearchId = defaultSnapshotSearchId.orEmpty()
        }
        const val TAG = "book-result-list-fragment"
    }

    private val BUNDLE_RECYCLERVIEW_STATE: String = "BUNDLE_RECYCLERVIEW_STATE"
    private val KEY_RECYCLERVIEW_POSITION: String = "KEY_RECYCLERVIEW_POSITION"
    private val bookSearchViewModel: BookSearchViewModel by viewModel()

    private val viewBinding: FragmentSearchListBinding by FragmentViewBinding(FragmentSearchListBinding::bind)
    private var defaultSearchKeyword: String by FragmentArgumentsDelegate()
    private var defaultSnapshotSearchId: String by FragmentArgumentsDelegate()
    private var searchRecordAnimator: ValueAnimator? = null
    private lateinit var fullBookStoreResultsAdapter: FullBookStoreResultAdapter

    //region View Components
    private lateinit var appbar: AppBarLayout
    private lateinit var adViewLayout: FrameLayout
    private lateinit var searchButton: ImageView
    private lateinit var cameraButton: ImageView
    private lateinit var searchEditText: EditText

    private lateinit var resultsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private lateinit var hintText: TextView
    private lateinit var backToTopButton: ImageButton
    private lateinit var shareResultMenu: MenuItem

    private lateinit var searchRecordsRootView: FrameLayout
    private lateinit var searchRecordsCardView: CardView
    private lateinit var searchRecordsRecyclerView: RecyclerView
    private lateinit var searchRecordsBackground: View
    private val searchRecordsAdapter = SearchRecordAdapter(this)
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = viewBinding.searchViewToolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        bindViews(view)
        init()

        fullBookStoreResultsAdapter = FullBookStoreResultAdapter(this, this)
        resultsRecyclerView.adapter = fullBookStoreResultsAdapter
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val recyclerViewState = savedInstanceState.getParcelable<Parcelable>(BUNDLE_RECYCLERVIEW_STATE)
            resultsRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)

            val recyclerViewPosition = savedInstanceState.getInt(KEY_RECYCLERVIEW_POSITION, 0)
            (resultsRecyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(recyclerViewPosition, 0)
            bookSearchViewModel.savePreviousScrollPosition(recyclerViewPosition)
            Log.i("BookResultListFragment", "restore recyclerView Position = $recyclerViewPosition")
        }
        bookSearchViewModel.screenViewState.observe(
            viewLifecycleOwner
        ) { _ ->
            ViewEffectObserver<ScreenState> {
                updateScreen(it)
            }
        }
        bookSearchViewModel.viewState.observe(
            viewLifecycleOwner,
            { state -> render(state) }
        )

        sendUserIntent(BookSearchUserIntent.OnViewReadyToServe)
        setupUI()
        handleInitialDeepLink()
    }

    private fun handleInitialDeepLink() {
        lifecycleScope.launchWhenResumed {
            if (defaultSearchKeyword.isNotBlank()) {
                searchWithText(defaultSearchKeyword)
                defaultSearchKeyword = ""
            } else if (defaultSnapshotSearchId.isNotBlank()) {
                showSearchSnapshot(defaultSnapshotSearchId)
                defaultSnapshotSearchId = ""
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_RECYCLERVIEW_STATE, resultsRecyclerView.layoutManager?.onSaveInstanceState())
        val recyclerViewPosition = (resultsRecyclerView.layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition()
        outState.putInt(KEY_RECYCLERVIEW_POSITION, recyclerViewPosition ?: 0)
        Log.i("BookResultListFragment", "save recyclerView Position = $recyclerViewPosition")
    }

    private fun bindViews(view: View) {
        appbar = view.findViewById(R.id.search_view_appbar)
        adViewLayout = view.findViewById(R.id.search_view_adview_layout)
        searchButton = view.findViewById(R.id.search_view_search_icon)
        cameraButton = view.findViewById(R.id.search_view_camera_icon)
        searchEditText = view.findViewById(R.id.search_view_edittext)

        resultsRecyclerView = view.findViewById(R.id.search_view_result)
        progressBar = view.findViewById(R.id.search_view_progressbar)

        hintText = view.findViewById(R.id.search_view_hint)
        backToTopButton = view.findViewById(R.id.search_view_back_to_top_button)

        searchRecordsRootView = view.findViewById(R.id.layout_search_records_rootview)
        searchRecordsCardView = view.findViewById(R.id.layout_search_records_card_view)
        searchRecordsRecyclerView = view.findViewById(R.id.layout_search_records_recycler_view)
        searchRecordsBackground = view.findViewById(R.id.search_view_search_records_background)
    }

    private fun init() {
        searchButton.setOnClickListener(this)
        if (isCameraAvailable()) {
            cameraButton.setOnClickListener(this)
        } else {
            cameraButton.visibility = View.GONE
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchWithEditText()
                return@setOnEditorActionListener true
            }
            false
        }

        searchEditText.setOnKeyListener(
            View.OnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        searchWithEditText()
                        return@OnKeyListener true
                    }

                    if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
                        hideVirtualKeyboard()
                        searchEditText.clearFocus()
                        return@OnKeyListener true
                    }
                }
                false
            }
        )

        searchEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            sendUserIntent(BookSearchUserIntent.FocusOnTextEditing(hasFocus))
        }

        with(searchRecordsRecyclerView) {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = searchRecordsAdapter
        }

        hintText.setOnClickListener(this)
        hintText.compoundDrawables
            .filterNotNull()
            .forEach { DrawableCompat.setTint(it, ContextCompat.getColor(requireContext(), R.color.gray)) }

        val linearLayoutManager = resultsRecyclerView.layoutManager as LinearLayoutManager
        linearLayoutManager.initialPrefetchItemCount = 6

        resultsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (searchEditText.isFocused) {
                    searchEditText.clearFocus()
                }
            }
        })
        searchRecordsBackground.setOnClickListener(this)

        loadAds()
        initScrollToTopButton()
    }

    override fun onDestroy() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(null)
        searchRecordsAdapter.release()
        fullBookStoreResultsAdapter.release()
        super.onDestroy()
    }

    private fun searchWithEditText() {
        hideVirtualKeyboard()
        searchEditText.clearFocus()
        val keyword: String = searchEditText.text.toString()
        sendUserIntent(BookSearchUserIntent.SearchBook(keyword))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_page, menu)
        shareResultMenu = menu.findItem(R.id.search_page_menu_action_share)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_page_menu_action_setting -> {
                if (isAdded) {
                    (requireActivity() as? BookSearchActivity)?.openPreferenceActivity()
                }
                true
            }
            R.id.search_page_menu_action_share -> {
                val targetKeyword = searchEditText.text.toString()
                if (!TextUtils.isEmpty(targetKeyword)) {
                    val text = "https://taiwan-ebook-lover.github.io/search?q=$targetKeyword"
                    val intent = Intent(Intent.ACTION_SEND)
                    with(intent) {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "From " + getString(R.string.app_name))
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    startActivity(Intent.createChooser(intent, getString(R.string.menu_share_menu_appear)))
                }
                true
            }
            else -> true
        }
    }

    private fun loadAds() {
        val adView: AdView = adViewLayout.findViewById(R.id.admob_view_header_adview)
        val configurationBuilder = RequestConfiguration.Builder()
        if (BuildConfig.DEBUG) {
            configurationBuilder.setTestDeviceIds(Arrays.asList(BuildConfig.ADMOB_TEST_DEVICE_ID))
        }
        MobileAds.setRequestConfiguration(configurationBuilder.build())
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun initScrollToTopButton() {
        backToTopButton.setOnClickListener(this)

        if (!isAdded) {
            return
        }

        backToTopButton.setBackgroundResource(R.drawable.material_rounded_button_green)
        resultsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (resultsRecyclerView.canScrollVertically(-1)) {
                    backToTopButton.setImageResource(requireContext(), R.drawable.ic_keyboard_arrow_up_24dp)
                } else {
                    backToTopButton.setImageResource(requireContext(), R.drawable.ic_search_white_24dp)
                }
            }
        })
    }

    private fun setupUI() {
        val hintWithAppVersion = hintText.text.toString() + "\n" + resources.getString(R.string.app_version, BuildConfig.VERSION_NAME)
        hintText.text = hintWithAppVersion
    }

    override fun render(viewState: BookResultViewState) {
        renderMainResultView(viewState)
    }

    private fun renderMainResultView(bookResultViewState: BookResultViewState) {
        when (bookResultViewState) {
            is BookResultViewState.PrepareBookResult -> {
                progressBar.visibility = View.VISIBLE
                resultsRecyclerView.visibility = View.GONE
                hintText.visibility = View.GONE
                backToTopButton.visibility = View.GONE
                adViewLayout.visibility = View.VISIBLE

                searchButton.isEnabled = false
                cameraButton.isEnabled = false

                if (this::shareResultMenu.isInitialized) {
                    shareResultMenu.setVisible(false)
                }

                if (bookResultViewState.scrollToTop) {
                    scrollToTop()
                }
            }
            is BookResultViewState.ShowBooks -> {
                if (bookResultViewState.adapterItems.isNotEmpty()) {
                    if (this::fullBookStoreResultsAdapter.isInitialized) {
                        fullBookStoreResultsAdapter.clean()
                        fullBookStoreResultsAdapter.addResult(bookResultViewState.adapterItems)
                    }
                }

                progressBar.visibility = View.GONE
                resultsRecyclerView.visibility = View.VISIBLE
                hintText.visibility = View.GONE
                backToTopButton.visibility = View.VISIBLE
                adViewLayout.visibility = View.GONE

                searchButton.isEnabled = true
                cameraButton.isEnabled = true

                if (this::shareResultMenu.isInitialized) {
                    shareResultMenu.setVisible(true)
                }

                if (bookResultViewState.scrollPosition > 0) {
                    scrollToPosition(bookResultViewState.scrollPosition)
                }
            }
            BookResultViewState.PrepareBookResultError -> {
                progressBar.visibility = View.GONE
                resultsRecyclerView.visibility = View.GONE
                hintText.visibility = View.VISIBLE
                backToTopButton.visibility = View.GONE
                adViewLayout.visibility = View.GONE

                searchButton.isEnabled = true
                cameraButton.isEnabled = true

                if (this::shareResultMenu.isInitialized) {
                    shareResultMenu.setVisible(false)
                }
            }
            is BookResultViewState.ShowSearchRecordList -> {
                val itemCounts = bookResultViewState.itemCounts
                val heightPadding = if (itemCounts < 5) {
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        (36f / itemCounts),
                        resources.displayMetrics
                    ).toInt()
                } else {
                    0
                }

                val adapterItemHeight = resources.getDimensionPixelSize(R.dimen.search_records_item_height)

                toggleSearchRecordView(true, (adapterItemHeight + heightPadding) * itemCounts)
                bookSearchViewModel.searchRecordLiveData.observe(
                    viewLifecycleOwner,
                    Observer { searchRecords ->
                        searchRecordsAdapter.addItems(searchRecords)
                    }
                )
            }
            BookResultViewState.HideSearchRecordList -> {
                if (this::searchRecordsRootView.isInitialized) {
                    bookSearchViewModel.searchRecordLiveData.removeObservers(viewLifecycleOwner)
                    toggleSearchRecordView(false)
                    (searchRecordsRecyclerView.layoutManager as? LinearLayoutManager)?.scrollToPosition(0)
                }
            }
        }
    }

    private fun updateScreen(screenState: ScreenState) {
        when (screenState) {
            ScreenState.EasterEgg -> {
                showEasterEgg01()
            }
            ScreenState.ConnectionTimeout -> {
                showInternetConnectionTimeout()
            }
            ScreenState.NetworkError -> {
                showNetworkErrorMessage()
            }
            ScreenState.EmptyKeyword -> {
                showKeywordIsEmpty()
            }
            ScreenState.NoInternetConnection -> {
                showInternetRequestDialog()
            }
            is ScreenState.ShowToastMessage -> {
                if (screenState.stringResId != BookSearchViewModel.NO_MESSAGE) {
                    showToast(getString(screenState.stringResId))
                } else {
                    showToast(screenState.message)
                }
            }
        }
    }

    private fun scrollToTop() {
        resultsRecyclerView.scrollToPosition(0)
    }

    private fun openBook(book: Book) {
        if (isAdded) {
            (requireActivity() as? BookSearchActivity)?.openBookLink(book)
        }
    }

    private fun showInternetRequestDialog() {
        if (isAdded) {
            val dialogBuilder = MaterialAlertDialogBuilder(requireActivity())
            dialogBuilder.setTitle(R.string.network_alert_dialog_title)
            dialogBuilder.setMessage(R.string.network_alert_message)
            dialogBuilder.setPositiveButton(R.string.dialog_ok) { _: DialogInterface, _: Int -> }
            dialogBuilder.create().show()
        }
    }

    private fun showInternetConnectionTimeout() {
        if (isAdded) {
            getString(R.string.state_timeout).showToastOn(requireContext())
        }
    }

    private fun showKeywordIsEmpty() {
        if (isAdded) {
            Toast.makeText(requireContext(), R.string.search_keyword_empty, Toast.LENGTH_LONG).show()
        }
    }

    private fun hideVirtualKeyboard() {
        if (isAdded) {
            val inputManager: InputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }
    }

    private fun focusBookSearchEditText() {
        if (isAdded) {
            appbar.setExpanded(true, true)
            searchEditText.requestFocus()
            val inputManager: InputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(searchEditText, 0)
        }
    }

    private fun showEasterEgg01() {
        if (isAdded) {
            Toast.makeText(requireContext(), R.string.easter_egg_01, Toast.LENGTH_LONG).show()
        }
    }

    private fun showNetworkErrorMessage() {
        if (isAdded) {
            getString(R.string.network_error_message).showToastOn(requireContext())
        }
    }

    private fun backToListTop() {
        resultsRecyclerView.smoothScrollToPosition(0)
    }

    private fun scrollToPosition(position: Int) {
        (resultsRecyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, 0)
    }

    private fun showToast(message: String) {
        if (isAdded) {
            message.showToastOn(requireContext())
        }
    }

    //region View.OnClickListener
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.search_view_search_icon -> {
                hideVirtualKeyboard()
                searchEditText.clearFocus()
                val keyword: String = searchEditText.text.toString()
                sendUserIntent(BookSearchUserIntent.SearchBook(keyword))
            }
            R.id.search_view_hint -> {
                hintPressed()
            }
            R.id.search_view_camera_icon -> {
                if (isAdded) {
                    (requireActivity() as? BookSearchActivity)?.openCameraPreviewActivity()
                }
            }
            R.id.search_view_back_to_top_button -> {
                val canListScrollVertically = resultsRecyclerView.canScrollVertically(-1)
                backToTop(canListScrollVertically)
            }
            R.id.search_view_search_records_background -> {
                toggleSearchRecordView(false)
                hideVirtualKeyboard()
            }
        }
    }

    private fun backToTop(canResultListScrollVertically: Boolean) {
        if (canResultListScrollVertically) {
            backToListTop()
            eventTracker.logEvent(EventTracker.CLICK_BACK_TO_TOP_BUTTON)
        } else {
            focusBookSearchEditText()
            eventTracker.logEvent(EventTracker.CLICK_TO_SEARCH_BUTTON)
        }
    }

    private fun hintPressed() {
        sendUserIntent(BookSearchUserIntent.PressHint)
        focusBookSearchEditText()
    }
    //endregion

    private fun isCameraAvailable(): Boolean {
        if (isAdded) {
            return requireActivity().packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ?: false
        }
        return false
    }

    private fun ImageButton.setImageResource(context: Context, @DrawableRes drawableId: Int) {
        ContextCompat.getDrawable(context, drawableId)?.run {
            if (isDarkTheme()) {
                DrawableCompat.setTint(this, ContextCompat.getColor(context, R.color.pure_dark))
            } else {
                DrawableCompat.setTint(this, ContextCompat.getColor(context, R.color.pure_white))
            }
            this@setImageResource.setImageDrawable(this)
        }
    }

    fun searchWithText(text: String) {
        searchEditText.setText(text)
        searchEditText.setSelection(text.length)
        searchEditText.clearFocus()
        hideVirtualKeyboard()
        sendUserIntent(BookSearchUserIntent.SearchBook(text))
    }

    fun showSearchSnapshot(searchId: String) {
        hideVirtualKeyboard()
        sendUserIntent(BookSearchUserIntent.ShowSearchSnapshot(searchId))
    }

    fun onBackPressed(): Boolean {
        if (searchEditText.isFocused) {
            searchEditText.clearFocus()
            return true
        }

        return false
    }

    fun toggleSearchRecordView(show: Boolean, targetHeight: Int = 0) {
        if (!this::searchRecordsRootView.isInitialized) {
            return
        }

        if (show) {
            expandSearchRecordView(searchRecordsRootView, targetHeight)
        } else {
            collapseSearchRecordView(searchRecordsRootView)
        }
    }

    private fun expandSearchRecordView(view: FrameLayout, targetHeight: Int) {
        val maxHeight = resources.getDimensionPixelSize(R.dimen.search_records_max_height)
        val height = if (targetHeight > maxHeight) {
            maxHeight
        } else {
            targetHeight
        }

        animateViewHeight(view, height)
    }

    private fun collapseSearchRecordView(view: FrameLayout) {
        animateViewHeight(view, 0)
    }

    private fun animateViewHeight(view: FrameLayout, targetHeight: Int) {
        searchRecordAnimator?.takeIf { it.isRunning }?.run {
            removeAllListeners()
            removeAllUpdateListeners()
            cancel()
        }

        val animation = ValueAnimator.ofInt(view.measuredHeightAndState, targetHeight)
        animation.duration = 150
        animation.setInterpolator(DecelerateInterpolator())
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                val isBackgroundVisible = searchRecordsBackground.visibility == View.VISIBLE
                val isGoingToExpand = targetHeight > 0
                if (isBackgroundVisible && !isGoingToExpand) {
                    searchRecordsBackground.visibility = View.GONE
                    backToTopButton.visibility = View.VISIBLE
                    return
                }

                if (!isBackgroundVisible && isGoingToExpand) {
                    searchRecordsBackground.visibility = View.VISIBLE
                    backToTopButton.visibility = View.GONE
                }
            }
        })
        animation.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        animation.start()
        searchRecordAnimator = animation
    }

    //region BookResultClickHandler
    override fun onBookCardClicked(book: Book) {
        openBook(book)
    }
    //endregion

    //region SearchRecordAdapter.OnSearchRecordsClickListener
    override fun onSearchRecordClicked(searchRecord: SearchRecord) {
        searchWithText(searchRecord.text)
    }

    override fun onSearchRecordCloseImageClicked(searchRecord: SearchRecord, position: Int) {
        requireContext().let {
            val message = getString(R.string.alert_dialog_delete_search_record_message, searchRecord.text)
            MaterialAlertDialogBuilder(it)
                .setTitle(R.string.alert_dialog_delete_search_records)
                .setMessage(message)
                .setPositiveButton(getString(R.string.dialog_ok)) { dialog, _ ->
                    sendUserIntent(BookSearchUserIntent.DeleteSearchRecord(searchRecord))
                    searchRecordsAdapter.notifyItemRemoved(position)
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, _ -> dialog.dismiss() }
                .create().show()
        }
    }
    //endregion

    private fun sendUserIntent(userIntent: BookSearchUserIntent) {
        lifecycleScope.launch {
            bookSearchViewModel.userIntents.send(userIntent)
        }
    }
}
