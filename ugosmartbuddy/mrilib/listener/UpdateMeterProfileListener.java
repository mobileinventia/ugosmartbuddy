package com.inventive.ugosmartbuddy.mrilib.listener;
import com.inventive.ugosmartbuddy.mrilib.models.Readings;

public interface UpdateMeterProfileListener {

    void updateMeterProfile(Readings readings, boolean isBilling, boolean isBillingException,
                            boolean isLoadsurvey, boolean isLoadsurveyException, boolean isTamper,
                            boolean isTamperException
            , boolean isDone);

    void updateProgress(boolean isProgress);
}
