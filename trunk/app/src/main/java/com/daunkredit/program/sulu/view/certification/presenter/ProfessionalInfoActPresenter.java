package com.daunkredit.program.sulu.view.certification.presenter;

import android.widget.TextView;

import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenter;
import com.daunkredit.program.sulu.bean.EmploymentBean;
import com.daunkredit.program.sulu.common.InfoAdapterString;

/**
 * @作者:My
 * @创建日期: 2017/6/21 14:09
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface ProfessionalInfoActPresenter extends BaseActivityPresenter {

    InfoAdapterString getCompanyProvinceAdapter(EmploymentBean employmentBean);

    InfoAdapterString getCompanyCityAdapter(EmploymentBean employmentBean);

    InfoAdapterString getCompanyStreetAdapter(EmploymentBean employmentBean);

    InfoAdapterString getCompanyAreaAdapter(EmploymentBean employmentBean);

    void uploadJobInfo(EmploymentBean employmentBean,TextView companyStreet);
}
