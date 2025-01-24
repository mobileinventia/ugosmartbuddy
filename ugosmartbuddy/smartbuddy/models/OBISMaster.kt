package com.inventive.ugosmartbuddy.smartbuddy.models

import java.util.ArrayList

class OBISMaster {

    data class ObisGroupModel(
        var ID: Int,
        var PROFILER_GROUP: String,
        var OBIS_SCALER: String,
        var PROFILER_CODE: Int,
        var IS_ACTIVE: Boolean,
        var OBIS_VALUE: String,
        var OBJECT_TYPE: String,
        var DESCRIPTION: String
    )


    data class OBIS(
        var PROFILER_GROUP: String,
        var OBIS_SCALER: String,
        var PROFILER_CODE: Int,
        var OBIS_VALUE: String,
        var OBJECT_TYPE: String,
        var DESCRIPTION: String
    )


    fun ObisGroupMaster(): ArrayList<ObisGroupModel> {
        return arrayListOf(
            ObisGroupModel(
                1,
                "InstantenousParams",
                "1.0.94.91.3.255",
                11,
                true,
                "1.0.94.91.0.255",
                "ProfileGeneric",
                "Instantenous Data"
            ),
            ObisGroupModel(
                2,
                "BillingParams",
                "1.0.94.91.6.255",
                12,
                true,
                "1.0.98.1.0.255",
                "ProfileGeneric",
                "Billings "
            ),
            ObisGroupModel(
                3,
                "Loadsurvey",
                "1.0.94.91.4.255",
                13,
                true,
                "1.0.99.1.0.255",
                "ProfileGeneric",
                "date, Ir, Iy, Ib, Vr, Vy, Vb, kwh, kvah, powerfactor, reactive lag, reactive lead"
            ),
            ObisGroupModel(
                4,
                "Tamper",
                "1.0.94.91.7.255",
                14,
                true,
                "0.0.99.98.0.255",
                "ProfileGeneric",
                "Voltage",
            ),
            ObisGroupModel(
                5,
                "Tamper",
                "1.0.94.91.7.255",
                14,
                true,
                "0.0.99.98.1.255",
                "ProfileGeneric",
                "Current"
            ),
            ObisGroupModel(
                6,
                "Tamper",
                "1.0.94.91.7.255",
                14,
                true,
                "0.0.99.98.2.255",
                "ProfileGeneric",
                "Power"
            ),
            ObisGroupModel(
                7,
                "Tamper",
                "1.0.94.91.7.255",
                14,
                true,
                "0.0.99.98.3.255",
                "ProfileGeneric",
                "Transaction"
            ),
            ObisGroupModel(
                8,
                "Tamper",
                "1.0.94.91.7.255",
                14,
                true,
                "0.0.99.98.4.255",
                "ProfileGeneric",
                "Other"
            ),
            ObisGroupModel(
                9,
                "Tamper",
                "1.0.94.91.7.255",
                14,
                true,
                "0.0.99.98.5.255",
                "ProfileGeneric",
                "NonRollOver"
            ),
            ObisGroupModel(
                10,
                "General",
                "",
                15,
                true,
                "0.0.96.1.0.255",
                "Data",
                "MeterNo",
            ),
            ObisGroupModel(
                11,
                "General",
                "",
                15,
                true,
                "0.0.96.1.1.255",
                "Data",
                "Meter Make"
            ),
            ObisGroupModel(
                12,
                "General",
                "",
                15,
                true,
                "0.0.96.1.4.255",
                "Data",
                "Year of Manufacture"
            ),
            ObisGroupModel(
                13,
                "General",
                "",
                15,
                true,
                "1.0.0.2.0.255",
                "Data",
                "Firmware"
            ),
            ObisGroupModel(
                14,
                "General",
                "",
                15,
                true,
                "0.0.94.91.9.255",
                "Data",
                "Meter Type"
            ),
            ObisGroupModel(
                15,
                "General",
                "",
                15,
                true,
                "1.0.81.7.0.255",
                "Register",
                "Phase Angle 1"
            ),
            ObisGroupModel(
                16,
                "General",
                "",
                15,
                true,
                "1.0.81.7.1.255",
                "Register",
                "Phase Angle 2"
            ),
            ObisGroupModel(
                17,
                "General",
                "",
                15,
                true,
                "1.0.81.7.2.255",
                "Register",
                "Phase Angle 3"
            ),
            ObisGroupModel(
                18,
                "General",
                "",
                15,
                true,
                "1.0.0.4.2.255",
                "Data",
                "Internal CT Ratio"
            ),
            ObisGroupModel(
                19,
                "General",
                "",
                15,
                true,
                "1.0.0.4.3.255",
                "Data",
                "Internal PT Ratio"
            ),
            ObisGroupModel(
                20,
                "General",
                "",
                15,
                true,
                "1.0.0.8.0.255",
                "Data",
                "Demand Integration Period"
            ),
            ObisGroupModel(
                21,
                "General",
                "",
                15,
                true,
                "0.0.42.0.0.255",
                "Data",
                "Logical Device Name"
            ),
            ObisGroupModel(
                22,
                "General",
                "",
                15,
                true,
                "0.0.94.91.11.255",
                "Data",
                "Meter Category"
            ),
            ObisGroupModel(
                23,
                "General",
                "",
                15,
                true,
                "1.0.0.8.4.255",
                "Data",
                "Load Interval Period"
            ),
            ObisGroupModel(
                24,
                "General",
                "",
                15,
                true,
                "0.0.94.91.12.255",
                "Data",
                "Current Rating"
            )
        )
    }


    fun obisGroup(): ArrayList<OBIS> {
        val obisGroupMaster = ObisGroupMaster() // Replace with actual function to fetch OBIS Group Master

        return ArrayList(obisGroupMaster.map { s ->
            OBIS(
                s.PROFILER_GROUP,
                s.OBIS_SCALER,
                s.PROFILER_CODE,
                s.OBIS_VALUE,
                s.OBJECT_TYPE,
                s.DESCRIPTION
            )
        })
    }




}
