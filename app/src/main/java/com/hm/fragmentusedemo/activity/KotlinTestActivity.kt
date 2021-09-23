package com.hm.fragmentusedemo.activity

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hm.fragmentusedemo.R

inline fun <reified T : Activity> Context.startActivity() {

    val intent = Intent(this, T::class.java)
    startActivity(intent)

}

class KotlinTestActivity : AppCompatActivity() {

    fun launch(context: Context) {
        val starter = Intent(context, KotlinTestActivity::class.java)
        context.startActivity(starter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_test)

        startActivity<ViewPagerWithFragmentActivity>()
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
    }


}
