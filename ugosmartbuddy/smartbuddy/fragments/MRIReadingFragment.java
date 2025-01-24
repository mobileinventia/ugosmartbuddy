package com.inventive.ugosmartbuddy.smartbuddy.fragments;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.inventive.ugosmartbuddy.apiCallRetrofit.Api;
import com.inventive.ugosmartbuddy.R;
import com.inventive.ugosmartbuddy.common.Constants;
import com.inventive.ugosmartbuddy.common.Utility;
import com.inventive.ugosmartbuddy.mrilib.common.Utils;
import com.inventive.ugosmartbuddy.mrilib.listener.MeterCallbackListener;
import com.inventive.ugosmartbuddy.mrilib.listener.MeterMisMatchListener;
import com.inventive.ugosmartbuddy.mrilib.listener.UpdateMeterProfileListener;
import com.inventive.ugosmartbuddy.mrilib.models.OBIS;
import com.inventive.ugosmartbuddy.mrilib.mri.DisconnectionControl;
import com.inventive.ugosmartbuddy.mrilib.mri.MRIAsync;
import com.inventive.ugosmartbuddy.mrilib.serial_communication.serial.GXSerial;
import com.inventive.ugosmartbuddy.mrilib.wifi.WifiConnectivity;
import com.inventive.ugosmartbuddy.smartbuddy.models.OBISMaster;
import com.inventive.ugosmartbuddy.smartbuddy.models.Readings;
import com.inventive.ugosmartbuddy.smartbuddy.models.SaveMRIDataModel;
import com.inventive.ugosmartbuddy.smartbuddy.models.ValidateModel;
import com.inventive.ugosmartbuddy.ugoapp.UgoApplication;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class MRIReadingFragment extends Fragment implements View.OnClickListener {
    private GXSerial serial;
    private FragmentActivity mActivity;
    private ProgressBar progressBilling, progressTamper, progressLoadSurvey;
    private ImageView imgBillingCheck, imgTamperCheck, imgLoadSurveyCheck, imgLoadSurveyCancel, imgTamperCancel;
    private TextView lblMeterBilling, lblTamper, lblLoadSurvey, txtPhase, txtPleaseWait;
    private Button btnDone, btnBack, btnRetry;
    private Bundle bundle;
    private MRIAsync mri;
    private String mriMeterNo;
    private ImageView imgBillingCancel;
    private String accountId, complaintNo;
    private String companyName, MeterMakeId;
    private String protocol;
    private String connectionMedium;
    private boolean isLoadsurvey, isBilling, recFlag;
    private boolean isTamper;
    private String Load_Days;
    private GifImageView load_gif1, load_gif2;
    private int protocolCode;
    private boolean IsSinglePhase;
    FrameLayout layout_root;
    private ArrayList<OBIS> general;
    private ArrayList<OBIS> instantaneous;
    private ArrayList<OBIS> billing;
    private ArrayList<OBIS> loadSurvey;
    private ArrayList<OBIS> temper;
    private final SaveMRIDataModel saveMRIDataModel = null;
    Readings ugo_readings = null;
    private boolean autoMRI = false;
    private String phaseTypeName;
    private int phaseTypeId;
    private String password;
    private int clientAddress;
    private String blockCipherkey;
    private String authKey;
    private String systemTittle;
    private String manufacturer, meterNo, deviceId;


    public MRIReadingFragment() {
    }

    @SuppressLint("ValidFragment")
    public MRIReadingFragment(boolean autoMRI, int protocolCode, boolean IsSinglePhase, GXSerial serial) {
        this.autoMRI = autoMRI;
        this.protocolCode = protocolCode;
        this.serial = serial;
        this.IsSinglePhase = IsSinglePhase;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mi_old_mri_reading, container, false);
        progressBilling = view.findViewById(R.id.progressBilling);
        progressTamper = view.findViewById(R.id.progressTamper);
        progressLoadSurvey = view.findViewById(R.id.progressLoadSurvey);
        load_gif1 = view.findViewById(R.id.load_gif1);
        load_gif2 = view.findViewById(R.id.load_gif2);
        imgBillingCheck = view.findViewById(R.id.imgBillingCheck);
        imgBillingCancel = view.findViewById(R.id.imgBillingCancel);
        imgTamperCheck = view.findViewById(R.id.imgTamperCheck);
        imgLoadSurveyCheck = view.findViewById(R.id.imgLoadSurveyCheck);
        imgLoadSurveyCancel = view.findViewById(R.id.imgLoadSurveyCancel);
        imgTamperCancel = view.findViewById(R.id.imgTamperCancel);

        lblMeterBilling = view.findViewById(R.id.lblMeterBilling);
        lblTamper = view.findViewById(R.id.lblTamper);
        lblLoadSurvey = view.findViewById(R.id.lblLoadSurvey);
        txtPleaseWait = view.findViewById(R.id.txtPleaseWait);
        txtPhase = view.findViewById(R.id.txtPhase);
        layout_root = view.findViewById(R.id.layout_root);

        btnDone = view.findViewById(R.id.btnDone);
        btnBack = view.findViewById(R.id.btnBack);
        btnRetry = view.findViewById(R.id.btnRetry);

        btnDone.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        lblMeterBilling.setOnClickListener(this);
        lblTamper.setOnClickListener(this);
        lblLoadSurvey.setOnClickListener(this);

        if (serial == null) {
            serial = Utils.findPorts(mActivity);
        } else {
            if (!serial.isOpen() || !WifiConnectivity.isConnected) {
                try {
                    serial.open();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        bundle = getArguments();
        if (bundle != null) {
            accountId = bundle.getString("ACCOUNT_NO");
            companyName = bundle.getString("MeterMakeName");
            MeterMakeId = bundle.getString("MeterMakeId");
            protocol = bundle.getString("protocol");
            connectionMedium = bundle.getString("connectionMedium");
            isBilling = bundle.getBoolean("isBilling");
            isLoadsurvey = bundle.getBoolean("isLoadsurvey");
            isTamper = bundle.getBoolean("isTamper");
            Load_Days = bundle.getString("Load_Days");
            phaseTypeId = bundle.getInt("phaseTypeId");
            phaseTypeName = bundle.getString("phaseTypeName");
            password = bundle.getString("PASSWORD");
            clientAddress = bundle.getInt("CLIENT_ADDRESS");
            blockCipherkey = bundle.getString("BLOCK_CIPHER_KEY");
            authKey = bundle.getString("AUTH_KEY");
            systemTittle = bundle.getString("SYSTEM_TITTLE");
            manufacturer = bundle.getString("MANUFECTURER");
            meterNo = bundle.getString("meterNo");
            recFlag = bundle.getBoolean("reconnect_flag");
        }


        Utility.saveBooleanPreference(Constants.VALID_SDK, false);
        if (!Utility.isDownloadDateAvailable(mActivity)) {
            getValidateSdkData();
        }
        else {
            if (Utility.getStringPreference(Constants.DOWNLOADING_DATE).equals(Utility.getCurrentDateDay())) {
                if (Utility.stringToDate(Utility.getStringPreference(Constants.EXPIRY_DATE)).compareTo(Utility.stringToDate(Utility.getCurrentDateDay())) < 0) {
                    unAuthorizedAccess(mActivity);
                } else {
                    if (checkValidMeter()) {
                        if (isBilling) {
                            progressBilling.setVisibility(View.VISIBLE);
                            imgBillingCheck.setVisibility(View.GONE);
                            imgBillingCancel.setVisibility(View.GONE);
                        } else {
                            progressBilling.setVisibility(GONE);
                            imgBillingCancel.setVisibility(View.VISIBLE);
                            imgBillingCheck.setVisibility(View.GONE);
                            lblMeterBilling.setText("Not Selected");
                        }

                        if (isLoadsurvey) {
                            progressLoadSurvey.setVisibility(View.VISIBLE);
                            imgLoadSurveyCheck.setVisibility(View.GONE);
                            imgLoadSurveyCancel.setVisibility(View.GONE);
                        } else {
                            progressLoadSurvey.setVisibility(GONE);
                            imgLoadSurveyCancel.setVisibility(View.VISIBLE);
                            imgLoadSurveyCheck.setVisibility(View.GONE);
                            lblLoadSurvey.setText("Not Selected");
                        }

                        if (isTamper) {
                            progressTamper.setVisibility(View.VISIBLE);
                            imgTamperCheck.setVisibility(View.GONE);
                            imgTamperCancel.setVisibility(View.GONE);
                        } else {
                            progressTamper.setVisibility(GONE);
                            imgTamperCheck.setVisibility(View.GONE);
                            lblTamper.setText("Not Selected");
                            imgTamperCancel.setVisibility(View.VISIBLE);
                        }

                        txtPhase.setText(companyName + " - " + protocol);
                        try {
                            UpdateMeterProfileListener updateMeterProfileListener = new UpdateMeterProfileListener() {
                                @Override
                                public void updateMeterProfile(com.inventive.ugosmartbuddy.mrilib.models.Readings readings, boolean isBilling, boolean isBillingException,
                                                               boolean isLoadsurvey, boolean isLoadsurveyException, boolean isTamper, boolean isTamperException
                                        , boolean isDone) {
                                    if (isBilling) {
                                        progressBilling.setVisibility(GONE);
                                        imgBillingCheck.setVisibility(View.VISIBLE);
                                        imgBillingCancel.setVisibility(View.GONE);
                                    }
                                    if (isBillingException) {
                                        progressBilling.setVisibility(GONE);
                                        imgBillingCheck.setVisibility(View.GONE);
                                        imgBillingCancel.setVisibility(View.VISIBLE);
                                        load_gif1.setVisibility(View.GONE);
                                        load_gif2.setVisibility(View.VISIBLE);
                                    }

                                    if (isLoadsurvey) {
                                        progressLoadSurvey.setVisibility(GONE);
                                        imgLoadSurveyCheck.setVisibility(View.VISIBLE);
                                        imgLoadSurveyCancel.setVisibility(View.GONE);
                                        lblLoadSurvey.setTextColor(Color.parseColor("#000000"));

                                    }
                                    if (isLoadsurveyException) {
                                        progressLoadSurvey.setVisibility(GONE);
                                        imgLoadSurveyCheck.setVisibility(View.GONE);
                                        imgLoadSurveyCancel.setVisibility(View.VISIBLE);
                                        load_gif1.setVisibility(View.GONE);
                                        load_gif2.setVisibility(View.VISIBLE);
                                    }

                                    if (isTamper) {
                                        progressTamper.setVisibility(GONE);
                                        imgTamperCheck.setVisibility(View.VISIBLE);
                                        imgTamperCancel.setVisibility(View.GONE);
                                        lblTamper.setTextColor(Color.parseColor("#000000"));

                                    }
                                    if (isTamperException) {
                                        progressTamper.setVisibility(GONE);
                                        imgTamperCheck.setVisibility(View.GONE);
                                        imgTamperCancel.setVisibility(View.VISIBLE);
                                        load_gif1.setVisibility(View.GONE);
                                        load_gif2.setVisibility(View.VISIBLE);

                                    }

                                    if (isDone) {
                                        load_gif1.setVisibility(View.GONE);
                                        load_gif2.setVisibility(View.VISIBLE);
                                        btnDone.setVisibility(View.VISIBLE);
                                        if (isBilling) {
//                            ugo_readings = MRIReadingsDataHelper.getByComplaintNo(complaintNo,accountId,mriMeterNo, mActivity);
//                            saveMRIDataModel = MRIDataHelper.getByComplaintNo(complaintNo,accountId, mActivity);

                                            if (ugo_readings == null)
                                                ugo_readings = new Readings();

                                            ugo_readings.setACCOUNT_NO(accountId);
                                            ugo_readings.setGENERAL_READING(readings.getGENERAL_READING());
                                            ugo_readings.setINSTANTENOUS_READING(readings.getINSTANTENOUS_READING());
                                            ugo_readings.setBILLING_READING(readings.getBILLING_READING());
                                            if (readings.getTAMPER_READING() != null)
                                                ugo_readings.setTAMPER_READING(readings.getTAMPER_READING());
                                            if (readings.getLOADSURVEY_READING() != null) {
                                                ugo_readings.setLOADSURVEY_READING(readings.getLOADSURVEY_READING());
                                                ugo_readings.setLOADSURVEY_SCALER(readings.getLOADSURVEY_SCALER());
                                            }

                                            lblMeterBilling.setTextColor(Color.parseColor("#000000"));
                                        }

                                        btnDone.setText("Continue");
                                        lblMeterBilling.setClickable(true);

                                        if (isTamper)
                                            lblTamper.setTextColor(Color.parseColor("#000000"));
                                        if (isLoadsurvey)
                                            lblLoadSurvey.setTextColor(Color.parseColor("#000000"));

                                        btnRetry.setVisibility(View.VISIBLE);
                                        btnBack.setVisibility(View.VISIBLE);


                                        if (!lblTamper.getText().equals("Not Selected")) {
                                            lblTamper.setTextColor(Color.parseColor("#000000"));
                                            lblTamper.setClickable(true);
                                        }
                                        if (!lblLoadSurvey.getText().equals("Not Selected")) {
                                            lblLoadSurvey.setClickable(true);
                                            lblLoadSurvey.setTextColor(Color.parseColor("#000000"));
                                        }
                                        Utility.playBeep(mActivity, "beep1.wav");
                                    }
                                }

                                @Override
                                public void updateProgress(boolean isProgress) {
                                    if (isProgress) {
                                        load_gif1.setVisibility(View.VISIBLE);
                                        load_gif2.setVisibility(View.GONE);
                                        progressBilling.setVisibility(View.VISIBLE);
                                        imgBillingCheck.setVisibility(GONE);
                                        imgBillingCancel.setVisibility(GONE);
                                    }
                                }
                            };

                            MRIAsync.setUpdateMeterProfileListener(updateMeterProfileListener);

                            OBISMaster obisMaster = new OBISMaster();
                            ArrayList<OBISMaster.OBIS> obisGroupModelList = obisMaster.obisGroup();


                            general = Utility.convertOBISModel(getGeneralData(obisGroupModelList));
                            instantaneous = Utility.convertOBISModel(getInstantaneousData(obisGroupModelList));
                            billing = Utility.convertOBISModel(getBillingData(obisGroupModelList));
                            loadSurvey = Utility.convertOBISModel(getLoadSurveyData(obisGroupModelList));
                            temper = Utility.convertOBISModel(getTemperData(obisGroupModelList));

                            if (mri == null)
                                mri = new MRIAsync(Constants.UTILITY_CODE, protocolCode, IsSinglePhase, txtPleaseWait, mActivity,
                                        MeterMakeId, isBilling, isLoadsurvey, isTamper, Load_Days, serial, true,
                                        true, true, general, instantaneous, billing, loadSurvey, temper, Constants.BAUDRATE, Constants.DATA_BIT, Constants.STOP_BIT,
                                        Constants.PARITY_BIT, Constants.AUTHENTICATION_TYPE, password,
                                        clientAddress, Constants.SERVER_ADDRESS, Constants.INTERFACETYPE,
                                        Constants.LOGICAL_REFERENCE_NAME, blockCipherkey,
                                        authKey, systemTittle, manufacturer,
                                        phaseTypeName, new MeterCallbackListener() {
                                    @Override
                                    public void updateOnSuccess(String meterNo) {

                                        mriMeterNo = meterNo;
                                    }
                                });


                            mri.execute();
                            lblMeterBilling.setClickable(false);
                            lblTamper.setClickable(false);
                            lblLoadSurvey.setClickable(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        MeterMisMatchListener meterMisMatchListener = mismatch -> {
                            if (mismatch) {
                                //Utility.replaceFragment(new MRIAssignedFragment(), mActivity.getSupportFragmentManager(), R.id.layout_fragment);
                            }
                        };

                        MRIAsync.setMeterMisMatchListener(meterMisMatchListener);
                    } else {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.meter_no_or_meter_make), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.putExtra("filePath", "");
                        mActivity.setResult(200, intent);
                        mActivity.finish();
                    }
                }
            } else {
                getValidateSdkData();
            }
        }


        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mri.cancel(true);
                    if (mri.isCancelled())
                        mri = null;

                    return true;
                }
            }
            return false;
        });
        return view;
    }

    private void getValidateSdkData() {
        Dialog dialog = Utility.progressDialog(mActivity, getResources().getString(R.string.downloading_complaint_data));
        dialog.show();

        Api gerritAPI = UgoApplication.Companion.getMInstance().getRetrofitInstanceLogin().create(Api.class);
        Call<ValidateModel> call = gerritAPI.getSdkValidityData();

        if (call != null) {
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ValidateModel> call, Response<ValidateModel> response) {
                    if (response.isSuccessful()) {
                        dialog.dismiss();
                        ValidateModel masterDataResponse = response.body();
                        if (masterDataResponse != null) {
                            if (!masterDataResponse.getSTATUS()) {
                                Utility.snackBar(
                                        layout_root,
                                        masterDataResponse.getMESSAGE().toString(),
                                        3000,
                                        ContextCompat.getColor(requireContext(), R.color.warning)
                                );
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            } else {
                                Toast.makeText(mActivity,"Download Sucessfull",Toast.LENGTH_LONG).show();
                                Utility.saveStringPreference(Constants.DOWNLOADING_DATE, masterDataResponse.getDOWNLOADING_DATE());
                                Utility.saveStringPreference(Constants.EXPIRY_DATE, masterDataResponse.getEXPIRY_DATE());
                                Utility.saveStringPreference(Constants.UGO_TOKEN, masterDataResponse.getUGO_TOKEN());
                                Utility.saveBooleanPreference(Constants.VALID_SDK, true);
                                if (Utility.stringToDate(Utility.getStringPreference(Constants.EXPIRY_DATE)).compareTo(Utility.stringToDate(Utility.getCurrentDateDay())) < 0) {
                                    unAuthorizedAccess(mActivity);
                                }
                                else {
                                    if (checkValidMeter()) {
                                        if (isBilling) {
                                            progressBilling.setVisibility(View.VISIBLE);
                                            imgBillingCheck.setVisibility(View.GONE);
                                            imgBillingCancel.setVisibility(View.GONE);
                                        } else {
                                            progressBilling.setVisibility(GONE);
                                            imgBillingCancel.setVisibility(View.VISIBLE);
                                            imgBillingCheck.setVisibility(View.GONE);
                                            lblMeterBilling.setText("Not Selected");
                                        }

                                        if (isLoadsurvey) {
                                            progressLoadSurvey.setVisibility(View.VISIBLE);
                                            imgLoadSurveyCheck.setVisibility(View.GONE);
                                            imgLoadSurveyCancel.setVisibility(View.GONE);
                                        } else {
                                            progressLoadSurvey.setVisibility(GONE);
                                            imgLoadSurveyCancel.setVisibility(View.VISIBLE);
                                            imgLoadSurveyCheck.setVisibility(View.GONE);
                                            lblLoadSurvey.setText("Not Selected");
                                        }

                                        if (isTamper) {
                                            progressTamper.setVisibility(View.VISIBLE);
                                            imgTamperCheck.setVisibility(View.GONE);
                                            imgTamperCancel.setVisibility(View.GONE);
                                        } else {
                                            progressTamper.setVisibility(GONE);
                                            imgTamperCheck.setVisibility(View.GONE);
                                            lblTamper.setText("Not Selected");
                                            imgTamperCancel.setVisibility(View.VISIBLE);
                                        }

                                        txtPhase.setText(companyName + " - " + protocol);
                                        try {
                                            UpdateMeterProfileListener updateMeterProfileListener = new UpdateMeterProfileListener() {
                                                @Override
                                                public void updateMeterProfile(com.inventive.ugosmartbuddy.mrilib.models.Readings readings, boolean isBilling, boolean isBillingException,
                                                                               boolean isLoadsurvey, boolean isLoadsurveyException, boolean isTamper, boolean isTamperException
                                                        , boolean isDone) {
                                                    if (isBilling) {
                                                        progressBilling.setVisibility(GONE);
                                                        imgBillingCheck.setVisibility(View.VISIBLE);
                                                        imgBillingCancel.setVisibility(View.GONE);
                                                    }
                                                    if (isBillingException) {
                                                        progressBilling.setVisibility(GONE);
                                                        imgBillingCheck.setVisibility(View.GONE);
                                                        imgBillingCancel.setVisibility(View.VISIBLE);
                                                        load_gif1.setVisibility(View.GONE);
                                                        load_gif2.setVisibility(View.VISIBLE);
                                                    }

                                                    if (isLoadsurvey) {
                                                        progressLoadSurvey.setVisibility(GONE);
                                                        imgLoadSurveyCheck.setVisibility(View.VISIBLE);
                                                        imgLoadSurveyCancel.setVisibility(View.GONE);
                                                        lblLoadSurvey.setTextColor(Color.parseColor("#000000"));

                                                    }
                                                    if (isLoadsurveyException) {
                                                        progressLoadSurvey.setVisibility(GONE);
                                                        imgLoadSurveyCheck.setVisibility(View.GONE);
                                                        imgLoadSurveyCancel.setVisibility(View.VISIBLE);
                                                        load_gif1.setVisibility(View.GONE);
                                                        load_gif2.setVisibility(View.VISIBLE);
                                                    }

                                                    if (isTamper) {
                                                        progressTamper.setVisibility(GONE);
                                                        imgTamperCheck.setVisibility(View.VISIBLE);
                                                        imgTamperCancel.setVisibility(View.GONE);
                                                        lblTamper.setTextColor(Color.parseColor("#000000"));

                                                    }
                                                    if (isTamperException) {
                                                        progressTamper.setVisibility(GONE);
                                                        imgTamperCheck.setVisibility(View.GONE);
                                                        imgTamperCancel.setVisibility(View.VISIBLE);
                                                        load_gif1.setVisibility(View.GONE);
                                                        load_gif2.setVisibility(View.VISIBLE);

                                                    }

                                                    if (isDone) {
                                                        load_gif1.setVisibility(View.GONE);
                                                        load_gif2.setVisibility(View.VISIBLE);
                                                        btnDone.setVisibility(View.VISIBLE);
                                                        if (isBilling) {
//                            ugo_readings = MRIReadingsDataHelper.getByComplaintNo(complaintNo,accountId,mriMeterNo, mActivity);
//                            saveMRIDataModel = MRIDataHelper.getByComplaintNo(complaintNo,accountId, mActivity);

                                                            if (ugo_readings == null)
                                                                ugo_readings = new Readings();

                                                            ugo_readings.setACCOUNT_NO(accountId);
                                                            ugo_readings.setGENERAL_READING(readings.getGENERAL_READING());
                                                            ugo_readings.setINSTANTENOUS_READING(readings.getINSTANTENOUS_READING());
                                                            ugo_readings.setBILLING_READING(readings.getBILLING_READING());
                                                            if (readings.getTAMPER_READING() != null)
                                                                ugo_readings.setTAMPER_READING(readings.getTAMPER_READING());
                                                            if (readings.getLOADSURVEY_READING() != null) {
                                                                ugo_readings.setLOADSURVEY_READING(readings.getLOADSURVEY_READING());
                                                                ugo_readings.setLOADSURVEY_SCALER(readings.getLOADSURVEY_SCALER());
                                                            }

                                                            lblMeterBilling.setTextColor(Color.parseColor("#000000"));
                                                        }

                                                        btnDone.setText("Continue");
                                                        lblMeterBilling.setClickable(true);

                                                        if (isTamper)
                                                            lblTamper.setTextColor(Color.parseColor("#000000"));
                                                        if (isLoadsurvey)
                                                            lblLoadSurvey.setTextColor(Color.parseColor("#000000"));

                                                        btnRetry.setVisibility(View.VISIBLE);
                                                        btnBack.setVisibility(View.VISIBLE);


                                                        if (!lblTamper.getText().equals("Not Selected")) {
                                                            lblTamper.setTextColor(Color.parseColor("#000000"));
                                                            lblTamper.setClickable(true);
                                                        }
                                                        if (!lblLoadSurvey.getText().equals("Not Selected")) {
                                                            lblLoadSurvey.setClickable(true);
                                                            lblLoadSurvey.setTextColor(Color.parseColor("#000000"));
                                                        }
                                                        Utility.playBeep(mActivity, "beep1.wav");
                                                    }
                                                }

                                                @Override
                                                public void updateProgress(boolean isProgress) {
                                                    if (isProgress) {
                                                        load_gif1.setVisibility(View.VISIBLE);
                                                        load_gif2.setVisibility(View.GONE);
                                                        progressBilling.setVisibility(View.VISIBLE);
                                                        imgBillingCheck.setVisibility(GONE);
                                                        imgBillingCancel.setVisibility(GONE);
                                                    }
                                                }
                                            };

                                            MRIAsync.setUpdateMeterProfileListener(updateMeterProfileListener);

                                            OBISMaster obisMaster = new OBISMaster();
                                            ArrayList<OBISMaster.OBIS> obisGroupModelList = obisMaster.obisGroup();


                                            general = Utility.convertOBISModel(getGeneralData(obisGroupModelList));
                                            instantaneous = Utility.convertOBISModel(getInstantaneousData(obisGroupModelList));
                                            billing = Utility.convertOBISModel(getBillingData(obisGroupModelList));
                                            loadSurvey = Utility.convertOBISModel(getLoadSurveyData(obisGroupModelList));
                                            temper = Utility.convertOBISModel(getTemperData(obisGroupModelList));

                                            if (mri == null)
                                                mri = new MRIAsync(Constants.UTILITY_CODE, protocolCode, IsSinglePhase, txtPleaseWait, mActivity,
                                                        MeterMakeId, isBilling, isLoadsurvey, isTamper, Load_Days, serial, true,
                                                        true, true, general, instantaneous, billing, loadSurvey, temper, Constants.BAUDRATE, Constants.DATA_BIT, Constants.STOP_BIT,
                                                        Constants.PARITY_BIT, Constants.AUTHENTICATION_TYPE, password,
                                                        clientAddress, Constants.SERVER_ADDRESS, Constants.INTERFACETYPE,
                                                        Constants.LOGICAL_REFERENCE_NAME, blockCipherkey,
                                                        authKey, systemTittle, manufacturer,
                                                        phaseTypeName, new MeterCallbackListener() {
                                                    @Override
                                                    public void updateOnSuccess(String meterNo) {

                                                        mriMeterNo = meterNo;
                                                    }
                                                });


                                            mri.execute();
                                            lblMeterBilling.setClickable(false);
                                            lblTamper.setClickable(false);
                                            lblLoadSurvey.setClickable(false);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        MeterMisMatchListener meterMisMatchListener = mismatch -> {
                                            if (mismatch) {
                                                //Utility.replaceFragment(new MRIAssignedFragment(), mActivity.getSupportFragmentManager(), R.id.layout_fragment);
                                            }
                                        };

                                        MRIAsync.setMeterMisMatchListener(meterMisMatchListener);
                                    } else {
                                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.meter_no_or_meter_make), Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent();
                                        intent.putExtra("filePath", "");
                                        mActivity.setResult(200, intent);
                                        mActivity.finish();
                                    }
                                }
                            }
                        } else {
                            Utility.snackBar(
                                    layout_root,
                                    getResources().getString(R.string.somethingwentwrong),
                                    3000,
                                    ContextCompat.getColor(requireContext(), R.color.warning)
                            );
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    } else {
                        dialog.cancel();
                        Log.d("TAG", "onResponse: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ValidateModel> call, Throwable t) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    call.cancel();
                }
            });
        }
    }

    private void unAuthorizedAccess(FragmentActivity mActivity) {
        new AlertDialog.Builder(mActivity, R.style.AlertDialogCustom_1)
                .setTitle("UGO Smart Buddy SDK")
                .setMessage("SDK Validity is Expired")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.putExtra("filePath", "");
                        mActivity.setResult(200, intent);
                        mActivity.finish();
                    }
                })
                .show();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, @NotNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    @Override
    public void onClick(View v) {
        //Fragment fragment = new SurveyMeterFragment();
        // Fragment fragment = new MRITabsFragment(activityDashboardBinding, fragmentMriManualReadingBinding, autoMRI);
        int id = v.getId();
        if (id == R.id.btnBack || id == R.id.btnDone) {
            String fileName =
                    (accountId + "_MRI_") + System.currentTimeMillis();
            JSONObject jsonObject =
                    Utility.getJsonMRI(ugo_readings, accountId);
            Log.d("MRI JSON", jsonObject.toString());

            String filePath = Utility.saveMriFileWithoutExtension(fileName, mriMeterNo, String.valueOf(jsonObject));

            bundle.putInt("pageId", 1);
            bundle.putString("meterNo", mriMeterNo);

            Intent intent = new Intent();
            intent.putExtra("filePath", filePath);
            mActivity.setResult(200, intent);
            mActivity.finish();
            //fragment.setArguments(bundle);
            //  Utility.replaceFragment(fragment, mActivity.getSupportFragmentManager(), R.id.layout_fragment);
        } else if (id == R.id.lblMeterBilling) {
            mri.cancel(true);
            if (mri.isCancelled()) {
                mri = null;
                if (mri == null)
                    mri = new MRIAsync(Constants.UTILITY_CODE, protocolCode, IsSinglePhase, txtPleaseWait, mActivity,
                            MeterMakeId, isBilling, isLoadsurvey, isTamper, Load_Days, serial, true,
                            false, false, general, instantaneous, billing, loadSurvey, temper, Constants.BAUDRATE, Constants.DATA_BIT, Constants.STOP_BIT,
                            Constants.PARITY_BIT, Constants.AUTHENTICATION_TYPE, password,
                            clientAddress, Constants.SERVER_ADDRESS, Constants.INTERFACETYPE,
                            Constants.LOGICAL_REFERENCE_NAME, blockCipherkey,
                            authKey, systemTittle, manufacturer,
                            phaseTypeName, new MeterCallbackListener() {
                        @Override
                        public void updateOnSuccess(String meterNo) {

                            mriMeterNo = meterNo;
                        }
                    });

                load_gif2.setVisibility(GONE);
                load_gif1.setVisibility(View.VISIBLE);
                mri.execute();
                lblMeterBilling.setClickable(false);
                lblTamper.setClickable(false);
                lblLoadSurvey.setClickable(false);
                btnDone.setVisibility(GONE);
                btnBack.setVisibility(GONE);
                btnRetry.setVisibility(GONE);
                progressBilling.setVisibility(View.VISIBLE);
                imgBillingCheck.setVisibility(GONE);
                imgBillingCancel.setVisibility(GONE);
            }
        } else if (id == R.id.lblTamper) {
            mri.cancel(true);
            if (mri.isCancelled()) {
                mri = null;
                if (mri == null)
                    mri = new MRIAsync(Constants.UTILITY_CODE, protocolCode, IsSinglePhase, txtPleaseWait, mActivity,
                            MeterMakeId, isBilling, isLoadsurvey, isTamper, Load_Days, serial, false,
                            true, false, general, instantaneous, billing, loadSurvey, temper, Constants.BAUDRATE, Constants.DATA_BIT, Constants.STOP_BIT,
                            Constants.PARITY_BIT, Constants.AUTHENTICATION_TYPE, password,
                            clientAddress, Constants.SERVER_ADDRESS, Constants.INTERFACETYPE,
                            Constants.LOGICAL_REFERENCE_NAME, blockCipherkey,
                            authKey, systemTittle, manufacturer,
                            phaseTypeName, new MeterCallbackListener() {
                        @Override
                        public void updateOnSuccess(String meterNo) {

                            mriMeterNo = meterNo;
                        }
                    });

                load_gif2.setVisibility(GONE);
                load_gif1.setVisibility(View.VISIBLE);
                mri.execute();
                lblMeterBilling.setClickable(false);
                lblTamper.setClickable(false);
                lblLoadSurvey.setClickable(false);
                btnDone.setVisibility(GONE);
                btnBack.setVisibility(GONE);
                btnRetry.setVisibility(GONE);
                progressTamper.setVisibility(View.VISIBLE);
                imgTamperCheck.setVisibility(GONE);
                imgTamperCancel.setVisibility(GONE);
            }
        } else if (id == R.id.lblLoadSurvey) {
            mri.cancel(true);
            if (mri.isCancelled()) {
                mri = null;
                if (mri == null)
                    mri = new MRIAsync(Constants.UTILITY_CODE, protocolCode, IsSinglePhase, txtPleaseWait, mActivity,
                            MeterMakeId, isBilling, isLoadsurvey, isTamper, Load_Days, serial, false,
                            false, true, general, instantaneous, billing, loadSurvey, temper, Constants.BAUDRATE, Constants.DATA_BIT, Constants.STOP_BIT,
                            Constants.PARITY_BIT, Constants.AUTHENTICATION_TYPE, password,
                            clientAddress, Constants.SERVER_ADDRESS, Constants.INTERFACETYPE,
                            Constants.LOGICAL_REFERENCE_NAME, blockCipherkey,
                            authKey, systemTittle, manufacturer,
                            phaseTypeName, new MeterCallbackListener() {
                        @Override
                        public void updateOnSuccess(String meterNo) {

                            mriMeterNo = meterNo;
                        }
                    });

                load_gif2.setVisibility(GONE);
                load_gif1.setVisibility(View.VISIBLE);
                mri.execute();
                lblMeterBilling.setClickable(false);
                lblTamper.setClickable(false);
                lblLoadSurvey.setClickable(false);
                btnDone.setVisibility(GONE);
                btnBack.setVisibility(GONE);
                btnRetry.setVisibility(GONE);
                progressLoadSurvey.setVisibility(View.VISIBLE);
                imgLoadSurveyCheck.setVisibility(GONE);
                imgLoadSurveyCancel.setVisibility(GONE);
            }
        } else if (id == R.id.btnRetry) {
            try {

                if (serial == null) {
                    serial = Utils.findPorts(mActivity);
                } else {
                    if (!serial.isOpen() || !WifiConnectivity.isConnected) {
                        serial.open();
                    }

                }
            } catch (Exception e) {
                e.getMessage();
                Utility.snackBar(layout_root, getActivity().getResources().getString(R.string.cablenotconnected), 1200, getResources().getColor(R.color.warning));
                return;
            }


            mri.cancel(true);
            if (mri.isCancelled()) {
                mri = null;
                if (mri == null)
                    mri = new MRIAsync(Constants.UTILITY_CODE, protocolCode, IsSinglePhase, txtPleaseWait, mActivity,
                            MeterMakeId, isBilling, isLoadsurvey, isTamper, Load_Days, serial, true,
                            true, true, general, instantaneous, billing, loadSurvey, temper, Constants.BAUDRATE, Constants.DATA_BIT, Constants.STOP_BIT,
                            Constants.PARITY_BIT, Constants.AUTHENTICATION_TYPE, password,
                            clientAddress, Constants.SERVER_ADDRESS, Constants.INTERFACETYPE,
                            Constants.LOGICAL_REFERENCE_NAME, blockCipherkey,
                            authKey, systemTittle, manufacturer,
                            phaseTypeName, new MeterCallbackListener() {
                        @Override
                        public void updateOnSuccess(String meterNo) {

                            mriMeterNo = meterNo;
                        }
                    });
            }

            mri.execute();
            load_gif2.setVisibility(GONE);
            load_gif1.setVisibility(View.VISIBLE);
            lblMeterBilling.setClickable(false);
            lblTamper.setClickable(false);
            lblLoadSurvey.setClickable(false);
            btnDone.setVisibility(GONE);
            btnBack.setVisibility(GONE);
            btnRetry.setVisibility(GONE);
            resetProgress();
                /*case R.id.btnBack:
                bundle.putInt("pageId", 1);
                bundle.putString("meterNo", mriMeterNo);
                fragment.setArguments(bundle);
                Utility.replaceFragment(fragment, mActivity.getSupportFragmentManager(), R.id.layout_fragment);
                break;*/
        }
    }

    public void resetProgress() {

        progressBilling.setVisibility(View.VISIBLE);
        imgBillingCheck.setVisibility(View.GONE);
        imgBillingCancel.setVisibility(View.GONE);

        if (isLoadsurvey) {
            progressLoadSurvey.setVisibility(View.VISIBLE);
            imgLoadSurveyCheck.setVisibility(View.GONE);
            imgLoadSurveyCancel.setVisibility(View.GONE);

        }

        if (isTamper) {
            progressTamper.setVisibility(View.VISIBLE);
            imgTamperCheck.setVisibility(View.GONE);
            imgTamperCancel.setVisibility(View.GONE);

        }

    }


    public static ArrayList<OBISMaster.OBIS> getGeneralData(ArrayList<OBISMaster.OBIS> obisCodeMaster) {
        ArrayList<OBISMaster.OBIS> where = new ArrayList<>();
        for (OBISMaster.OBIS o : obisCodeMaster) {
            if (o.getPROFILER_CODE() == 15)
                where.add(o);
        }
        return where;
    }

    public static ArrayList<OBISMaster.OBIS> getInstantaneousData(ArrayList<OBISMaster.OBIS> obisCodeMaster) {
        ArrayList<OBISMaster.OBIS> where = new ArrayList<>();
        for (OBISMaster.OBIS o : obisCodeMaster) {
            if (o.getPROFILER_CODE() == 11)
                where.add(o);
        }
        return where;
    }

    public static ArrayList<OBISMaster.OBIS> getBillingData(ArrayList<OBISMaster.OBIS> obisCodeMaster) {
        ArrayList<OBISMaster.OBIS> where = new ArrayList<>();
        for (OBISMaster.OBIS o : obisCodeMaster) {
            if (o.getPROFILER_CODE() == 12)
                where.add(o);
        }
        return where;
    }

    public static ArrayList<OBISMaster.OBIS> getLoadSurveyData(ArrayList<OBISMaster.OBIS> obisCodeMaster) {
        ArrayList<OBISMaster.OBIS> where = new ArrayList<>();
        for (OBISMaster.OBIS o : obisCodeMaster) {
            if (o.getPROFILER_CODE() == 13)
                where.add(o);
        }
        return where;
    }

    public static ArrayList<OBISMaster.OBIS> getTemperData(ArrayList<OBISMaster.OBIS> obisCodeMaster) {
        ArrayList<OBISMaster.OBIS> where = new ArrayList<>();
        for (OBISMaster.OBIS o : obisCodeMaster) {
            if (o.getPROFILER_CODE() == 14)
                where.add(o);
        }
        return where;
    }

    @Override
    public void onDestroy() {
        if (mri != null) {
            mri.cancel(true);
        }
        super.onDestroy();
    }

    public boolean checkValidMeter() {
        DisconnectionControl disconnectionControl = new DisconnectionControl();
        try {
            ArrayList<String> result = disconnectionControl.verifyMeterNo(
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
                    phaseTypeName,
                    mActivity
            );

            if (result == null) {
                Utility.snackBar(
                        layout_root, "Meter Number Not Fetched",
                        1200, getActivity().getResources().getColor(R.color.warning)
                );
                return false;
            }


            Log.d("resultData", result.get(2));

            // For Allied
            /*if (result.size() > 2 && result.get(2) != null && !result.get(2).contains("Allied")) {
                Utility.snackBar(
                        layout_root,
                        getActivity().getResources().getString(R.string.meter_make_mismatch),
                        2400,
                        getActivity().getResources().getColor(R.color.warning)
                );
                return false;
            }*/


            // For LNT

            if (result.size() > 2 && result.get(2) != null && !result.get(2).contains("Schneider")) {
                Utility.snackBar(
                        layout_root,
                        getActivity().getResources().getString(R.string.meter_make_mismatch),
                        2400,
                        getActivity().getResources().getColor(R.color.warning)
                );
                return false;
            }

            if (!Utility.isNullOrEmpty(meterNo) && result.get(0) != null && meterNo.equals(result.get(0))
            ) {
                deviceId = result.get(1);
                return true;
            } else {
                Utility.snackBar(
                        layout_root,
                        getActivity().getResources().getString(R.string.meter_no_mismatch),
                        2400,
                        getActivity().getResources().getColor(R.color.warning)
                );
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}