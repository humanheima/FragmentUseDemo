package com.hm.fragmentusedemo.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hm.fragmentusedemo.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LifeCycleFragment.newInstance] factory method to
 * create an instance of this fragment.
 * 声明周期方法
 *
 * I/LifeCycleFragment: onAttach:
 * I/LifeCycleFragment: onCreateView:
 * I/LifeCycleFragment: onViewCreated:
 * I/LifeCycleFragment: onActivityCreated:
 * I/LifeCycleFragment: onStart:
 * I/LifeCycleFragment: onResume:
 * I/LifeCycleFragment: onPause:
 * I/LifeCycleFragment: onStop:
 * I/LifeCycleFragment: onDestroyView:
 * I/LifeCycleFragment: onDestroy:
 * I/LifeCycleFragment: onDetach:
 *
 *
 * 1. 在、ViewPager中，第一次创建的时候，会调用 setUserVisibleHint。
 * 2. 在ViewPager中左右切换的时候，会调用 setUserVisibleHint， onResume  不会调用。

 *
 *
 */
class LifeCycleFragment : Fragment() {

    private val TAG: String = "LifeCycleFragment"

    private var param1: String? = null
    private var param2: String? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.i(TAG, "onAttach: ")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView: ")
        return inflater.inflate(R.layout.fragment_life_cycle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: ")

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i(TAG, "onActivityCreated: ")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: ")
    }


    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop: ")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "onDestroyView: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i(TAG, "onDetach: ")
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LifeCycleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                LifeCycleFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}