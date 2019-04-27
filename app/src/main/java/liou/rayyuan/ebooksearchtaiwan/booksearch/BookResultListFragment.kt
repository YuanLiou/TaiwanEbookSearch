
package liou.rayyuan.ebooksearchtaiwan.booksearch

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_search_list.*
import liou.rayyuan.ebooksearchtaiwan.BaseFragment
import liou.rayyuan.ebooksearchtaiwan.BuildConfig
import liou.rayyuan.ebooksearchtaiwan.R
import liou.rayyuan.ebooksearchtaiwan.model.EventTracker
import liou.rayyuan.ebooksearchtaiwan.model.RemoteConfigManager
import liou.rayyuan.ebooksearchtaiwan.model.entity.Book
import liou.rayyuan.ebooksearchtaiwan.utils.FragmentArgumentsDelegate
import liou.rayyuan.ebooksearchtaiwan.utils.showToastOn
import liou.rayyuan.ebooksearchtaiwan.view.BookResultClickHandler
import liou.rayyuan.ebooksearchtaiwan.view.FullBookStoreResultAdapter
import org.koin.android.viewmodel.ext.android.viewModel

class BookResultListFragment : BaseFragment(), View.OnClickListener, BookResultClickHandler {

    companion object {
        fun newInstance(defaultKeyword: String?) = BookResultListFragment().apply {
            this.defaultSearchKeyword = defaultKeyword ?: ""
        }
        const val TAG = "book-result-list-fragment"
    }

    private val BUNDLE_RECYCLERVIEW_STATE: String = "BUNDLE_RECYCLERVIEW_STATE"
    private val KEY_RECYCLERVIEW_POSITION: String = "KEY_RECYCLERVIEW_POSITION"
    private val bookSearchViewModel: BookSearchViewModel by viewModel()

    private var defaultSearchKeyword: String by FragmentArgumentsDelegate()
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
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(search_view_toolbar)
        bindViews(view)
        init()

        fullBookStoreResultsAdapter = FullBookStoreResultAdapter(this, eventTracker)
        resultsRecyclerView.adapter = fullBookStoreResultsAdapter
        bookSearchViewModel.setRecyclerViewAdapter(fullBookStoreResultsAdapter)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val recyclerViewState = savedInstanceState.getParcelable(BUNDLE_RECYCLERVIEW_STATE) as Parcelable
            resultsRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)

            val recyclerViewPosition = savedInstanceState.getInt(KEY_RECYCLERVIEW_POSITION, 0)
            (resultsRecyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(recyclerViewPosition, 0)
            bookSearchViewModel.lastScrollPosition = recyclerViewPosition
            Log.i("BookResultListFragment", "restore recyclerView Position = $recyclerViewPosition")
        }
        bookSearchViewModel.screenViewState.observe(viewLifecycleOwner, Observer<ScreenState> { state -> updateScreen(state) })
        bookSearchViewModel.listViewState.observe(viewLifecycleOwner, Observer<ListViewState> { state -> setMainResultView(state) })
        bookSearchViewModel.ready()
        setupUI()

        if (!TextUtils.isEmpty(defaultSearchKeyword)) {
            searchWithText(defaultSearchKeyword)
            defaultSearchKeyword = ""
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
                hideVirtualKeyboard()
                val keyword: String = searchEditText.text.toString()
                bookSearchViewModel.searchBook(keyword)
            }
            false
        }

        hintText.setOnClickListener(this)
        hintText.compoundDrawables
                .filterNotNull()
                .forEach { DrawableCompat.setTint(it, ContextCompat.getColor(context!!, R.color.gray)) }

        val linearLayoutManager = resultsRecyclerView.layoutManager as LinearLayoutManager
        linearLayoutManager.initialPrefetchItemCount = 6

        loadAds()
        initScrollToTopButton()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_page, menu)
        shareResultMenu = menu.findItem(R.id.search_page_menu_action_share)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            return when (it.itemId) {
                R.id.search_page_menu_action_setting -> {
                    if (isAdded && activity is BookSearchActivity) {
                        (activity as BookSearchActivity).openPreferenceActivity()
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
        } ?: return super.onOptionsItemSelected(item)
    }

    private fun loadAds() {
        val adView: AdView = adViewLayout.findViewById(R.id.admob_view_header_adview)
        val adRequestBuilder = AdRequest.Builder()
        if (BuildConfig.DEBUG) {
            adRequestBuilder.addTestDevice(BuildConfig.ADMOB_TEST_DEVICE_ID)
        }
        val adRequest = adRequestBuilder.build()
        adView.loadAd(adRequest)
    }

    private fun initScrollToTopButton() {
        backToTopButton.setOnClickListener(this)

        if (!isAdded) {
            return
        }

        if (remoteConfigManager.firebaseRemoteConfig.getBoolean(RemoteConfigManager.COLOR_BACK_TO_TOP_BUTTON_KEY)) {
            backToTopButton.setBackgroundResource(R.drawable.material_rounded_button_green)
        } else {
            val typedValue = TypedValue()
            activity?.theme?.resolveAttribute(R.attr.backToTopButtonDrawable, typedValue, true)
            val backToButtonButtonResourceId = typedValue.resourceId
            backToTopButton.setBackgroundResource(backToButtonButtonResourceId)
        }

        resultsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (resultsRecyclerView.canScrollVertically(-1)) {
                    backToTopButton.setImageResource(context!!, R.drawable.ic_keyboard_arrow_up_24dp)
                } else {

                    if (remoteConfigManager.firebaseRemoteConfig.getBoolean(RemoteConfigManager.KEYBOARD_BACK_TO_TOP_ICON_KEY)) {
                        backToTopButton.setImageResource(context!!, R.drawable.ic_keyboard_white_24dp)
                    } else {
                        backToTopButton.setImageResource(context!!, R.drawable.ic_search_white_24dp)
                    }
                }
            }
        })
    }

    private fun setupUI() {
        val hintWithAppVersion = hintText.text.toString() + "\n" + resources.getString(R.string.app_version, BuildConfig.VERSION_NAME)
        hintText.text = hintWithAppVersion
    }

    private fun setMainResultView(listViewState: ListViewState?) {
        when (listViewState) {
            is ListViewState.Prepare -> {
                progressBar.visibility = View.VISIBLE
                resultsRecyclerView.visibility = View.GONE
                hintText.visibility = View.GONE
                backToTopButton.visibility = View.GONE
                adViewLayout.visibility = View.VISIBLE

                if (this::shareResultMenu.isInitialized) {
                    shareResultMenu.setVisible(false)
                }

                if (listViewState.scrollToTop) {
                    scrollToTop()
                }
            }
            is ListViewState.Ready -> {
                if (listViewState.adapterItems.isNotEmpty()) {
                    if (this::fullBookStoreResultsAdapter.isInitialized) {
                        fullBookStoreResultsAdapter.addResult(listViewState.adapterItems)
                    }
                }

                progressBar.visibility = View.GONE
                resultsRecyclerView.visibility = View.VISIBLE
                hintText.visibility = View.GONE
                backToTopButton.visibility = View.VISIBLE
                adViewLayout.visibility = View.GONE

                if (this::shareResultMenu.isInitialized) {
                    shareResultMenu.setVisible(true)
                }

                if (listViewState.scrollPosition > 0) {
                    scrollToPosition(listViewState.scrollPosition)
                }
            }
            ListViewState.Error -> {
                progressBar.visibility = View.GONE
                resultsRecyclerView.visibility = View.GONE
                hintText.visibility = View.VISIBLE
                backToTopButton.visibility = View.GONE
                adViewLayout.visibility = View.GONE

                if (this::shareResultMenu.isInitialized) {
                    shareResultMenu.setVisible(false)
                }
            }
            else -> {}
        }
    }

    private fun updateScreen(screenState: ScreenState?) {
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
            else -> {}
        }
    }

    private fun scrollToTop() {
        resultsRecyclerView.scrollToPosition(0)
    }

    private fun openBook(book: Book) {
        if (isAdded && activity is BookSearchActivity) {
            (activity as BookSearchActivity).openBookLink(book)
        }
    }

    private fun showInternetRequestDialog() {
        if (isAdded) {
            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(activity!!)
            dialogBuilder.setTitle(R.string.network_alert_dialog_title)
            dialogBuilder.setMessage(R.string.network_alert_message)
            dialogBuilder.setPositiveButton(R.string.dialog_ok) { _: DialogInterface, _: Int -> }
            dialogBuilder.create().show()
        }
    }

    private fun showInternetConnectionTimeout() {
        if (isAdded) {
            getString(R.string.state_timeout).showToastOn(context!!)
        }
    }

    private fun showKeywordIsEmpty() {
        if (isAdded) {
            Toast.makeText(context!!, R.string.search_keyword_empty, Toast.LENGTH_LONG).show()
        }
    }

    private fun hideVirtualKeyboard() {
        if (isAdded) {
            val inputManager: InputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }
    }

    private fun focusBookSearchEditText() {
        if (isAdded) {
            appbar.setExpanded(true, true)
            searchEditText.requestFocus()
            val inputManager: InputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(searchEditText, 0)
        }
    }

    private fun showEasterEgg01() {
        if (isAdded) {
            Toast.makeText(context!!, R.string.easter_egg_01, Toast.LENGTH_LONG).show()
        }
    }

    private fun showNetworkErrorMessage() {
        if (isAdded) {
            getString(R.string.network_error_message).showToastOn(context!!)
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
            message.showToastOn(context!!)
        }
    }

    //region View.OnClickListener
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.search_view_search_icon -> {
                hideVirtualKeyboard()
                val keyword: String = searchEditText.text.toString()
                bookSearchViewModel.searchBook(keyword)
            }
            R.id.search_view_hint -> {
                hintPressed()
            }
            R.id.search_view_camera_icon -> {
                if (isAdded && activity is BookSearchActivity) {
                    (activity as BookSearchActivity).openCameraPreviewActivity()
                }
            }
            R.id.search_view_back_to_top_button -> {
                val canListScrollVertically = resultsRecyclerView.canScrollVertically(-1)
                backToTop(canListScrollVertically)
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
        bookSearchViewModel.hintPressed()
        focusBookSearchEditText()
    }
    //endregion

    private fun isCameraAvailable(): Boolean {
        if (isAdded) {
            return activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA) ?: false
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

    internal fun searchWithText(text: String) {
        searchEditText.setText(text)
        searchEditText.setSelection(text.length)
        hideVirtualKeyboard()
        bookSearchViewModel.searchBook(text)
        bookSearchViewModel.logISBNScanningSucceed()
    }

    //region BookResultClickHandler
    override fun onBookCardClicked(book: Book) {
        openBook(book)
    }
    //endregion
}