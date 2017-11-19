package liou.rayyuan.ebooksearchtaiwan

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.main_result_text
import kotlinx.android.synthetic.main.activity_main.main_search_button
import kotlinx.android.synthetic.main.activity_main.main_search_edittext
import liou.rayyuan.ebooksearchtaiwan.model.ConnectionType.GET
import liou.rayyuan.ebooksearchtaiwan.model.EbookSearchService
import liou.rayyuan.ebooksearchtaiwan.model.NetworkConnectionListener
import liou.rayyuan.ebooksearchtaiwan.model.NetworkConnector

class MainActivity : AppCompatActivity(), NetworkConnectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_search_button.setOnClickListener({
            val keywords: String = main_search_edittext.text.toString()

            val ebookSearchService = EbookSearchService()
            val queryStrings: HashMap<String, String> = HashMap()
            queryStrings.put("q", keywords)

            val targetURL: String? = ebookSearchService.buildQueryStringURL("search", queryStrings)
            val connector = NetworkConnector(ebookSearchService, GET, this)
            connector.execute(targetURL)
        })
    }

    override fun onNetworkConnectionSucceed(result: String?) {
        main_result_text.text = result
    }
}
