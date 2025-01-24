package com.inventive.ugosmartbuddy.smartbuddy.activities

import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.inventive.ugosmartbuddy.R
import com.inventive.ugosmartbuddy.databinding.ActivityMriactivityBinding
import com.inventive.ugosmartbuddy.smartbuddy.fragments.MRIReadingFragment
import com.inventive.ugosmartbuddy.smartbuddy.fragments.RelayConnectFragment

class MRIActivity : AppCompatActivity() {
    private lateinit var bindingMriActivity: ActivityMriactivityBinding
    private var isBilling = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingMriActivity = DataBindingUtil.setContentView(this, R.layout.activity_mriactivity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window: Window = window
            val decorView: View = window.decorView
            val wic = WindowInsetsControllerCompat(window, decorView)
            wic.isAppearanceLightStatusBars = true // true or false as desired.
        }

        val bundle = intent.extras

        if (bundle != null) {
            isBilling = bundle.getBoolean("isBilling")
        }

        if (isBilling) {
            com.inventive.ugosmartbuddy.common.Utility.replaceFragmentWithBundle(
                MRIReadingFragment(),
                supportFragmentManager,
                R.id.layout_fragment, bundle!!
            )
        } else {
            com.inventive.ugosmartbuddy.common.Utility.replaceFragmentWithBundle(
                RelayConnectFragment(),
                supportFragmentManager,
                R.id.layout_fragment, bundle!!
            )
        }

        /*com.inventive.ugosmartbuddy.common.Utility.replaceFragmentWithBundle(
            FirmwareUpdateFragment(),
            supportFragmentManager,
            R.id.layout_fragment, bundle!!
        )*/

    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return try {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val v = currentFocus
                if (v is EditText) {
                    val outRect = Rect()
                    v.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        v.clearFocus()
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                    }
                }
            }
            super.dispatchTouchEvent(event)
        } catch (e: Exception) {
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }
}