package com.hm.fragmentusedemo.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hm.fragmentusedemo.R
import com.hm.fragmentusedemo.fragment.LifeCycleFragment.Companion.newInstance

class FragmentLifecycleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_lifecycle)
        val lifeCycleFragment = newInstance("", "")
        supportFragmentManager.beginTransaction().add(R.id.ll_root, lifeCycleFragment).commit()
    }
}