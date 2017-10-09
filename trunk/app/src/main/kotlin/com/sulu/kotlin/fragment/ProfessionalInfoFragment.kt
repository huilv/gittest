package com.sulu.kotlin.fragment

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.common.StringUtil
import com.daunkredit.program.sulu.R
import com.daunkredit.program.sulu.app.FieldParams
import com.daunkredit.program.sulu.app.base.BaseFragment
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenter
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenterImpl
import com.daunkredit.program.sulu.bean.EmploymentBean
import com.daunkredit.program.sulu.bean.EmploymentServerBean
import com.daunkredit.program.sulu.bean.RegionBean
import com.daunkredit.program.sulu.common.InfoAdapter
import com.daunkredit.program.sulu.common.InfoAdapterEnum
import com.daunkredit.program.sulu.common.TokenManager
import com.daunkredit.program.sulu.harvester.stastic.ActionType
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue
import com.daunkredit.program.sulu.view.certification.ProfessionalInfoActivity
import com.daunkredit.program.sulu.view.certification.status.InfoType
import com.daunkredit.program.sulu.view.certification.status.JobStatus
import com.daunkredit.program.sulu.view.certification.status.RegionLevel
import com.daunkredit.program.sulu.view.certification.status.SalaryStatus
import com.daunkredit.program.sulu.widget.droplistview.DrapListViewAdapter
import com.daunkredit.program.sulu.widget.droplistview.XLeoDropListView
import com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager
import com.daunkredit.program.sulu.widget.selfdefedittext.OnCheckInputResultAdapter
import com.daunkredit.program.sulu.widget.selfdefedittext.XLeoEditText
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast
import com.hwangjr.rxbus.RxBus
import com.jakewharton.rxbinding.widget.RxTextView
import com.orhanobut.logger.Logger
import okhttp3.ResponseBody
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action0
import rx.schedulers.Schedulers

/**
 * @作者:XLEO
 * @创建日期: 2017/8/15 10:01
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class ProfessionalInfoFragment : BaseFragment<ProfessionalInfoPresenter>(), ProfessionalInfoView {

    lateinit var jobType: TextView
    lateinit var salary: TextView
    lateinit var companyName: XLeoEditText

    lateinit var companyAddress: XLeoEditText
    lateinit var companyTel: XLeoEditText
    lateinit var jobInfoSubmitButton: Button
    lateinit var mbdlv: XLeoDropListView
    lateinit var region: TextView
    val token = TokenManager.getInstance().token
    lateinit var dialogPlus: Dialog
    var employmentBean: EmploymentBean = EmploymentBean()

    private var inputCheckListener: OnCheckInputResultAdapter? = null
    private var mTelPreStrings: Array<String> = arrayOf("021", "0251")
    private var mAdapter: DrapListViewAdapter? = null

    override fun doPreBuildHeader(): Boolean {
        return true
    }

    override fun initHeader(view: TextView?): Boolean {
        view!!.text = getString(R.string.textview_field_professional_info)
        return true
    }

    override fun getBackPressListener(): View.OnClickListener {
        return object : View.OnClickListener {
            override fun onClick(v: View?) {
                mActivity.finish()
            }
        }
    }

    override fun initPresenter(): ProfessionalInfoPresenter {
        return ProfessionalInfoPresenterImpl()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_professional_info
    }

    private var hasSave: Boolean = false

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        findAndInit(view!!)
        RxBus.get().register(this)
        jobType.requestFocus()
        initWorkTelPre()
        initInputCheck()
        editTextChangeEvent()
        if (hasSave && savedInstanceState != null) {
            initViewBySavedState(savedInstanceState!!)
        }
        updateButtonState()
    }

    //    lateinit var jobType: TextView
//    lateinit var salary: TextView
//    lateinit var companyName: XLeoEditText
//
//    lateinit var companyAddress: XLeoEditText
//    lateinit var companyTel: XLeoEditText
//    lateinit var jobInfoSubmitButton: Button
//    lateinit var mbdlv: XLeoDropListView
//    lateinit var region: TextView
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putCharSequence(FieldParams.ProfessionInfo.JOBTYPE, jobType.text)
            outState.putCharSequence(FieldParams.ProfessionInfo.SALARY, salary.text)
            outState.putCharSequence(FieldParams.ProfessionInfo.COMPANYNAME, companyName.text?.toString())
            outState.putCharSequence(FieldParams.ProfessionInfo.COMPANYADDRESS, companyAddress.text?.toString())
            outState.putCharSequence(FieldParams.ProfessionInfo.COMPANYTEL, companyTel.text?.toString())
            outState.putCharSequence(FieldParams.ProfessionInfo.COMPANYTELPRE, mbdlv.selectedItem)
            outState.putCharSequence(FieldParams.ProfessionInfo.REGION, region.text)
            hasSave = true
        }
    }

    private fun initViewBySavedState(state: Bundle) {
        jobType.text = state[FieldParams.ProfessionInfo.JOBTYPE] as CharSequence
        salary.text = state[FieldParams.ProfessionInfo.SALARY] as CharSequence
        companyName.setText(state[FieldParams.ProfessionInfo.COMPANYNAME] as CharSequence)
        companyTel.setText(state[FieldParams.ProfessionInfo.COMPANYTEL] as CharSequence)
        val indexOf = mTelPreStrings.indexOf(state[FieldParams.ProfessionInfo.COMPANYTELPRE]?.toString())
        mbdlv.setSelectedItem(if (indexOf <= -1 || indexOf >= mTelPreStrings.size) {
            0
        } else {
            indexOf
        })
        region.text = state[FieldParams.ProfessionInfo.REGION] as CharSequence
        val needUpdate = arguments[FieldParams.NEED_UPDATE_REGION]
        if (needUpdate != null && needUpdate as Boolean) {
            updateRegion()
            arguments.putBoolean(FieldParams.NEED_UPDATE_REGION, false)
        }
        companyAddress.setText(state[FieldParams.ProfessionInfo.COMPANYADDRESS] as CharSequence)
    }

    override fun onProfessionDataObtain(employmentServerBean: EmploymentServerBean) {
        if (employmentServerBean != null) {
            employmentBean = EmploymentBean(employmentServerBean)
            jobType.setText(getString(JobStatus.valueOf(employmentBean.jobType).showString))
            salary.setText(getString(SalaryStatus.valueOf(employmentBean.getSalary()).showString))
            companyName.setText(employmentBean.getCompanyName())
            initRegion()
            var companyPhone = employmentBean.getCompanyPhone()
            for (i in 0..mTelPreStrings.size - 1) {
                if (companyPhone.startsWith(mTelPreStrings[i])) {
                    mbdlv.setSelectedItem(i)
                    companyPhone = companyPhone.substring(mTelPreStrings[i].length)
                    break
                }
            }
            companyTel.setText(companyPhone)

        }

        updateButtonState()
        val needUpdate = arguments[FieldParams.NEED_UPDATE_REGION]
        if (needUpdate != null && needUpdate as Boolean) {
            updateRegion()
            arguments.putBoolean(FieldParams.NEED_UPDATE_REGION, false)
        }
    }

    private fun updateRegion() {
        val regionDatas = HashMap<String, RegionBean.RegionsBean>()
        val level = (mActivity as ProfessionalInfoActivity).getRegionDatas(regionDatas)
        when (level) {
            RegionLevel.province.ordinal -> {
                val province = regionDatas.get(RegionLevel.province.toString())
                if (employmentBean.companyProvince?.id != province!!.id) {
                    employmentBean.companyProvince = province
                    employmentBean.companyArea = null
                    employmentBean.companyDistrict = null
                    employmentBean.companyCity = null
                    employmentBean.companyAddress = null
                }
            }
            RegionLevel.city.ordinal -> {
                val province = regionDatas.get(RegionLevel.province.toString())
                val city = regionDatas.get(RegionLevel.city.toString())
                if (employmentBean.companyProvince?.id != province!!.id) {
                    employmentBean.companyProvince = province
                    employmentBean.companyCity = city
                    employmentBean.companyArea = null
                    employmentBean.companyDistrict = null
                    employmentBean.companyAddress = null
                } else if (employmentBean.companyCity?.id != city!!.id) {
                    employmentBean.companyCity = city
                    employmentBean.companyArea = null
                    employmentBean.companyDistrict = null
                    employmentBean.companyAddress = null
                }
            }
            RegionLevel.district.ordinal -> {
                val province = regionDatas.get(RegionLevel.province.toString())
                val city = regionDatas.get(RegionLevel.city.toString())
                val district = regionDatas.get(RegionLevel.district.toString())
                if (employmentBean.companyProvince?.id != province!!.id) {
                    employmentBean.companyProvince = province
                    employmentBean.companyCity = city
                    employmentBean.companyArea = null
                    employmentBean.companyDistrict = district
                    employmentBean.companyAddress = null
                } else if (employmentBean.companyCity?.id != city!!.id) {
                    employmentBean.companyCity = city
                    employmentBean.companyArea = null
                    employmentBean.companyDistrict = district
                    employmentBean.companyAddress = null
                } else if (employmentBean.companyDistrict?.id != district!!.id) {
                    employmentBean.companyArea = null
                    employmentBean.companyDistrict = district
                    employmentBean.companyAddress = null
                }
            }
            RegionLevel.area.ordinal -> {
                val province = regionDatas.get(RegionLevel.province.toString())
                val city = regionDatas.get(RegionLevel.city.toString())
                val district = regionDatas.get(RegionLevel.district.toString())
                val area = regionDatas.get(RegionLevel.area.toString())
                if (employmentBean.companyProvince?.id != province!!.id) {
                    employmentBean.companyProvince = province
                    employmentBean.companyCity = city
                    employmentBean.companyArea = area
                    employmentBean.companyDistrict = district
                    employmentBean.companyAddress = null
                } else if (employmentBean.companyCity?.id != city!!.id) {
                    employmentBean.companyCity = city
                    employmentBean.companyArea = area
                    employmentBean.companyDistrict = district
                    employmentBean.companyAddress = null
                } else if (employmentBean.companyDistrict?.id != district!!.id) {
                    employmentBean.companyArea = area
                    employmentBean.companyDistrict = district
                    employmentBean.companyAddress = null
                } else if (employmentBean.companyArea?.id != area!!.id) {
                    employmentBean.companyArea = area
                    employmentBean.companyAddress = null
                }
            }
            else -> {
                throw IllegalArgumentException("wrong argument of region datas")
            }
        }
        initRegion()
    }

    private fun initRegion() {
        region.text = employmentBean.companyProvince.name + "-" + employmentBean.companyCity.name + "-" + employmentBean.companyDistrict.name + "-" + employmentBean.companyArea.name
        companyAddress.setText(employmentBean.companyAddress)
        updateButtonState()
    }

    private fun findAndInit(view: View) {
        companyAddress = view.find(R.id.id_edittext_job_info_address)
        jobType = view.find(R.id.id_textview_job_info_job_type)
        jobType.onClick {
            setJobType()
        }
        salary = view.find(R.id.id_textview_job_info_salary)
        salary.onClick {
            setSalary()
        }
        companyName = view.find(R.id.id_edittext_job_info_conmany_name)
        companyTel = view.find(R.id.id_edittext_job_info_tel)
        jobInfoSubmitButton = view.find(R.id.id_button_job_info_submit)
        jobInfoSubmitButton.onClick {
            jobInfoSubmit()
        }
        mbdlv = view.find(R.id.bdlv_job_info_telpre)
        region = view.find(R.id.id_textview_job_info_region)
        region.onClick {
            getRegion()
        }
    }

    private fun getRegion() {
        (mActivity as ProfessionalInfoActivity).forwardToNextFragment(null)
    }

    override fun initData() {
        if (hasSave) {
            hasSave = false
        } else {
            mPresenter.obtainProfessionData()
        }
    }

    private fun initWorkTelPre() {
        mTelPreStrings = arrayOf("021", "0251")
        mAdapter = DrapListViewAdapter(mbdlv, R.layout.my_simple_spinner_item, mTelPreStrings)
        //mAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown_item);
        //mbdlv.setEmptyView(mTvTel);
        mbdlv.setAdapter(mAdapter)
        mbdlv.setSelectedItem(0)
    }


    // EditText listener
    private fun editTextChangeEvent() {
        val observableCompanyName = RxTextView.textChanges(companyName!!)
        val observableCompanyAddress = RxTextView.textChanges(companyAddress!!)
        val observableCompanyTel = RxTextView.textChanges(companyTel)
//        charSequence, charSequence2, charSequence3 -> !StringUtil.isNullOrEmpty(charSequence.toString()) || !StringUtil.isNullOrEmpty(charSequence2.toString()) || !StringUtil.isNullOrEmpty(charSequence3.toString())
        Observable.combineLatest(observableCompanyName, observableCompanyAddress, observableCompanyTel) { charSequence, charSequence2, charSequence3 -> true }.subscribe { updateButtonState() }
    }


    //update submit button
    private fun updateButtonState() {
        if (isCheckedField()) {
            jobInfoSubmitButton.setClickable(true)
            jobInfoSubmitButton.setAlpha(0.8f)
        } else {
            jobInfoSubmitButton.setClickable(false)
            jobInfoSubmitButton.setAlpha(0.3f)
        }
    }

    private fun initInputCheck() {
        inputCheckListener = object : OnCheckInputResultAdapter() {
            override fun onCheckResult(v: EditText): Boolean {
                val text = v.text.toString()
                if (v === companyName) {
                    if (TextUtils.isEmpty(text)) {
                        return false
                    }
                } else if (v === companyAddress) {
                    if (TextUtils.isEmpty(text)) {
                        return false
                    }
                } else if (v === companyTel) {
                    if (TextUtils.isEmpty(text) || !text.matches("\\d{1,10}".toRegex())) {
                        return false
                    }
                } else {

                }
                return true
            }
        }
        companyName.setOnCheckInputResult(inputCheckListener)
        companyAddress.setOnCheckInputResult(inputCheckListener)
        companyTel.setOnCheckInputResult(inputCheckListener)

    }

    /**
     * Set JobType adapter;

     * @return
     */
    fun getJobTypeAdapter(): InfoAdapter {
        val jobTypeAdapter = InfoAdapterEnum(context)
        jobTypeAdapter.addItem(JobStatus.ACCOUNTING, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.WAITER, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.ENGINEER, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.EXECUTIVE, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.GENERAL_ADMINISTRATION, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.INFORMATION_TECHNOLOGY, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.CONSULTANT, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.MARKETING, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.TEACHER, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.MILITARY, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.RETIRED, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.STUDENT, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.ENTREPRENEUR, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.POLICE, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.FARMER, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.FISHERMAN, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.BREEDER, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.DOCTOR, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.MEDICAL_PERSONNEL, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.LAWYER, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.CHEF, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.RESEARCHER, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.DESIGNER, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.ARCHITECT, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.WORKERS_ART, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.SECURITY, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.BROKER, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.DISTRIBUTOR, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.AIR_TRANSPORTATION, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.SEA_TRANSPORTATION, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.LAND_TRANSPORTATION, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.LABOR, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.CRAFTSMAN, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.HOUSEWIFE, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.STATE_OFFICIALS, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.GOVERNMENT_EMPLOYEE, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.INFORMAL_WORKERS, InfoType.JOBTYPE)
        jobTypeAdapter.addItem(JobStatus.OTHER, InfoType.JOBTYPE)
        jobTypeAdapter.setOnItemClickListener(object : OnInfoAdapterItemClickListener {
            override fun onItemClick(data: InfoAdapter.InfoItem) {
                onJobTypeSelected(data)
            }

        })
        return jobTypeAdapter
    }

    /**


     * BELOW_2B("below 2,000,000", "小于2,000,000", ""),
     * BETWEEN_2B_4B("between 2,000,000 and 4,000,000", "2,000,000-4,000,000之间", ""),
     * BETWEEN_4B_8B("between 4,000,000 and 8,000,000", "4,000,000-8,000,000之间", ""),
     * BETWEEN_8B_12B("between 8,000,000 and 12,000,000", "8,000,000-12,000,000之间", ""),
     * BETWEEN_12B_20B("between 12,000,000 and 20,000,000", "12,000,000-20,000,000之间", ""),
     * OVER_20B("over 20,000,000", "20,000,000以上", "");
     * @return
     */

    fun getSalaryAdapter(): InfoAdapter {
        val salaryAdapter = InfoAdapterEnum(context)
        salaryAdapter.addItem(SalaryStatus.BELOW_2B, InfoType.SALARY)
        salaryAdapter.addItem(SalaryStatus.BETWEEN_2B_4B, InfoType.SALARY)
        salaryAdapter.addItem(SalaryStatus.BETWEEN_4B_8B, InfoType.SALARY)
        //        salaryAdapter.addItem(SalaryStatus.BETWEEN_12B_20B, InfoType.SALARY);
        salaryAdapter.addItem(SalaryStatus.OVER_8B, InfoType.SALARY)
        salaryAdapter.setOnItemClickListener(object : OnInfoAdapterItemClickListener {
            override fun onItemClick(data: InfoAdapter.InfoItem) {
                onSalarySelected(data)
            }

        })
        return salaryAdapter
    }

    fun onJobTypeSelected(data: InfoAdapter.InfoItem) {
        if (data.type == InfoType.JOBTYPE) {
            if (dialogPlus != null && dialogPlus.isShowing()) {
                dialogPlus.dismiss()
            }
            jobType.setText(data.infoStr)
            employmentBean.jobType = data.valueStr
            updateButtonState()
            Logger.d("job type: " + data.infoStr + data.valueStr + employmentBean.jobType)
        }
    }

    fun onSalarySelected(data: InfoAdapter.InfoItem) {
        if (data.type == InfoType.SALARY) {
            if (dialogPlus != null && dialogPlus.isShowing()) {
                dialogPlus.dismiss()
            }
            salary.setText(data.infoStr)
            employmentBean.salary = data.valueStr
            updateButtonState()
        }
    }

    fun isCheckedField(): Boolean {
        if (StringUtil.isNullOrEmpty(jobType.getText().toString())) {
            //            ToastManager.showToast(getString(R.string.show_input_job_type));
            return false
        }
        if (StringUtil.isNullOrEmpty(salary.getText().toString())) {
            //            ToastManager.showToast(getString(R.string.show_input_salary));
            return false
        }
        if (StringUtil.isNullOrEmpty(companyName.getText().toString())) {
            //            ToastManager.showToast(getString(R.string.show_input_company_name));
            return false
        }
        if (employmentBean.companyProvince?.name == null) {
            //            ToastManager.showToast(getString(R.string.show_input_company_province));
            return false
        }
        if (employmentBean.companyCity?.name == null) {
            //            ToastManager.showToast(getString(R.string.show_input_company_city));
            return false
        }
        if (employmentBean.companyDistrict?.name == null) {
            //            ToastManager.showToast(getString(R.string.show_input_company_street));
            return false
        }
        if (employmentBean.companyArea?.name == null) {
            //            ToastManager.showToast(getString(R.string.show_input_company_area));
            return false
        }

        if (StringUtil.isNullOrEmpty(companyAddress.text?.toString())) {
            //            ToastManager.showToast(getString(R.string.show_input_company_address));
            return false
        }

        return !StringUtil.isNullOrEmpty(companyTel.text?.toString())

    }


    fun setJobType() {
        val jobTypeAdapter = getJobTypeAdapter() as InfoAdapterEnum
        dialogPlus = DialogManager.newListViewDialog(context)
                .setAdapter(jobTypeAdapter)
                .setExpanded(false)
                .setGravity(Gravity.CENTER)
                .create()
        dialogPlus.show()
    }

    fun setSalary() {
        val salaryAdapter = getSalaryAdapter() as InfoAdapterEnum
        dialogPlus = DialogManager.newListViewDialog(context)
                .setAdapter(salaryAdapter)
                .setExpanded(false)
                .setGravity(Gravity.CENTER)
                .create()
        dialogPlus.show()
    }

    fun jobInfoSubmit() {
        UserEventQueue.add(ClickEvent(jobInfoSubmitButton.toString(), ActionType.CLICK, jobInfoSubmitButton.getText().toString()))
        if (isCheckedField()) {
            //            String position = mbdlv.getSelectedItem();
            //            if (position == null) {
            //                ToastManager.showToast(getString(R.string.show_input_areacode));
            //                return;
            //            }
            mActivity.showLoading(resources.getText(R.string.show_uploading).toString())
            employmentBean.companyName = if (companyName.getText() == null) null else companyName.getText().toString().trim()
            employmentBean.companyPhone = mbdlv.getText().toString() + if (companyTel.getText() != null) companyTel.getText().toString().trim() else ""
            employmentBean.companyAddress = if (companyAddress.getText() == null) null else companyAddress.getText().toString().trim()
            mPresenter.uploadJobInfo(employmentBean)
        }else{
            updateButtonState()
        }
    }

}

class ProfessionalInfoPresenterImpl : ProfessionalInfoPresenter, BaseFragmentPresenterImpl() {
    override fun obtainProfessionData() {
        mUserApi.getEmploymentInfo(TokenManager.getInstance().token)
                .doOnSubscribe(Action0 {
                    if (isAttached) {
                        mView.baseActivity.runOnUiThread {
                            mView.baseActivity.showLoading(null)
                        }
                    }

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<EmploymentServerBean>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {

                        if (isAttached) {
                            mView.baseActivity.dismissLoading()
                        }
                        Logger.d("Get EmployeeInfo failed: " + e.message)
                        XLeoToast.showMessage(R.string.show_get_employeeinfo_failed)

                    }

                    override fun onNext(employmentServerBean: EmploymentServerBean) {

                        if (isAttached) {
                            mView.baseActivity.dismissLoading()
                            (mView as ProfessionalInfoView).onProfessionDataObtain(employmentServerBean)
                        }
                    }
                })
    }

    override fun uploadJobInfo(employmentBean: EmploymentBean) {
//        @Field("companyName") String companyName,
//        @Field("companyProvince") String companyProvince,
//        @Field("companyCity") String companyCity,
//        @Field("companyDistrict") String companyDistrict,
//        @Field("companyArea") String companyArea,
//        @Field("companyAddress") String companyAddress,
//        @Field("companyPhone") String companyPhone,
//        @Field("profession") String profession,
//        @Field("salary") String salary,
        mUserApi.submitEmploymentInfo(employmentBean.companyName, employmentBean.companyProvince.name, employmentBean.companyCity.name, employmentBean.companyDistrict.name, employmentBean.companyArea.name, employmentBean.companyAddress, employmentBean.companyPhone, employmentBean.jobType, employmentBean.salary, TokenManager.getInstance().token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ResponseBody>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
//                        Toast.makeText(mView.baseActivity, e.message, Toast.LENGTH_SHORT).show()
                        XLeoToast.showMessage(R.string.show_get_region_faild)
                        Logger.d("submit personall info failed.")
                        if (!isAttached) {
                            return
                        }
                        mView.baseActivity.dismissLoading()

                    }

                    override fun onNext(responseBody: ResponseBody) {
                        Logger.d("Submit success!")
                        if (!isAttached) {
                            return
                        }
                        mView.baseActivity.setResult(Activity.RESULT_OK)
                        mView.baseActivity.dismissLoading()
                        mView.baseActivity.finish()

                    }
                })
    }

}

interface ProfessionalInfoView {
    fun onProfessionDataObtain(employmentServerBean: EmploymentServerBean)

}

interface ProfessionalInfoPresenter : BaseFragmentPresenter {
    fun uploadJobInfo(employmentBean: EmploymentBean)
    fun obtainProfessionData()

}
