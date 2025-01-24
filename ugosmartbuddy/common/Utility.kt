package com.inventive.ugosmartbuddy.common

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.inventive.ugosmartbuddy.R
import com.inventive.ugosmartbuddy.mrilib.models.OBIS
import com.inventive.ugosmartbuddy.smartbuddy.models.OBISMaster
import com.inventive.ugosmartbuddy.smartbuddy.models.Readings
import com.inventive.ugosmartbuddy.ugoapp.UgoApplication
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale


object Utility {
    private const val MY_PREFS_NAME = "MySdkPrefsFile"

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

    @JvmStatic
    fun convertOBISModel(appModel: ArrayList<OBISMaster.OBIS>): ArrayList<OBIS> {
        val libModel = ArrayList<OBIS>()
        if (appModel.size > 0) {
            for (app in appModel) {
                val lib = OBIS()
                lib.obiS_SCALER = app.OBIS_SCALER
                lib.obiS_VALUE = app.OBIS_VALUE
                lib.objecT_TYPE = app.OBJECT_TYPE
                lib.profileR_CODE = app.PROFILER_CODE.toString()
                lib.profileR_GROUP = app.PROFILER_GROUP
                libModel.add(lib)
            }
        }
        return libModel
    }

    @JvmStatic
    fun saveBooleanPreference(key: String?, value: Boolean) {
        val editor: SharedPreferences.Editor = UgoApplication.mInstance.getSharedPreferences(
            MY_PREFS_NAME, Context.MODE_PRIVATE
        )!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    @JvmStatic
    fun getBooleanPreference(key: String?): Boolean {
        return UgoApplication.mInstance.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
            ?.getBoolean(key, false)!!
    }

    @JvmStatic
    fun saveStringPreference(key: String?, value: String?) {
        val editor: SharedPreferences.Editor = UgoApplication.mInstance.getSharedPreferences(
            MY_PREFS_NAME, Context.MODE_PRIVATE
        )!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    @JvmStatic
    fun getStringPreference(key: String?): String {
        return UgoApplication.mInstance.getSharedPreferences(
            MY_PREFS_NAME, Context.MODE_PRIVATE)
            ?.getString(key, null)!!
    }

    @JvmStatic
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

    @JvmStatic
    fun getJsonMRI(readings1: Readings?, accountNo :String
    ): JSONObject {
        val `object` = JSONObject()
        try {
            val object1 = JSONObject()
            if (readings1 != null) {
                if (!isNullOrEmpty(readings1.GENERAL_READING)) object1.put(
                    "General", JSONArray(readings1.GENERAL_READING)
                )
                if (!isNullOrEmpty(readings1.INSTANTENOUS_READING)) object1.put(
                    "Instantenous", JSONArray(readings1.INSTANTENOUS_READING)
                )
                if (!isNullOrEmpty(readings1.BILLING_READING)) object1.put(
                    "Billing", JSONArray(readings1.BILLING_READING)
                )
                if (!isNullOrEmpty(readings1.TAMPER_READING)) object1.put(
                    "Tamper", JSONArray(readings1.TAMPER_READING)
                )
                if (!isNullOrEmpty(readings1.LOADSURVEY_READING)) object1.put(
                    "LoadSurvey", JSONArray(readings1.LOADSURVEY_READING)
                )
                if (!isNullOrEmpty(readings1.LOADSURVEY_SCALER)) object1.put(
                    "LoadSurvey_S", JSONArray(readings1.LOADSURVEY_SCALER)
                )
                `object`.put("METER_DATA", object1)
            }
            `object`.put("ACCOUNT_NO", accountNo)
        } catch (ex: java.lang.Exception) {
            ex.message
        }
        return `object`
    }

    @JvmStatic
    fun saveMriFileWithoutExtension(fileName: String,meterNo: String, content: String): String {
        var filePath =""
        var currentYear =""
        var currentMonthName =""
        var currentDay =""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For API 26 and above
            val currentDate = LocalDate.now()
            currentYear = currentDate.year.toString()
            currentMonthName = currentDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) // "Jan", "Feb", etc.
            currentDay = currentDate.dayOfMonth.toString()
        } else {
            // For API below 26
            val calendar = Calendar.getInstance()
            currentYear = calendar.get(Calendar.YEAR).toString()
            currentDay = calendar.get(Calendar.DAY_OF_MONTH).toString()
            val monthIndex = calendar.get(Calendar.MONTH) // Month is 0-based
            currentMonthName = DateFormatSymbols().shortMonths[monthIndex] // "Jan", "Feb", etc.
        }

        val outputStream: FileOutputStream
        val myDir: File = com.inventive.ugosmartbuddy.ugoapp.UgoApplication.mInstance.applicationContext.filesDir
        val documentsFolder = File(myDir, "ugo_mri")
        if (!documentsFolder.exists()) {
            documentsFolder.mkdirs()
        }

        // Construct the folder hierarchy
        val yearFolder = File(documentsFolder, currentYear)
        val monthFolder = File(yearFolder, currentMonthName)
        val dayFolder = File(monthFolder, currentDay)
        val meterNoFolder = File(dayFolder, meterNo)
        try {
            if (!meterNoFolder.exists()) {
                meterNoFolder.mkdirs()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            // Create the file without an extension
            val file = File(meterNoFolder.path + "/" + fileName)
            outputStream = FileOutputStream(file)
            outputStream.write(content.toByteArray())
            outputStream.flush()
            outputStream.close()
            filePath = file.absolutePath
            println("File saved successfully at: ${file.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error saving the file: ${e.message}")
        }

        return meterNoFolder.path
    }

    fun getCurrenTime(): String? {
        return timeToString(Date())
    }

    fun timeToString(date: Date?): String? {
        var date1: String? = null
        try {
            val formatter = SimpleDateFormat("hh:mm aaa", Locale.ENGLISH)
            date1 = formatter.format(date)
        } catch (pe: java.lang.Exception) {
            pe.printStackTrace()
        }
        return date1
    }

    fun showDialog(context: Context?, title: String?, message: String?) {
        val builder = AlertDialog.Builder(
            context!!
        )
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_simple, null)
        val txtdialogMsg = dialogView.findViewById<View>(R.id.txtDialogMsg) as TextView
        txtdialogMsg.text = message
        val txtDialogTitle = dialogView.findViewById<View>(R.id.txtDialogTitle) as TextView
        txtDialogTitle.text = title
        // FontHelper.applyFont(FontHelper.FontType.OS_REGULAR, dialogView.getRootView());
        builder.setView(dialogView)
        val btnDialogOk = dialogView.findViewById<View>(R.id.btnDialogOk) as Button
        val dialog = builder.create()
        btnDialogOk.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    val currentDateTime: String?
        get() = dateStringWithTime(Date())


    fun dateStringWithTime(date: Date?): String? {
        var date1: String? = null
        try {
            val formatter = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH)
            date1 = formatter.format(date)
        } catch (pe: Exception) {
            pe.printStackTrace()
        }
        return date1
    }

    @JvmStatic
    fun progressDialog(context: Context, message: String?): Dialog {
        val dialog = Dialog(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        dialog.setContentView(inflate)
        dialog.setCancelable(false)
        val progressText = inflate.findViewById<TextView>(R.id.progressText)
        progressText.text = message
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        return dialog
    }

    @JvmStatic
    val currentDateDay: String?
        get() = dayToString(Date())


    fun dayToString(date: Date?): String? {
        var date1: String? = null
        try {
            val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            date1 = formatter.format(date)
        } catch (pe: Exception) {
            pe.printStackTrace()
        }
        return date1
    }

    @JvmStatic
    fun isDownloadDateAvailable(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.contains(Constants.DOWNLOADING_DATE)
    }

 /*   @JvmStatic
    fun isFutureDate(expiryDate: String): Boolean {
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
        val targetDate = dateFormat.parse(expiryDate) ?: return false
        val currentDate = Date()

        expDate = stringToDate(targetDate)

        // Compare the dates
        return targetDate.after(currentDate)
    }*/

    @JvmStatic
    fun stringToDate(Date: String?): Date? {
        var date: Date? = null
        try {
            val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            date = formatter.parse(Date)
        } catch (pe: java.lang.Exception) {
            pe.printStackTrace()
        }
        return date
    }


}


