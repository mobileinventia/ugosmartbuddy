package com.inventive.smartbuddy.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inventive.smartbuddy.R

class USBAttachedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usbattached)
        val intent = Intent(this@USBAttachedActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}