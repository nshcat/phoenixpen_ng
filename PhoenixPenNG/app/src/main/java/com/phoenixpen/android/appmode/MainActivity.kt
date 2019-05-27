package com.phoenixpen.android.appmode

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.phoenixpen.android.R


class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*supportFragmentManager.addOnBackStackChangedListener {
            val backCount = supportFragmentManager.backStackEntryCount
            if(backCount == 0)
                finish()
        }*/

        val fm = supportFragmentManager
        /*for (i in 0 until fm.getBackStackEntryCount()) {
            fm.popBackStack()
        }*/

        //if(savedInstanceState == null)

        val frag = fm.findFragmentByTag(OpenGLFragment.TAG)


        if(frag != null)
        {
            supportFragmentManager.beginTransaction().add(R.id.main_container, OpenGLFragment(), OpenGLFragment.TAG)
                    .addToBackStack(null).commit()
        }
    }

}
