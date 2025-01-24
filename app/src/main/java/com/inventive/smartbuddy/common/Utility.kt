package com.inventive.smartbuddy.common

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar


object Utility {

    fun addFragment(fragment: Fragment?, fragmentManager: FragmentManager, resId: Int) {
        try {
            if (fragment != null) {
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.add(resId, fragment).commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @JvmStatic
    fun playBeep(context: Context, beepSoundFile: String?) {
        var m = MediaPlayer()
        try {
            if (m.isPlaying) {
                m.stop()
                m.release()
                m = MediaPlayer()
            }
            val descriptor = context.assets.openFd(beepSoundFile ?: "beep.mp3")
            m.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            descriptor.close()
            m.prepare()
            m.setVolume(1f, 1f)
            m.start()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }




    fun isNullOrEmpty(var0: String?): Boolean {
        return var0 == null || var0.trim { it <= ' ' }.isEmpty() || var0.trim { it <= ' ' }
            .equals("null", ignoreCase = true)
    }

    @JvmStatic
    fun replaceFragment(fragment: Fragment?, fragmentManager: FragmentManager, resId: Int) {
        if (fragment != null) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            //   fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            fragmentTransaction.replace(resId, fragment)
            // fragmentTransaction.commit();
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commitAllowingStateLoss()
        }
    }

    @JvmStatic
    fun snackBar(view: View?, msg: String?, duration: Int?, color: Int) {
        val sb = Snackbar.make(view!!, msg!!, duration!!)
        val sbView = sb.view
        sbView.setBackgroundColor(color)
        sb.show()
    }

    @JvmStatic
    fun replaceFragmentWithBundle(
        fragment: Fragment?,
        fragmentManager: FragmentManager,
        resId: Int,
        bundle: Bundle
    ) {
        if (fragment != null) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragment.arguments = bundle
            fragmentTransaction.replace(resId, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commitAllowingStateLoss()
        }
    }

}


