package liou.rayyuan.ebooksearchtaiwan

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.main_search_button
import kotlinx.android.synthetic.main.activity_main.main_search_edittext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_search_button.setOnClickListener({
            val keywords: String = main_search_edittext.text.toString()
            Toast.makeText(this, "You typed = " + keywords.trim(), Toast.LENGTH_LONG).show()
        })
    }
}
