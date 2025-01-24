package com.inventive.smartbuddy.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.inventive.smartbuddy.R
import com.inventive.smartbuddy.databinding.ActivityMainBinding

import com.inventive.ugosmartbuddy.smartbuddy.activities.MRIActivity
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 200) {
            val data = result.data
            val filePath = data?.getStringExtra("filePath")

            val numFilesFs = File(filePath).listFiles()
            Log.d("numFilesFs", numFilesFs?.get(0)?.absolutePath ?: "No files found")
        }
        else
        {
            val data = result.data
            val action = data?.getStringExtra("action")
            val fieldMeterNo = data?.getStringExtra("fieldMeterNo")
            val remark = data?.getStringExtra("remark")
            val visitDate = data?.getStringExtra("visitDate")
            val visitDate1 = data?.getStringExtra("visitDate")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //  val numFilesFs = File(filesDir.absolutePath + "/ugo_mri").listFiles()

        // FOR LNT Meter
        binding.btnStartApp.setOnClickListener {
            val intent = Intent(this, MRIActivity::class.java)
            val mBundle = Bundle()
            mBundle.putString("ACCOUNT_NO", "1085845444")
            mBundle.putString("MeterMakeName", "L&T")
            mBundle.putString("MeterMakeId", "30")
            mBundle.putString("protocol", "DLMS")
            mBundle.putString("connectionMedium", "PROBE")
            mBundle.putBoolean("isBilling", false)
            mBundle.putBoolean("isLoadsurvey", false)
            mBundle.putBoolean("isTamper", false)
            mBundle.putString("Load_Days", "60")
            mBundle.putInt("phaseTypeId", 1)
            mBundle.putString("phaseTypeName", "Single Phase")
            mBundle.putString("PASSWORD", "0TMP4GSM01035820")
            mBundle.putInt("CLIENT_ADDRESS", 48)
            mBundle.putString("BLOCK_CIPHER_KEY", "1234567890123456")
            mBundle.putString("AUTH_KEY", "1234567890123456")
            mBundle.putString("SYSTEM_TITTLE", "LTCLIENT")
            mBundle.putString("MANUFECTURER", "LNT")
            mBundle.putString("consumerName", "Shivam Singhal")
            mBundle.putString("address", "B-14 , Inventia Technology, Sector-67,Noida")
            mBundle.putBoolean("reconnect_flag", true)
            mBundle.putString("meterNo", "T0029574")
            intent.putExtras(mBundle)
            resultLauncher.launch(intent)
        }

        // FOR AEW METERS
        /* binding.dont.setOnClickListener({
             val intent = Intent(this, MRIActivity::class.java)
             val mBundle = Bundle()
             mBundle.putString("MeterMakeName", "Allied Smart")
             mBundle.putString("MeterMakeId", "88")
             mBundle.putString("ACCOUNT_NO", "1085845444")
             mBundle.putString("protocol", "DLMS")
             mBundle.putString("connectionMedium", "PROBE")
             mBundle.putBoolean("isBilling", false)
             mBundle.putBoolean("isLoadsurvey", false)
             mBundle.putBoolean("isTamper", true)
             mBundle.putString("Load_Days", "60")
             mBundle.putInt("phaseTypeId", 1)
             mBundle.putString("phaseTypeName", "Single Phase")
             mBundle.putString("PASSWORD", "ALLIED_SMARTRV21")
             mBundle.putInt("CLIENT_ADDRESS", 48)
             mBundle.putString("BLOCK_CIPHER_KEY", "AEW_UnicastAssc1")
             mBundle.putString("AUTH_KEY", "AEW_UnicastAssc1")
             mBundle.putString("SYSTEM_TITTLE", "ALL00000")
             mBundle.putString("MANUFECTURER", "Allied")
             mBundle.putString("consumerName", "Shivam Singhal")
             mBundle.putString("address", "B-14 , Inventia Technology, Sector-67,Noida")
             mBundle.putBoolean("reconnect_flag", true)
             mBundle.putString("meterNo", "AW8000499")
             intent.putExtras(mBundle)
             resultLauncher.launch(intent)
         })*/
    }
}