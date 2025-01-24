package com.inventive.ugosmartbuddy.smartbuddy.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel.obtain
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.inventive.ugosmartbuddy.ApiCall.Api
import com.inventive.ugosmartbuddy.R
import com.inventive.ugosmartbuddy.common.Constants
import com.inventive.ugosmartbuddy.common.Utility
import com.inventive.ugosmartbuddy.common.Utility.currentDateDay
import com.inventive.ugosmartbuddy.common.Utility.getStringPreference
import com.inventive.ugosmartbuddy.common.Utility.isDownloadDateAvailable
import com.inventive.ugosmartbuddy.common.Utility.progressDialog
import com.inventive.ugosmartbuddy.common.Utility.saveBooleanPreference
import com.inventive.ugosmartbuddy.common.Utility.saveStringPreference
import com.inventive.ugosmartbuddy.common.Utility.snackBar
import com.inventive.ugosmartbuddy.common.Utility.stringToDate
import com.inventive.ugosmartbuddy.databinding.FragmentRelayConnectBinding
import com.inventive.ugosmartbuddy.mrilib.common.Utils
import com.inventive.ugosmartbuddy.mrilib.mri.DisconnectionControl
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial
import com.inventive.ugosmartbuddy.smartbuddy.models.SaveRelayDataModel
import com.inventive.ugosmartbuddy.smartbuddy.models.ValidateModel
import com.inventive.ugosmartbuddy.ugoapp.UgoApplication.Companion.mInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RelayConnectFragment : Fragment(), View.OnClickListener {

    private var mActivity: FragmentActivity? = null
    private lateinit var fragmentRelayConnectBinding: FragmentRelayConnectBinding
    private var bundle: Bundle? = null
    private val MAX_BUNDLE_SIZE = 300
    private var accountId = ""
    private var password = ""
    private var clientAddress = 0
    private var blockCipherkey = ""
    private var authKey = ""
    private var systemTittle = ""
    private var manufacturer = ""
    private var phase = ""
    private var meterNo = ""
    private var name = ""
    private var address = ""
    private var recFlag = false
    private var saveRelayDataModel: SaveRelayDataModel? = null
    private var serial: GXSerial? = null
    private var deviceId: String? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val bundleSize = getBundleSize(outState)
        if (bundleSize > MAX_BUNDLE_SIZE * 1024) {
            outState.clear()
        }
    }

    private fun getBundleSize(bundle: Bundle): Long {
        val dataSize: Long
        val obtain = obtain()
        dataSize = try {
            obtain.writeBundle(bundle)
            obtain.dataSize().toLong()
        } finally {
            obtain.recycle()
        }
        return dataSize
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        fragmentRelayConnectBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_relay_connect, container, false)
        return fragmentRelayConnectBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentRelayConnectBinding.btnReconnect.setOnClickListener(this)
        fragmentRelayConnectBinding.btnProceedSmart.setOnClickListener(this)

        serial = Utils.findPorts(mActivity)
        updateUI()
    }

    private fun updateUI() {
        bundle = arguments
        if (bundle != null) {
            accountId = bundle!!.getString("ACCOUNT_NO")!!
            password = bundle!!.getString("PASSWORD")!!
            password = bundle!!.getString("PASSWORD")!!
            clientAddress = bundle!!.getInt("CLIENT_ADDRESS")
            blockCipherkey = bundle!!.getString("BLOCK_CIPHER_KEY")!!
            authKey = bundle!!.getString("AUTH_KEY")!!
            systemTittle = bundle!!.getString("SYSTEM_TITTLE")!!
            manufacturer = bundle!!.getString("MANUFECTURER")!!
            phase = bundle!!.getString("phaseTypeName")!!
            meterNo = bundle!!.getString("meterNo")!!
            name = bundle!!.getString("consumerName")!!
            address = bundle!!.getString("address")!!
            recFlag = bundle!!.getBoolean("reconnect_flag")

            fragmentRelayConnectBinding.txtMeterNo.text = meterNo
            if (recFlag) {
                fragmentRelayConnectBinding.txtRelayOperation.text = "Reconnection"
            } else {
                fragmentRelayConnectBinding.txtRelayOperation.text = "Disconnection"
            }
            fragmentRelayConnectBinding.txtAccountNo.text = accountId
            fragmentRelayConnectBinding.txtReachedTime.text = Utility.currentDateTime
            fragmentRelayConnectBinding.txtConsumerName.text = name
            fragmentRelayConnectBinding.txtAddress.text = address
            if (recFlag) {
                fragmentRelayConnectBinding.btnReconnect.text = "Reconnect"
            } else {
                fragmentRelayConnectBinding.btnReconnect.text = "Disconnection"
            }
        }

        saveBooleanPreference(Constants.VALID_SDK, false)
        if (!isDownloadDateAvailable(mActivity!!)) {
            getValidateSdkData()
        } else {
            if (getStringPreference(Constants.DOWNLOADING_DATE) == currentDateDay) {
                if (stringToDate(getStringPreference(Constants.EXPIRY_DATE))!!.compareTo(stringToDate(currentDateDay)) < 0) {
                    unAuthorizedAccess(mActivity!!)
                }
            } else {
                getValidateSdkData()
            }
        }
    }

    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.btnReconnect -> {
                if (serial == null) {
                    Utility.showDialog(
                        mActivity,
                        "Cable Issue",
                        "Cable is not connected with mobile"
                    )
                } else {
                    if (validateRelayData()) {
                        reconnectMeter(recFlag)
                    }
                }
            }

            R.id.btnProceedSmart -> {
                if (validateRelayData()) {
                    saveRelayData()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    private fun validateRelayData(): Boolean {

        if (Utility.isNullOrEmpty(fragmentRelayConnectBinding.txtRemark.text.toString())) {
            fragmentRelayConnectBinding.txtRemark.requestFocus()
            Utility.snackBar(
                fragmentRelayConnectBinding.rootLayout,
                requireActivity().resources.getString(R.string.remarks),
                1200,
                ContextCompat.getColor(requireContext(), R.color.warning)
            )
            return false
        }

        val disconnectionControl =
            DisconnectionControl()
        try {
            val result = disconnectionControl.verifyMeterNo(
                serial,
                Constants.BAUDRATE,
                Constants.DATA_BIT,
                Constants.STOP_BIT,
                Constants.PARITY_BIT,
                Constants.AUTHENTICATION_TYPE,
                password,
                Constants.CLIENT_ADDRESS,
                Constants.SERVER_ADDRESS,
                Constants.INTERFACETYPE,
                Constants.LOGICAL_REFERENCE_NAME,
                authKey,
                blockCipherkey,
                systemTittle,
                Constants.AUTHENTICATION_TYPE,
                manufacturer,
                phase,
                mActivity
            )

            if (result == null) {
                Utility.snackBar(
                    fragmentRelayConnectBinding.rootLayout, "Meter Number Not Fetched",
                    1200, resources.getColor(R.color.warning)
                )
                return false
            }

            // For Allied
            /*if (result.size > 2 && result[2] != null &&  !result[2].contains(manufacturer,true) ) {
                Utility.snackBar(
                    fragmentRelayConnectBinding.rootLayout,
                    resources.getString(R.string.meter_make_mismatch),
                    2400,
                    resources.getColor(R.color.warning)
                )
                return false
            }*/


            // For LNT
            if (result.size > 2 && result[2] != null &&  !result[2].contains("Schneider",true) ) {
                Utility.snackBar(
                    fragmentRelayConnectBinding.rootLayout,
                    resources.getString(R.string.meter_make_mismatch),
                    2400,
                    resources.getColor(R.color.warning)
                )
                return false
            }

            if (!Utility.isNullOrEmpty(
                    fragmentRelayConnectBinding.txtMeterNo.getText().toString()
                ) && result[0] != null && fragmentRelayConnectBinding.txtMeterNo.text.toString() == result[0]
            ) {
                deviceId = result[1]
                return true
            } else {
                Utility.snackBar(
                    fragmentRelayConnectBinding.rootLayout,
                    resources.getString(R.string.meter_no_mismatch),
                    2400,
                    resources.getColor(R.color.warning)
                )
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }



        return true

    }

    private fun reconnectMeter(reconnectionFlag: Boolean) {
        val progressDialog = ProgressDialog(mActivity, R.style.MyAlertDialogStyle)
        progressDialog.setMessage("Reconnecting Meter...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        try {

            val disconnectionControl = DisconnectionControl()
            try {
                disconnectionControl.readData(
                    fragmentRelayConnectBinding.txtMeterNo.getText().toString(),
                    serial,
                    reconnectionFlag,
                    Constants.BAUDRATE,
                    Constants.DATA_BIT,
                    Constants.STOP_BIT,
                    Constants.PARITY_BIT,
                    Constants.AUTHENTICATION_TYPE,
                    password,
                    Constants.CLIENT_ADDRESS,
                    Constants.SERVER_ADDRESS,
                    Constants.INTERFACETYPE,
                    Constants.LOGICAL_REFERENCE_NAME,
                    authKey,
                    blockCipherkey,
                    systemTittle,
                    Constants.AUTHENTICATION_TYPE,
                    manufacturer,
                    phase,
                    mActivity
                )
              /*  if (isConnected.equals("CONNECTED", true)) {
                    Toast.makeText(mActivity, "Meter Already Connected", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                    return
                } else {*/
                    progressDialog.dismiss()

                    val intent = Intent()
                    if (!reconnectionFlag) {
                        intent.putExtra("action", "Disconnect")
                    } else {
                        intent.putExtra("action", "Reconnect")
                    }
                    intent.putExtra("fieldMeterNo", deviceId)
                    intent.putExtra("remark", fragmentRelayConnectBinding.txtRemark.text.toString())
                    intent.putExtra("visitDate", Utility.currentDateTime)
                    mActivity!!.setResult(300, intent)
                    mActivity!!.finish()
              //  }
            } catch (e: Exception) {
                e.printStackTrace()
                if (!mActivity!!.isFinishing) progressDialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (!mActivity!!.isFinishing) {
                progressDialog.cancel()
            }
        }
    }

    private fun saveRelayData() {
        val intent = Intent()
        intent.putExtra("action", "No Action")
        intent.putExtra("fieldMeterNo", deviceId)
        intent.putExtra("remark", fragmentRelayConnectBinding.txtRemark.text.toString())
        intent.putExtra("visitDate", Utility.currentDateTime)
        mActivity!!.setResult(300, intent)
        mActivity!!.finish()
    }


    private fun getValidateSdkData() {
        val dialog = progressDialog(
            mActivity!!, resources.getString(R.string.downloading_complaint_data)
        )
        dialog.show()

        val gerritAPI = mInstance.retrofitInstanceLogin!!.create(
            Api::class.java
        )
        val call = gerritAPI.getSdkValidityData()

        call?.enqueue(object : Callback<ValidateModel?> {
            override fun onResponse(
                call: Call<ValidateModel?>,
                response: Response<ValidateModel?>
            ) {
                if (response.isSuccessful) {
                    dialog.dismiss()
                    val masterDataResponse = response.body()
                    if (masterDataResponse != null) {
                        if (!masterDataResponse.STATUS) {
                            snackBar(
                                fragmentRelayConnectBinding.rootLayout,
                                masterDataResponse.MESSAGE.toString(),
                                3000,
                                ContextCompat.getColor(requireContext(), R.color.warning)
                            )
                            if (dialog.isShowing) {
                                dialog.dismiss()
                            }
                        } else {
                            Toast.makeText(mActivity, "Download Sucessfull", Toast.LENGTH_LONG)
                                .show()
                            saveStringPreference(
                                Constants.DOWNLOADING_DATE,
                                masterDataResponse.DOWNLOADING_DATE
                            )
                            saveStringPreference(
                                Constants.EXPIRY_DATE,
                                masterDataResponse.EXPIRY_DATE
                            )
                            saveStringPreference(Constants.UGO_TOKEN, masterDataResponse.UGO_TOKEN)
                            saveBooleanPreference(Constants.VALID_SDK, true)
                            if (stringToDate(getStringPreference(Constants.EXPIRY_DATE))!!.compareTo(stringToDate(currentDateDay)) < 0) {
                                unAuthorizedAccess(mActivity!!)
                            }

                        }
                    } else {
                        snackBar(
                            fragmentRelayConnectBinding.rootLayout,
                            resources.getString(R.string.somethingwentwrong),
                            3000,
                            ContextCompat.getColor(requireContext(), R.color.warning)
                        )
                        if (dialog.isShowing) {
                            dialog.dismiss()
                        }
                    }
                } else {
                    dialog.cancel()
                    Log.d("TAG", "onResponse: " + response.message())
                }
            }

            override fun onFailure(call: Call<ValidateModel?>, t: Throwable) {
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
                call.cancel()
            }
        })
    }

    private fun unAuthorizedAccess(mActivity: FragmentActivity) {
        AlertDialog.Builder(mActivity, R.style.AlertDialogCustom_1)
            .setMessage("SDK Validity is Expired")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                val intent = Intent()
                intent.putExtra("filePath", "")
                mActivity.setResult(200, intent)
                mActivity.finish()
            }
            .show()
    }


}