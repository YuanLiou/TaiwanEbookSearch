package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.BundleCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withResumed
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rayliu.commonmain.domain.model.Book
import com.rayliu.commonmain.domain.model.SearchRecord
import kotlinx.coroutines.launch
import liou.rayyuan.ebooksearchtaiwan.BaseFragment
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.arch.IView
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.SearchBox
import liou.rayyuan.ebooksearchtaiwan.booksearch.composable.ServiceStatusList
import liou.rayyuan.ebooksearchtaiwan.booksearch.review.PlayStoreReviewHelper
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.BookResultViewState
import liou.rayyuan.ebooksearchtaiwan.booksearch.viewstate.ScreenState
import liou.rayyuan.ebooksearchtaiwan.databinding.FragmentSearchListBinding
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker
import liou.rayyuan.ebooksearchtaiwan.ui.theme.EBookTheme
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentArgumentsDelegate
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentViewBinding
import liou.rayyuan.ebooksearchtaiwan.utils.setupEdgeToEdge
import liou.rayyuan.ebooksearchtaiwan.utils.showToastOn
import liou.rayyuan.ebooksearchtaiwan.utils.updateMargins
import liou.rayyuan.ebooksearchtaiwan.view.ViewEffectObserver
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookResultListFragment :
    BaseFragment(R.layout.fragment_search_list),
    View.OnClickListener,
    BookResultClickHandler,
    SearchRecordAdapter.OnSearchRecordsClickListener,
    IView<BookResultViewState> {
    private val bookSearchViewModel: BookSearchViewModel by viewModel()
    private val playStoreReviewHelper: PlayStoreReviewHelper by inject()

    private val viewBinding: FragmentSearchListBinding by FragmentViewBinding(
        FragmentSearchListBinding::bind
    )
    private var defaultSearchKeyword: String by FragmentArgumentsDelegate()
    private var defaultSnapshotSearchId: String by FragmentArgumentsDelegate()
    private var searchRecordAnimator: ValueAnimator? = null
    private lateinit var fullBookStoreResultsAdapter: FullBookStoreResultAdapter

    //region View Components
    private lateinit var resultsRecyclerView: RecyclerView

    private lateinit var shareResultMenu: MenuItem
    private lateinit var copyUrlMenu: MenuItem

    private lateinit var searchRecordsRootView: FrameLayout
    private lateinit var searchRecordsRecyclerView: RecyclerView
    private val searchRecordsAdapter = SearchRecordAdapter(this)
    //endregion

    private var hasUserSeenRankWindow = false
    private var openResultCounts = 0

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(viewBinding.searchViewToolbar)
        bindViews(view)
        init()
        setupOptionMenu()
        setupEdgeToEdge()

        fullBookStoreResultsAdapter = FullBookStoreResultAdapter(this, this)
        resultsRecyclerView.adapter = fullBookStoreResultsAdapter

        viewBinding.searchViewAppbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val alphaValue = 1f - ((verticalOffset * -1) / appBarLayout.totalScrollRange.toFloat())
            appBarLayout.alpha = alphaValue
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val recyclerViewState =
                BundleCompat.getParcelable(savedInstanceState, BUNDLE_RECYCLERVIEW_STATE, Parcelable::class.java)
            if (recyclerViewState != null) {
                resultsRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
            }

            val recyclerViewPosition = savedInstanceState.getInt(KEY_RECYCLERVIEW_POSITION, 0)
            (resultsRecyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                recyclerViewPosition,
                0
            )
            bookSearchViewModel.savePreviousScrollPosition(recyclerViewPosition)
            Log.i("BookResultListFragment", "restore recyclerView Position = $recyclerViewPosition")
        }

        // Render View Effect
        bookSearchViewModel.screenViewState.observe(
            viewLifecycleOwner,
            ViewEffectObserver {
                updateScreen(it)
            }
        )

        // Render Book Result State
        bookSearchViewModel.viewState.observe(
            viewLifecycleOwner
        ) { state -> render(state) }

        sendUserIntent(BookSearchUserIntent.OnViewReadyToServe)
        setupServiceStatusUi()
        setupToolbar()
        handleInitialDeepLink()

        viewLifecycleOwner.lifecycleScope.launch {
            hasUserSeenRankWindow = bookSearchViewModel.checkUserHasSeenRankWindow()
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                sendUserIntent(BookSearchUserIntent.CheckServiceStatus)
            }
        }
    }

    private fun setupServiceStatusUi() {
        viewBinding.searchViewComposeView.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                EBookTheme(
                    darkTheme = isDarkTheme()
                ) {
                    val bookStoreDetails =
                        bookSearchViewModel.bookStoreDetails
                            .collectAsStateWithLifecycle()
                            .value
                    ServiceStatusList(
                        storeDetails = bookStoreDetails,
                        modifier = Modifier
                    )
                }
            }
        }
    }

    private fun setupToolbar() {
        viewBinding.searchViewAppbarComposeView.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                EBookTheme(
                    darkTheme = isDarkTheme()
                ) {
                    val searchKeywords =
                        bookSearchViewModel.searchKeywords
                            .collectAsStateWithLifecycle()
                            .value

                    val focusAction =
                        bookSearchViewModel.focusTextInput
                            .collectAsStateWithLifecycle()
                            .value

                    val virtualKeyboardAction =
                        bookSearchViewModel.showVirtualKeyboard
                            .collectAsStateWithLifecycle()
                            .value

                    SearchBox(
                        text = searchKeywords,
                        onTextChange = {
                            sendUserIntent(BookSearchUserIntent.UpdateKeyword(it))
                        },
                        onPressSearch = {
                            sendUserIntent(BookSearchUserIntent.SearchBook())
                        },
                        focusAction = focusAction,
                        onFocusActionFinish = {
                            sendUserIntent(BookSearchUserIntent.ResetFocusAction)
                        },
                        onFocusChange = {
                            sendUserIntent(BookSearchUserIntent.UpdateTextInputFocusState(it.isFocused))
                        },
                        virtualKeyboardAction = virtualKeyboardAction,
                        showCameraButton = isCameraAvailable(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    private fun handleInitialDeepLink() {
        lifecycleScope.launch {
            withResumed {
                if (defaultSearchKeyword.isNotBlank()) {
                    searchWithText(defaultSearchKeyword)
                    defaultSearchKeyword = ""
                } else if (defaultSnapshotSearchId.isNotBlank()) {
                    showSearchSnapshot(defaultSnapshotSearchId)
                    defaultSnapshotSearchId = ""
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(
            BUNDLE_RECYCLERVIEW_STATE,
            resultsRecyclerView.layoutManager?.onSaveInstanceState()
        )
        val recyclerViewPosition =
            (resultsRecyclerView.layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition()
        outState.putInt(KEY_RECYCLERVIEW_POSITION, recyclerViewPosition ?: 0)
        Log.i("BookResultListFragment", "save recyclerView Position = $recyclerViewPosition")
    }

    private fun bindViews(view: View) {
        resultsRecyclerView = view.findViewById(R.id.search_view_result)

        searchRecordsRootView = view.findViewById(R.id.layout_search_records_rootview)
        searchRecordsRecyclerView = view.findViewById(R.id.layout_search_records_recycler_view)
    }

    private fun init() {
        with(searchRecordsRecyclerView) {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = searchRecordsAdapter
        }

        val linearLayoutManager = resultsRecyclerView.layoutManager as LinearLayoutManager
        linearLayoutManager.initialPrefetchItemCount = 6

        resultsRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    if (bookSearchViewModel.isTextInputFocused.value) {
                        sendUserIntent(BookSearchUserIntent.ForceFocusOrUnfocusKeywordTextInput(false))
                    }
                }
            }
        )
        viewBinding.searchViewSearchRecordsBackground.setOnClickListener(this)

        initAdMods()
        initScrollToTopButton()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            // remove AppBarLayout's shadow
            viewBinding.searchViewAppbar.outlineProvider = null
        }
    }

    override fun onDestroy() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(null)
        searchRecordsAdapter.release()
        fullBookStoreResultsAdapter.release()
        super.onDestroy()
    }

    private fun setupEdgeToEdge() {
        viewBinding.root.setupEdgeToEdge { view, insets ->
            val bars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                )

            view.updatePadding(
                left = bars.left,
                right = bars.right
            )

            viewBinding.searchViewBackToTopButton.updateMargins(bottom = bars.bottom)
            viewBinding.searchViewAppbar.updateMargins(top = bars.top)
            viewBinding.searchViewSearchRecordsBackground.updateMargins(top = bars.top)
        }
    }

    private fun setupOptionMenu() {
        (activity as? MenuHost)?.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater
                ) {
                    menuInflater.inflate(R.menu.search_page, menu)
                }

                override fun onPrepareMenu(menu: Menu) {
                    shareResultMenu =
                        menu.findItem(R.id.search_page_menu_action_share).also {
                            it.isVisible = bookSearchViewModel.hasPreviousSearch
                        }
                    copyUrlMenu =
                        menu.findItem(R.id.search_page_menu_action_copy_url).also {
                            it.isVisible = bookSearchViewModel.hasPreviousSearch
                        }
                }

                override fun onMenuItemSelected(item: MenuItem): Boolean =
                    when (item.itemId) {
                        R.id.search_page_menu_action_setting -> {
                            if (isAdded) {
                                (requireActivity() as? BookSearchActivity)?.openPreferenceActivity()
                            }
                            true
                        }

                        R.id.search_page_menu_action_share -> {
                            sendUserIntent(BookSearchUserIntent.ShareSnapshot)
                            true
                        }

                        R.id.search_page_menu_action_copy_url -> {
                            sendUserIntent(BookSearchUserIntent.CopySnapshotUrlToClipboard)
                            true
                        }

                        else -> {
                            true
                        }
                    }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    private fun initAdMods() {
        MobileAds.initialize(requireContext())
        val configurationBuilder = RequestConfiguration.Builder()
        if (BuildConfig.DEBUG) {
            configurationBuilder.setTestDeviceIds(listOf(BuildConfig.ADMOB_TEST_DEVICE_ID))
        }
        MobileAds.setRequestConfiguration(configurationBuilder.build())
    }

    private fun initScrollToTopButton() {
        viewBinding.searchViewBackToTopButton.setOnClickListener(this)
        viewBinding.searchViewBackToTopButton.setOnLongClickListener {
            focusAndCleanBookSearchEditText()
            true
        }

        if (!isAdded) {
            return
        }

        viewBinding.searchViewBackToTopButton.setBackgroundResource(R.drawable.material_rounded_button_green)
        resultsRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (resultsRecyclerView.canScrollVertically(-1)) {
                        viewBinding.searchViewBackToTopButton.setImageResource(
                            requireContext(),
                            R.drawable.ic_keyboard_arrow_up_24dp
                        )
                    } else {
                        viewBinding.searchViewBackToTopButton.setImageResource(
                            requireContext(),
                            R.drawable.ic_search_white_24dp
                        )
                    }
                }
            }
        )
    }

    override fun render(viewState: BookResultViewState) {
        renderMainResultView(viewState)
    }

    private fun renderMainResultView(bookResultViewState: BookResultViewState) {
        when (bookResultViewState) {
            is BookResultViewState.PrepareBookResult -> {
                viewBinding.searchViewProgressbar.visibility = View.VISIBLE
                resultsRecyclerView.visibility = View.GONE
                viewBinding.searchViewComposeView.visibility = View.GONE
                viewBinding.searchViewBackToTopButton.visibility = View.GONE

                // TODO: Disable search and camera icon

                if (this::shareResultMenu.isInitialized) {
                    shareResultMenu.setVisible(false)
                }

                if (this::copyUrlMenu.isInitialized) {
                    copyUrlMenu.setVisible(false)
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

                viewBinding.searchViewProgressbar.visibility = View.GONE
                resultsRecyclerView.visibility = View.VISIBLE
                viewBinding.searchViewComposeView.visibility = View.GONE
                viewBinding.searchViewBackToTopButton.visibility = View.VISIBLE

                // TODO: Enable search and camera icon

                if (bookResultViewState.keyword.isNotEmpty()) {
                    changeSearchBoxKeyword(bookResultViewState.keyword)
                }

                if (this::shareResultMenu.isInitialized) {
                    shareResultMenu.setVisible(true)
                }

                if (this::copyUrlMenu.isInitialized) {
                    copyUrlMenu.setVisible(true)
                }

                if (bookResultViewState.scrollPosition > 0) {
                    scrollToPosition(bookResultViewState.scrollPosition)
                }
            }

            BookResultViewState.PrepareBookResultError -> {
                viewBinding.searchViewProgressbar.visibility = View.GONE
                resultsRecyclerView.visibility = View.GONE
                viewBinding.searchViewComposeView.visibility = View.VISIBLE
                viewBinding.searchViewBackToTopButton.visibility = View.GONE

                // TODO: Enable search and camera icon

                if (this::shareResultMenu.isInitialized) {
                    shareResultMenu.setVisible(false)
                }

                if (this::copyUrlMenu.isInitialized) {
                    copyUrlMenu.setVisible(false)
                }
            }

            is BookResultViewState.ShowSearchRecordList -> {
                val itemCounts = bookResultViewState.itemCounts
                val heightPadding =
                    if (itemCounts < 5) {
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            (36f / itemCounts),
                            resources.displayMetrics
                        ).toInt()
                    } else {
                        0
                    }

                val adapterItemHeight =
                    resources.getDimensionPixelSize(R.dimen.search_records_item_height)

                toggleSearchRecordView(true, (adapterItemHeight + heightPadding) * itemCounts)
                bookSearchViewModel.searchRecordLiveData.observe(
                    viewLifecycleOwner
                ) { searchRecords ->
                    if (searchRecords != null) {
                        searchRecordsAdapter.addItems(viewLifecycleOwner.lifecycle, searchRecords)
                    }
                }
            }

            BookResultViewState.HideSearchRecordList -> {
                if (this::searchRecordsRootView.isInitialized) {
                    bookSearchViewModel.searchRecordLiveData.removeObservers(viewLifecycleOwner)
                    toggleSearchRecordView(false)
                    (searchRecordsRecyclerView.layoutManager as? LinearLayoutManager)?.scrollToPosition(0)
                }
            }

            is BookResultViewState.ShareCurrentPageSnapshot -> {
                val intent = Intent(Intent.ACTION_SEND)
                with(intent) {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "From " + getString(R.string.app_name))
                    putExtra(Intent.EXTRA_TEXT, bookResultViewState.url)
                }
                startActivity(
                    Intent.createChooser(
                        intent,
                        getString(R.string.menu_share_menu_appear)
                    )
                )
            }
        }
    }

    private fun updateScreen(screenState: ScreenState) {
        when (screenState) {
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
                    return
                }
                if (screenState.message != null) {
                    showToast(screenState.message)
                }
            }

            ScreenState.NoSharingContentAvailable -> {
                showToast(getString(R.string.no_shareable_content))
            }

            is ScreenState.ShowUserRankingDialog -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    playStoreReviewHelper.showReviewDialog(requireActivity(), screenState.reviewInfo)
                    // Reset counts and flags
                    hasUserSeenRankWindow = true
                    openResultCounts = 0
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
            if (!hasUserSeenRankWindow) {
                openResultCounts++
            }
        }
    }

    fun checkShouldAskUserRankApp() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (openResultCounts < POPUP_REVIEW_WINDOW_THRESHOLD) {
                return@launch
            }

            val hasUserSeenRankWindow =
                bookSearchViewModel.checkUserHasSeenRankWindow().also {
                    this@BookResultListFragment.hasUserSeenRankWindow = it
                }

            if (hasUserSeenRankWindow) {
                return@launch
            }

            if (BuildConfig.DEBUG) {
                Toast.makeText(requireContext(), "Rank Window Should Popup", Toast.LENGTH_SHORT).show()
            }

            val reviewInfo =
                runCatching {
                    playStoreReviewHelper.prepareReviewInfo()
                }.getOrNull()

            if (reviewInfo != null) {
                sendUserIntent(BookSearchUserIntent.AskUserRankApp(reviewInfo))
            }
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
            Toast.makeText(requireContext(), R.string.search_keyword_empty, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun hideVirtualKeyboard() {
        sendUserIntent(BookSearchUserIntent.ForceShowOrHideVirtualKeyboard(false))
    }

    private fun focusAndCleanBookSearchEditText() {
        sendUserIntent(BookSearchUserIntent.UpdateKeyword(TextFieldValue("")))
        focusBookSearchEditText()
    }

    private fun focusBookSearchEditText() {
        if (isAdded) {
            viewBinding.searchViewAppbar.setExpanded(true, true)
            sendUserIntent(BookSearchUserIntent.ForceFocusOrUnfocusKeywordTextInput(true))
            sendUserIntent(BookSearchUserIntent.ForceShowOrHideVirtualKeyboard(true))
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
        (resultsRecyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
            position,
            0
        )
    }

    private fun showToast(message: String) {
        if (isAdded) {
            message.showToastOn(requireContext())
        }
    }

    private fun openCameraPreview() {
        if (isAdded) {
            (requireActivity() as? BookSearchActivity)?.openCameraPreviewActivity()
        }
    }

    private fun searchBook() {
        hideVirtualKeyboard()
        sendUserIntent(BookSearchUserIntent.ForceFocusOrUnfocusKeywordTextInput(false))
        sendUserIntent(BookSearchUserIntent.SearchBook())
    }

    //region View.OnClickListener
    override fun onClick(view: View?) {
        when (view?.id) {
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
    //endregion

    private fun isCameraAvailable(): Boolean {
        if (isAdded) {
            return requireActivity().packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
                ?: false
        }
        return false
    }

    private fun ImageButton.setImageResource(
        context: Context,
        @DrawableRes drawableId: Int
    ) {
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
        changeSearchBoxKeyword(text)
        hideVirtualKeyboard()
        sendUserIntent(BookSearchUserIntent.SearchBook(text))
    }

    private fun changeSearchBoxKeyword(keyword: String) {
        sendUserIntent(BookSearchUserIntent.UpdateKeyword(TextFieldValue(keyword, selection = TextRange(keyword.length))))
        sendUserIntent(BookSearchUserIntent.ForceFocusOrUnfocusKeywordTextInput(false))
    }

    fun showSearchSnapshot(searchId: String) {
        hideVirtualKeyboard()
        sendUserIntent(BookSearchUserIntent.ShowSearchSnapshot(searchId))
    }

    fun backPressed(): Boolean {
        if (bookSearchViewModel.isTextInputFocused.value) {
            sendUserIntent(BookSearchUserIntent.ForceFocusOrUnfocusKeywordTextInput(false))
            return true
        }

        return false
    }

    fun toggleSearchRecordView(
        show: Boolean,
        targetHeight: Int = 0
    ) {
        if (!this::searchRecordsRootView.isInitialized) {
            return
        }

        if (show) {
            expandSearchRecordView(searchRecordsRootView, targetHeight)
        } else {
            collapseSearchRecordView(searchRecordsRootView)
        }
    }

    private fun expandSearchRecordView(
        view: FrameLayout,
        targetHeight: Int
    ) {
        val maxHeight = resources.getDimensionPixelSize(R.dimen.search_records_max_height)
        val height =
            if (targetHeight > maxHeight) {
                maxHeight
            } else {
                targetHeight
            }

        animateViewHeight(view, height)
    }

    private fun collapseSearchRecordView(view: FrameLayout) {
        animateViewHeight(view, 0)
    }

    private fun animateViewHeight(
        view: FrameLayout,
        targetHeight: Int
    ) {
        searchRecordAnimator?.takeIf { it.isRunning }?.run {
            removeAllListeners()
            removeAllUpdateListeners()
            cancel()
        }

        val animation = ValueAnimator.ofInt(view.measuredHeightAndState, targetHeight)
        animation.duration = 150
        animation.setInterpolator(DecelerateInterpolator())
        animation.addListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    val isBackgroundVisible = viewBinding.searchViewSearchRecordsBackground.visibility == View.VISIBLE
                    val isGoingToExpand = targetHeight > 0
                    if (isBackgroundVisible && !isGoingToExpand) {
                        viewBinding.searchViewSearchRecordsBackground.visibility = View.GONE
                        viewBinding.searchViewBackToTopButton.visibility = View.VISIBLE
                        return
                    }

                    if (!isBackgroundVisible && isGoingToExpand) {
                        viewBinding.searchViewSearchRecordsBackground.visibility = View.VISIBLE
                        viewBinding.searchViewBackToTopButton.visibility = View.GONE
                    }
                }
            }
        )
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

    override fun onSearchRecordCloseImageClicked(
        searchRecord: SearchRecord,
        position: Int
    ) {
        requireContext().let {
            val message =
                getString(R.string.alert_dialog_delete_search_record_message, searchRecord.text)
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

    companion object {
        private const val BUNDLE_RECYCLERVIEW_STATE = "BUNDLE_RECYCLERVIEW_STATE"
        private const val KEY_RECYCLERVIEW_POSITION = "KEY_RECYCLERVIEW_POSITION"
        private const val POPUP_REVIEW_WINDOW_THRESHOLD = 5
        private const val CLICK_MILLISECOND_THRESHOLD = 2000L

        fun newInstance(
            defaultKeyword: String?,
            defaultSnapshotSearchId: String?
        ) = BookResultListFragment().apply {
            this.defaultSearchKeyword = defaultKeyword.orEmpty()
            this.defaultSnapshotSearchId = defaultSnapshotSearchId.orEmpty()
        }

        const val TAG = "book-result-list-fragment"
    }
}
