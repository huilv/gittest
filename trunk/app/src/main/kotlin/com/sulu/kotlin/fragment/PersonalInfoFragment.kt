package com.sulu.kotlin.fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.text.Editable
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
import com.daunkredit.program.sulu.bean.GeoLocationBean
import com.daunkredit.program.sulu.bean.PersonalInfoBean
import com.daunkredit.program.sulu.bean.PersonalInfoServerBean
import com.daunkredit.program.sulu.bean.RegionBean
import com.daunkredit.program.sulu.common.InfoAdapter
import com.daunkredit.program.sulu.common.InfoAdapterEnum
import com.daunkredit.program.sulu.common.InfoAdapterString
import com.daunkredit.program.sulu.common.TokenManager
import com.daunkredit.program.sulu.common.network.GeoLocationApi
import com.daunkredit.program.sulu.common.utils.LoggerWrapper
import com.daunkredit.program.sulu.harvester.stastic.ActionType
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue
import com.daunkredit.program.sulu.view.certification.CheckInFacebookActivity
import com.daunkredit.program.sulu.view.certification.PersonalInfoActView
import com.daunkredit.program.sulu.view.certification.PersonalInfoActivity
import com.daunkredit.program.sulu.view.certification.status.*
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
 * @创建日期: 2017/8/14 10:01
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class PersonalInfoFragment : BaseFragment<PersonalInfoPresenter>(), PersonalInfoView {

    lateinit var fullName: XLeoEditText
    lateinit var ktpno: XLeoEditText
    lateinit var gender: TextView
    lateinit var education: TextView
    lateinit var marital: TextView
    lateinit var childrenAccount: TextView
    lateinit var region: TextView
    lateinit var address: EditText
    lateinit var duration: TextView
    lateinit var pInfoSummit: Button
    lateinit var dialogPlus: Dialog

    var personalInfoBean: PersonalInfoBean = PersonalInfoBean()
    lateinit var mTvPersonalInfoFacebookId: TextView

    private var hasSavedState: Boolean = false


    lateinit var familyNameInLaw: EditText

    private val mRequestCode = 1222

    override fun doPreBuildHeader(): Boolean {
        return true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if(outState != null){
           outState.putCharSequence(FieldParams.PersonInfo.FULLNAME,fullName.text.toString())
            outState.putCharSequence(FieldParams.PersonInfo.KTP_NO,ktpno.text.toString())
            outState.putCharSequence(FieldParams.PersonInfo.GENDER,gender.text)
            outState.putCharSequence(FieldParams.PersonInfo.EDUCATION,education.text)
            outState.putCharSequence(FieldParams.PersonInfo.MARITAL,marital.text)
            outState.putCharSequence(FieldParams.PersonInfo.CHILDRENACCOUNT,childrenAccount.text)
            outState.putCharSequence(FieldParams.PersonInfo.REGION,region.text)
            outState.putCharSequence(FieldParams.PersonInfo.ADDRESS,address.text)
            outState.putCharSequence(FieldParams.PersonInfo.DURATION,duration.text)
            outState.putCharSequence(FieldParams.PersonInfo.FAMILYNAMEINLAW,familyNameInLaw.text.toString())
            hasSavedState = true
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }
    override fun initHeader(view: TextView?): Boolean {
        view?.setText(R.string.textview_title_personal_info)
        return true
    }

    override fun initPresenter(): PersonalInfoPresenter {
        return PersonalInfoPresenterImpl()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_personal_info
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        RxBus.get().register(context)
        //geoRequest();
        initFind(view!!)
        initInputCheck()
        editTextChangeEvent()
        if (hasSavedState && savedInstanceState != null) {
            initViewByState(savedInstanceState!!)
        }
        updateSubmitSate()
    }

    private fun initViewByState(state: Bundle) {
        fullName.setText(state[FieldParams.PersonInfo.FULLNAME] as CharSequence)
        ktpno.setText(state[FieldParams.PersonInfo.KTP_NO] as CharSequence)
        gender.text = state[FieldParams.PersonInfo.GENDER] as CharSequence
        familyNameInLaw.setText(state[FieldParams.PersonInfo.FAMILYNAMEINLAW] as CharSequence)
        education.text = state[FieldParams.PersonInfo.EDUCATION] as CharSequence
        marital.text = state[FieldParams.PersonInfo.MARITAL] as CharSequence
        childrenAccount.text = state[FieldParams.PersonInfo.CHILDRENACCOUNT] as CharSequence
        region.text = state[FieldParams.PersonInfo.REGION] as CharSequence

        duration.text = state[FieldParams.PersonInfo.DURATION] as CharSequence
        val any = arguments[FieldParams.NEED_UPDATE_REGION]
        if(any != null && any as Boolean){
            handleRegionChange()
            arguments.putBoolean(FieldParams.NEED_UPDATE_REGION,false)
        }
        address.setText(state[FieldParams.PersonInfo.ADDRESS] as CharSequence)
    }

    private fun initFind(view: View) {
        familyNameInLaw = view.find(R.id.id_edittext_personal_onfo_family_name_in_law)
        mTvPersonalInfoFacebookId = view.find(R.id.tv_personal_info_facebook_id)
        fullName = view.find(R.id.id_edittext_personal_info_fullname)
        ktpno = view.find(R.id.id_edittext_personal_info_ktp_no)
        gender = view.find(R.id.id_textview_persoanl_info_gender)
        gender.onClick { setGender() }
        education = view.find(R.id.id_textview_personal_info_education)
        education.onClick { setEducation() }
        marital = view.find(R.id.id_textview_personal_info_marital_status)
        marital.onClick { setMarital() }
        childrenAccount = view.find(R.id.id_textview_personal_info_number_of_children)
        childrenAccount.onClick { setChildrenAccount() }
        region = view.find(R.id.id_textview_personal_info_region)
        region.onClick { getRegion() }
        address = view.find(R.id.id_edittext_personal_onfo_address)
        duration = view.find(R.id.id_textview_personal_info_duration)
        duration.onClick { setDuration() }
        pInfoSummit = view.find(R.id.id_button_personal_info_submit)
        pInfoSummit.onClick {
            personalInofSubmit()
        }
    }

    private fun getRegion() {
        (mActivity as PersonalInfoActivity).forwardToNextFragment(null)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            handleRegionChange()
        }
    }

    private fun handleRegionChange() {
        val regionDatas = HashMap<String, RegionBean.RegionsBean>()
        val level = (mActivity as PersonalInfoActivity).getRegionDatas(regionDatas)
        when (level) {
            RegionLevel.province.ordinal -> {
                val province = regionDatas.get(RegionLevel.province.toString())
                if (personalInfoBean.province?.id != province!!.id) {
                    personalInfoBean.setProvince(province)
                    personalInfoBean.area = null
                    personalInfoBean.district = null
                    personalInfoBean.city = null
                    personalInfoBean.address = null
                }
            }
            RegionLevel.city.ordinal -> {
                val province = regionDatas.get(RegionLevel.province.toString())
                val city = regionDatas.get(RegionLevel.city.toString())
                if (personalInfoBean.province?.id != province!!.id) {
                    personalInfoBean.province = province
                    personalInfoBean.city = city
                    personalInfoBean.area = null
                    personalInfoBean.district = null
                    personalInfoBean.address = null
                } else if (personalInfoBean.city?.id != city!!.id) {
                    personalInfoBean.city = city
                    personalInfoBean.area = null
                    personalInfoBean.district = null
                    personalInfoBean.address = null
                }
            }
            RegionLevel.district.ordinal -> {
                val province = regionDatas.get(RegionLevel.province.toString())
                val city = regionDatas.get(RegionLevel.city.toString())
                val district = regionDatas.get(RegionLevel.district.toString())
                if (personalInfoBean.province?.id != province!!.id) {
                    personalInfoBean.province = province
                    personalInfoBean.city = city
                    personalInfoBean.area = null
                    personalInfoBean.district = district
                    personalInfoBean.address = null
                } else if (personalInfoBean.city?.id != city!!.id) {
                    personalInfoBean.city = city
                    personalInfoBean.area = null
                    personalInfoBean.district = district
                    personalInfoBean.address = null
                } else if (personalInfoBean.district?.id != district!!.id) {
                    personalInfoBean.area = null
                    personalInfoBean.district = district
                    personalInfoBean.address = null
                }
            }
            RegionLevel.area.ordinal -> {
                val province = regionDatas.get(RegionLevel.province.toString())
                val city = regionDatas.get(RegionLevel.city.toString())
                val district = regionDatas.get(RegionLevel.district.toString())
                val area = regionDatas.get(RegionLevel.area.toString())
                if (personalInfoBean.province?.id != province!!.id) {
                    personalInfoBean.province = province
                    personalInfoBean.city = city
                    personalInfoBean.area = area
                    personalInfoBean.district = district
                    personalInfoBean.address = null
                } else if (personalInfoBean.city?.id != city!!.id) {
                    personalInfoBean.city = city
                    personalInfoBean.area = area
                    personalInfoBean.district = district
                    personalInfoBean.address = null
                } else if (personalInfoBean.district?.id != district!!.id) {
                    personalInfoBean.area = area
                    personalInfoBean.district = district
                    personalInfoBean.address = null
                } else if (personalInfoBean.area.id != area!!.id) {
                    personalInfoBean.area = area
                    personalInfoBean.address = null
                }
            }
            else -> {
                throw IllegalArgumentException("wrong argument of region datas")
            }
        }
        updateRegionInfo()
    }

    private fun updateRegionInfo() {
        region.text = personalInfoBean.province.name + "-" + personalInfoBean.city.name + "-" + personalInfoBean.district.name + "-" + personalInfoBean.area.name
        address.setText(personalInfoBean.address)
        updateSubmitSate()
    }

    override fun initData() {
        if (hasSavedState) {
            hasSavedState = false
        }else {
            mPresenter.obtainInitDatas()
        }
    }

    override fun reinitView(mPersonalInfoBean: PersonalInfoServerBean) {
        if (mPersonalInfoBean != null) {
            personalInfoBean = PersonalInfoBean(mPersonalInfoBean)
            fullName.setText(personalInfoBean.getFullName())
            ktpno.setText(personalInfoBean.getCredentialNo())
            familyNameInLaw.setText(personalInfoBean.getFamilyNameInLaw())
            gender.setText(getString(GenderStatus.valueOf(personalInfoBean.getGender()).showString))
            education.setText(personalInfoBean.getLastEducation())
            marital.setText(getString(MarriageStatus.valueOf(personalInfoBean.getMaritalStatus()).showString))
            childrenAccount.setText(getString(ChildrenNumStatus.valueOf(personalInfoBean.getChildrenNumber()).showString))
            region.text = personalInfoBean.getProvince().name + "-" + personalInfoBean.getCity().name + "-" + personalInfoBean.getDistrict().name + "-" + personalInfoBean.getArea().name
            address.setText(personalInfoBean.getAddress())
            duration.setText(getString(DurationStatus.valueOf(personalInfoBean.getResidenceDuration()).showString))
            if (personalInfoBean.getFacebookId() != null) {
                mTvPersonalInfoFacebookId.setText(personalInfoBean.getFacebookId())
            }
            updateSubmitSate()
        }
        val any = arguments[FieldParams.NEED_UPDATE_REGION]
        if(any != null && any as Boolean){
            handleRegionChange()
            arguments.putBoolean(FieldParams.NEED_UPDATE_REGION,false)
        }
    }

    private fun geoResultHandle(requestCode: Int, resultCode: Int) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1211) {
            //            Place place = PlacePicker.getPlace(data, context);
            //            String toastMsg = String.format("Place: %s", place.getName());
            //            LoggerWrapper.d(toastMsg);
            val obj = TokenManager.getInstance().getAndRemove(FieldParams.LOCATION_RESULT_BUNDLE)
            if (obj != null && obj is Bundle) {
                val bundle = obj
                val resultText = bundle.getString(FieldParams.LOCATION_RESULT)
                val resultBean = bundle.getSerializable(FieldParams.LOCATION_RESULT_GEO) as GeoLocationBean
                if (resultBean.results != null && resultBean.results.size > 0) {
                    val locationDetail = resultBean.results[0]
                    val address_components = locationDetail.address_components
                    if (address_components != null && address_components.size > 0) {
                        val size = address_components.size
//                        if (size > 2) {
//                            province.setText(address_components[size - 3].long_name)
//                        }
//                        if (size > 3) {
//                            city.setText(address_components[size - 4].long_name)
//                        }
//                        if (size > 4) {
//                            street.setText(address_components[size - 5].long_name)
//                        }
//                        if (size > 5) {
//                            area.setText(address_components[size - 6].long_name)
//                        }
                        region.text = if (size > 2) {
                            address_components[size - 3].long_name
                        } else {
                            ""
                        } + "-" + if (size > 3) {
                            address_components[size - 4].long_name
                        } else {
                            ""
                        } + "-" + if (size > 4) {
                            address_components[size - 5].long_name
                        } else {
                            ""
                        } + "-" +
                                if (size > 5) {
                                    address_components[size - 6].long_name
                                } else {
                                    ""
                                }
                        if (size > 6) {
                            address.setText(address_components[size - 7].long_name)
                        }
                    }
                }
            }
        }
    }

    private fun geoRequest() {
        view?.findViewById(R.id.btn_location_find)?.setOnClickListener(View.OnClickListener {
            //                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            //
            //                try {
            //                    startActivityForResult(builder.build(PersonalInfoActivity.context), 1211);
            //                } catch (Exception e) {
            //                    XLeoToast.showMessage(R.string.tips4google_service_not_available);
            //                }
//            startActivityForResult(Intent(context,  MapsActivity::class.java), 1211)
        })
        view?.findViewById(R.id.btn_location_now)?.setOnClickListener(View.OnClickListener { mPresenter.obtainLastLocation(context) })
    }


    private fun initInputCheck() {
        ktpno.setOnCheckInputResult(object : OnCheckInputResultAdapter() {
            override fun onCheckResult(v: EditText): Boolean {
                return checkInput(v)
            }

            override fun onTextChanged(v: EditText, s: Editable) {
                if (s.length > 16) {
                    v.setTextColor(resources.getColor(R.color.colorAlerm_red))
                } else {
                    v.setTextColor(resources.getColor(R.color.white))
                }
            }
        })
    }

    private fun checkInput(v: EditText): Boolean {
        val text = v.text ?: return false
        val result = text.toString()
        if (TextUtils.isEmpty(result)) {
            return false
        }
        if (v === ktpno) {
            if (result.length != 16) {
                return false
            }
            val substring = result.substring(6, 8)
            if (Integer.valueOf(substring) > 40) {
                gender.setText(R.string.enum_gender_female)
            } else {
                gender.setText(R.string.enum_gender_male)
            }
        }

        return true
    }


    private fun editTextChangeEvent() {

        val observableFullName = RxTextView.textChanges(fullName)
        val observableFullKtp = RxTextView.textChanges(ktpno)
        val observableFullAddress = RxTextView.textChanges(address)
        val observableFullFamilyNameInlaw = RxTextView.textChanges(familyNameInLaw)

        Observable.combineLatest(observableFullName, observableFullAddress, observableFullKtp, observableFullFamilyNameInlaw) { charSequence, charSequence2, charSequence3, charSequence4 -> !StringUtil.isNullOrEmpty(charSequence.toString()) || !StringUtil.isNullOrEmpty(charSequence2.toString()) || !StringUtil.isNullOrEmpty(charSequence3.toString()) || !StringUtil.isNullOrEmpty(charSequence4.toString()) }.subscribe { updateSubmitSate() }
    }


    /**
     * Set  gender adapter
     */
    fun getGenderAdapter(): InfoAdapter {
        val genderAdapter = InfoAdapterEnum(context)
        genderAdapter.addItem(GenderStatus.MALE, InfoType.GENDER)
        genderAdapter.addItem(GenderStatus.FEMALE, InfoType.GENDER)
        genderAdapter.setOnItemClickListener(object:OnInfoAdapterItemClickListener{
            override fun onItemClick(data: InfoAdapter.InfoItem) {
                if (dialogPlus != null && dialogPlus.isShowing()) {
                    dialogPlus.dismiss()
                }
                gender.setText(data.infoStr)
                personalInfoBean.setGender(data.valueStr)
                updateSubmitSate()
            }

        })

        return genderAdapter

    }

    override fun onDurationSelected(data: InfoAdapter.InfoItem) {
        if (dialogPlus != null && dialogPlus.isShowing()) {
            dialogPlus.dismiss()
        }
        duration.setText(data.infoStr)
        personalInfoBean.setResidenceDuration(data.valueStr)
        updateSubmitSate()
    }
    /**
     * Set education
     */
    fun getEducationAdapter(): InfoAdapterString {
        val educationAdapter = InfoAdapterString(context)
        educationAdapter.addItem("DIPLOMA_I", InfoType.EDUCATION)
        educationAdapter.addItem("DIPLOMA_II", InfoType.EDUCATION)
        educationAdapter.addItem("DIPLOMA_III", InfoType.EDUCATION)
        educationAdapter.addItem("SD", InfoType.EDUCATION)
        educationAdapter.addItem("SLTP", InfoType.EDUCATION)
        educationAdapter.addItem("SLTA", InfoType.EDUCATION)
        educationAdapter.addItem("S1", InfoType.EDUCATION)
        educationAdapter.addItem("S2", InfoType.EDUCATION)
        educationAdapter.addItem("S3", InfoType.EDUCATION)
        educationAdapter.setOnItemClickListener(object : OnInfoAdapterItemClickListener{
            override fun onItemClick(data: InfoAdapter.InfoItem) {
                if (dialogPlus != null && dialogPlus.isShowing()) {
                    dialogPlus.dismiss()
                }
                education.setText(data.infoStr)
                personalInfoBean.setLastEducation(data.infoStr)
                updateSubmitSate()
            }

        })
        return educationAdapter
    }

    /**
     * Set marital status
     */

    fun getMaritalStatusAdapter(): InfoAdapter {
        val maritalAdapter = InfoAdapterEnum(context)
        maritalAdapter.addItem(MarriageStatus.MARRIED, InfoType.MATRIAL)
        maritalAdapter.addItem(MarriageStatus.SINGLE, InfoType.MATRIAL)
        maritalAdapter.addItem(MarriageStatus.DIVORCED, InfoType.MATRIAL)
        maritalAdapter.addItem(MarriageStatus.WIDOWED, InfoType.MATRIAL)
        maritalAdapter.setOnItemClickListener(object :OnInfoAdapterItemClickListener{
            override fun onItemClick(data: InfoAdapter.InfoItem) {
                if (dialogPlus != null && dialogPlus.isShowing()) {
                    dialogPlus.dismiss()
                }
                marital.setText(data.infoStr)
                personalInfoBean.setMaritalStatus(data.valueStr)
                updateSubmitSate()
            }

        })
        return maritalAdapter
    }

    /**
     * Set children number
     */
    fun getChildrenAccountAdapter(): InfoAdapter {
        val childrenAccountAdapter = InfoAdapterEnum(context)
        childrenAccountAdapter.addItem(ChildrenNumStatus.ZERO, InfoType.CHILDREN)
        childrenAccountAdapter.addItem(ChildrenNumStatus.ONE, InfoType.CHILDREN)
        childrenAccountAdapter.addItem(ChildrenNumStatus.TWO, InfoType.CHILDREN)
        childrenAccountAdapter.addItem(ChildrenNumStatus.THREE, InfoType.CHILDREN)
        childrenAccountAdapter.addItem(ChildrenNumStatus.FOUR, InfoType.CHILDREN)
        childrenAccountAdapter.addItem(ChildrenNumStatus.OVER_FOUR, InfoType.CHILDREN)
        childrenAccountAdapter.setOnItemClickListener(object :OnInfoAdapterItemClickListener{
            override fun onItemClick(data: InfoAdapter.InfoItem) {
                if (dialogPlus != null && dialogPlus.isShowing()) {
                    dialogPlus.dismiss()
                }
                childrenAccount.setText(data.infoStr)
                personalInfoBean.setChildrenNumber(data.valueStr)
                updateSubmitSate()
            }
        })
        return childrenAccountAdapter
    }


    private fun updateSubmitSate() {
        if (isCheckedField()) {
            pInfoSummit.setClickable(true)
            pInfoSummit.setAlpha(0.8f)
        } else {
            pInfoSummit.setClickable(false)
            pInfoSummit.setAlpha(0.3f)
        }
    }

    fun isCheckedField(): Boolean {
        if (StringUtil.isNullOrEmpty(fullName.getText().toString())) {
            //ToastManager.showToast(getStringById(R.string.show_input_fullname));
            return false
        }
        if (StringUtil.isNullOrEmpty(ktpno.getText().toString())) {
            //ToastManager.showToast(getStringById(R.string.show_input_ktp_no));
            return false
        }
        if (StringUtil.isNullOrEmpty(familyNameInLaw.getText().toString())) {
            //ToastManager.showToast(getStringById(R.string.show_input_ktp_no));
            return false
        }

        if (StringUtil.isNullOrEmpty(gender.getText().toString())) {
            //ToastManager.showToast(getStringById(R.string.show_input_gender));
            return false
        }
        if (StringUtil.isNullOrEmpty(education.getText().toString())) {
            //ToastManager.showToast(getStringById(R.string.show_input_education));
            return false
        }
        if (StringUtil.isNullOrEmpty(marital.getText().toString())) {
            //ToastManager.showToast(getStringById(R.string.show_input_marital));
            return false
        }
        if (StringUtil.isNullOrEmpty(childrenAccount.getText().toString())) {
            //ToastManager.showToast(getStringById(R.string.show_input_account));
            return false
        }

        if (personalInfoBean.province?.name == null) {
            //ToastManager.showToast(getStringById(R.string.show_input_province));
            return false
        }
        if (personalInfoBean.city?.name== null) {
            //ToastManager.showToast(getStringById(R.string.show_input_city));
            return false
        }
        if (personalInfoBean.district?.name == null) {
            // ToastManager.showToast(getStringById(R.string.show_input_street));
            return false
        }
        if (personalInfoBean.area?.name == null) {
            //ToastManager.showToast(getStringById(R.string.show_input_area));
            return false
        }
        if (StringUtil.isNullOrEmpty(address.getText().toString())) {
            //ToastManager.showToast(getStringById(R.string.show_input_address));
            return false
        }
        return !StringUtil.isNullOrEmpty(duration.getText().toString())


    }

    fun setFacebookId() {
        startActivityForResult(Intent(context, CheckInFacebookActivity::class.java), mRequestCode)
    }


    fun setGender() {
        //        Logger.d("PersonalInfoActivity be clicked");
        val genderAdapter = getGenderAdapter() as InfoAdapterEnum
        dialogPlus = DialogManager.newListViewDialog(context)
                .setAdapter(genderAdapter)
                .setExpanded(false)
                .setGravity(Gravity.CENTER)
                .create()
        dialogPlus.show()
    }

    fun setEducation() {
        //        Logger.d("PersonalInfoActivity be clicked");
        val educationAdapter = getEducationAdapter()
        dialogPlus = DialogManager.newListViewDialog(context)
                .setAdapter(educationAdapter)
                .setExpanded(false)
                .setGravity(Gravity.CENTER)
                .create()
        dialogPlus.show()
    }


    fun setMarital() {
        //        Logger.d("PersonalInfoActivity be clicked");
        val maritalAdapter = getMaritalStatusAdapter() as InfoAdapterEnum
        dialogPlus = DialogManager.newListViewDialog(context)
                .setAdapter(maritalAdapter)
                .setExpanded(false)
                .setGravity(Gravity.CENTER)
                .create()
        dialogPlus.show()
    }


    fun setChildrenAccount() {
        //        Logger.d("PersonalInfoActivity be clicked");
        val childrenAccountAdapter = getChildrenAccountAdapter() as InfoAdapterEnum
        dialogPlus = DialogManager.newListViewDialog(context)
                .setAdapter(childrenAccountAdapter)
                .setExpanded(false)
                .setGravity(Gravity.CENTER)
                .create()
        dialogPlus.show()
    }

    fun setDuration() {
        //        Logger.d("PersonalInfoActivity be clicked");
        val durationAdapter = mPresenter.getDurationAdapter() as InfoAdapterEnum
        dialogPlus = DialogManager.newListViewDialog(context)
                .setAdapter(durationAdapter)
                .setExpanded(false)
                .setGravity(Gravity.CENTER)
                .create()
        dialogPlus.show()
    }

    fun personalInofSubmit() {
        UserEventQueue.add(ClickEvent(pInfoSummit.toString(), ActionType.CLICK, pInfoSummit.getText().toString()))
        if (ktpno.getText().toString().trim({ it <= ' ' }).length != 16) {
            ktpno.setTextColor(Color.RED)
            XLeoToast.showMessage(R.string.text_wrong_ktp_tips)
        } else if (isCheckedField()) {
            mActivity.showLoading(resources.getText(R.string.show_uploading).toString())
            personalInfoBean.setFullName(fullName.getText().toString().trim({ it <= ' ' }))
            personalInfoBean.familyNameInLaw = familyNameInLaw.text.toString()
            personalInfoBean.setCredentialNo(ktpno.getText().toString().trim({ it <= ' ' }))
            personalInfoBean.setAddress(address.getText().toString().trim({ it <= ' ' }))
            mPresenter.uploadDatas(personalInfoBean)
        }
    }

    override fun onAddressesObtain(addresses: List<Address>) {
        if (addresses != null && addresses.size > 0) {
            val sb = StringBuffer()
            //            for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex() - 1; i++) {
            //                sb.append(addresses.get(0).getAddressLine(i) + "-");
            //            }
            //            sb.append(addresses.get(0).getAddressLine(addresses.get(0).getMaxAddressLineIndex()));
            val address = addresses[0]
            sb.append(address.countryName + "-")
            sb.append(address.adminArea + "-")
            if (!TextUtils.isEmpty(address.subAdminArea)) {
                sb.append(address.subAdminArea + "-")
            }
            if (!TextUtils.isEmpty(address.locality)) {
                sb.append(address.locality + "-")
            }
            if (!TextUtils.isEmpty(address.subLocality)) {
                sb.append(address.subLocality + "-")
            }
            if (!TextUtils.isEmpty(address.thoroughfare)) {
                sb.append(address.thoroughfare + "-")
            }
            if (!TextUtils.isEmpty(address.subThoroughfare)) {
                sb.append(address.subThoroughfare)
            }
            LoggerWrapper.d("address" + addresses[0].getAddressLine(0))
            this.address.setText(sb.toString())
        } else {
            XLeoToast.showMessage(R.string.tips4inputaddress)
        }
    }

    override fun onLastLocationObtainError() {
        //todo
    }

}

class PersonalInfoPresenterImpl : PersonalInfoPresenter, BaseFragmentPresenterImpl() {
    /**
     * Set duration
     */
    override fun getDurationAdapter(): InfoAdapter {
        val areaAdapter = InfoAdapterEnum(mView.baseActivity)
        areaAdapter.addItem(DurationStatus.THREE_MONTH, InfoType.DURATION)
        areaAdapter.addItem(DurationStatus.SIX_MONTH, InfoType.DURATION)
        areaAdapter.addItem(DurationStatus.ONE_YEAR, InfoType.DURATION)
        areaAdapter.addItem(DurationStatus.TWO_YEAR, InfoType.DURATION)
        areaAdapter.addItem(DurationStatus.OVER_TWO_YEAR, InfoType.DURATION)
        areaAdapter.setOnItemClickListener(object :OnInfoAdapterItemClickListener{
            override fun onItemClick(data: InfoAdapter.InfoItem) {
                if (isAttached) {
                    (mView as PersonalInfoView).onDurationSelected(data)
                }
            }
        })
        return areaAdapter
    }

    override fun uploadDatas(personalInfoBean: PersonalInfoBean) {
        mUserApi.submitPersonalInfo(personalInfoBean.fullName, personalInfoBean.credentialNo, personalInfoBean.familyNameInLaw, personalInfoBean.gender, personalInfoBean.province.name, personalInfoBean.city.name, personalInfoBean.district.name, personalInfoBean.area.name, personalInfoBean.address, personalInfoBean.lastEducation, personalInfoBean.maritalStatus, personalInfoBean.childrenNumber, personalInfoBean.residenceDuration, personalInfoBean.facebookId, TokenManager.getInstance().token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ResponseBody>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        XLeoToast.showMessage(e.message)
                        if (!isAttached) {
                            return
                        }
                        mView.baseActivity.dismissLoading()
                        Logger.d("e:" + e)

                    }

                    override fun onNext(responseBody: ResponseBody) {
                        if (!isAttached) {
                            return
                        }
                        mView.baseActivity.setResult(Activity.RESULT_OK)
                        mView.baseActivity.dismissLoading()
                        mView.baseActivity.finish()
                        Logger.d("Submit success!")
                    }
                })
    }

    override fun obtainLastLocation(personalInfoActivity: Context) {
        val locationManager = personalInfoActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(personalInfoActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(personalInfoActivity.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (isAttached) {
                (mView as PersonalInfoActView).onPermissionDenied()
            }
            return
        }
        var lastKnownLocation: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (lastKnownLocation == null) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        }

        if (lastKnownLocation == null) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        if (lastKnownLocation == null) {
            if (isAttached) {
                (mView as PersonalInfoView).onLastLocationObtainError()
            }
        }
        getAddressesByGeoCoder(personalInfoActivity, lastKnownLocation!!.latitude, lastKnownLocation.longitude)
    }

    fun getAddressesByGeoCoder(mapsActivity: Context, latitude: Double, longitude: Double) {
        GeoLocationApi.getAddresses(mapsActivity, latitude, longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Address>>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        LoggerWrapper.d(e)
                        XLeoToast.showMessage(R.string.tips4inputaddress)
                        if (isAttached) {
                            (mView as PersonalInfoActView).onLastLocationObtainError()
                        }
                    }

                    override fun onNext(addresses: List<Address>) {
                        LoggerWrapper.e("geo data bean:" + addresses[0].adminArea)
                        if (isAttached) {
                            (mView as PersonalInfoView).onAddressesObtain(addresses)
                        }
                    }
                })
    }

    private var hasObtain:Boolean = false
    override fun obtainInitDatas() {
        if (hasObtain) {
            return
        }
        if (isAttached) {
            mUserApi.getPersonalInfo(TokenManager.getInstance().token)
                    .doOnSubscribe(Action0 {
                        if (isAttached) {
                            mView.baseActivity.runOnUiThread {
                                mView.baseActivity.showLoading(null)
                            }
                        }

                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<PersonalInfoServerBean>() {
                        override fun onCompleted() {

                        }

                        override fun onError(e: Throwable) {
                            if (isAttached) {
                                mView.baseActivity.runOnUiThread {
                                    mView.baseActivity.dismissLoading()
                                }
                            }
                            Logger.d("Get Personalinfo failed: " + e.message)
                            XLeoToast.showMessage(R.string.show_get_personalinfo_failed)
                        }

                        override fun onNext(personalInfoServerBean: PersonalInfoServerBean) {
                            if (isAttached) {
                                (mView as PersonalInfoView).reinitView(personalInfoServerBean as PersonalInfoServerBean)
                                hasObtain = true
                                mView.baseActivity.runOnUiThread {
                                    mView.baseActivity.dismissLoading()
                                }
                            }
                        }
                    })
        }
    }

}

interface PersonalInfoView {
    fun reinitView(personalInfoServerBean: PersonalInfoServerBean)
    fun onLastLocationObtainError()
    fun onAddressesObtain(addresses: List<Address>)
    fun onDurationSelected(data: InfoAdapter.InfoItem)

}

interface PersonalInfoPresenter : BaseFragmentPresenter {
    fun obtainInitDatas()
    fun uploadDatas(personalInfoBean: PersonalInfoBean)
    fun getDurationAdapter(): InfoAdapter
    fun obtainLastLocation(context: Context)

}

interface OnInfoAdapterItemClickListener{
    fun onItemClick(data:InfoAdapter.InfoItem)
}
