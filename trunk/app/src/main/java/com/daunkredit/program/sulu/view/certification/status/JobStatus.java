package com.daunkredit.program.sulu.view.certification.status;

import com.daunkredit.program.sulu.R;

/**
 * @作者:My
 * @创建日期: 2017/3/24 12:07
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public enum JobStatus implements InfoValueType {
    ACCOUNTING("ACCOUNTING", R.string.enum_job_accounting),
    WAITER("WAITER", R.string.enum_job_waiter),
    ENGINEER("ENGINEER", R.string.enum_job_engineer),
    EXECUTIVE("EXECUTIVE",R.string.enum_job_executive),
    GENERAL_ADMINISTRATION("GENERAL_ADMINISTRATION",R.string.enum_job_general_administration),
    INFORMATION_TECHNOLOGY("INFORMATION_TECHNOLOGY",R.string.enum_job_information_technology),
    CONSULTANT("CONSULTANT",R.string.enum_job_consultant),
    MARKETING("MARKETING",R.string.enum_job_marketing),
    TEACHER("TEACHER", R.string.enum_job_teacher),
    MILITARY("MILITARY",R.string.enum_job_military),
    RETIRED("RETIRED",R.string.enum_job_retired),
    STUDENT("STUDENT",R.string.enum_job_student),
    ENTREPRENEUR("ENTREPRENEUR",R.string.enum_job_entrepreneur),
    POLICE("POLICE",R.string.enum_job_police),
    FARMER("FARMER",R.string.enum_job_farmer),
    FISHERMAN("FISHERMAN",R.string.enum_job_fisherman),
    BREEDER("BREEDER",R.string.enum_job_breeder),
    DOCTOR("DOCTOR", R.string.enum_job_doctor),
    MEDICAL_PERSONNEL("MEDICAL_PERSONNEL",R.string.enum_job_medical_personal),
    LAWYER("LAWYER", R.string.enum_job_lawyer),
    CHEF("CHEF", R.string.enum_job_chef),
    RESEARCHER("RESEARCHER",R.string.enum_job_research),
    DESIGNER("DESIGNER", R.string.enum_job_designer),
    ARCHITECT("ARCHITECT",R.string.enum_job_architect),
    WORKERS_ART("WORKERS_ART",R.string.enum_job_workers_art),
    SECURITY("SECURITY",R.string.enum_job_security),
    BROKER("BROKER",R.string.enum_job_broker),
    DISTRIBUTOR("DISTRIBUTOR",R.string.enum_job_distributor),
    AIR_TRANSPORTATION("AIR_TRANSPORTATION",R.string.enum_job_air_transportation),
    SEA_TRANSPORTATION("SEA_TRANSPORTATION",R.string.enum_job_sea_transportation),
    LAND_TRANSPORTATION("LAND_TRANSPORTATION",R.string.enum_job_land_transportation),
    LABOR("LABOR",R.string.enum_job_labor),
    CRAFTSMAN("CRAFTSMAN",R.string.enum_job_craftsman),
    HOUSEWIFE("HOUSEWIFE",R.string.enum_job_housewife),
    STATE_OFFICIALS("STATE_OFFICIALS",R.string.enum_job_state_officials),
    GOVERNMENT_EMPLOYEE("GOVERNMENT_EMPLOYEE",R.string.enum_job_government_employment),
    INFORMAL_WORKERS("INFORMAL_WORKERS",R.string.enum_job_information_worker),
    OTHER("OTHER", R.string.enum_job_other);


    private final int mStrigId;
    private final String mValue;

    JobStatus(String value, int stringId) {
        mValue = value;
        mStrigId = stringId;
    }

    @Override
    public String getValue() {
        return mValue;
    }

    @Override
    public int getShowString() {
        return mStrigId;
    }
    }
