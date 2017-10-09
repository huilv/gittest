package com.daunkredit.program.sulu.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.StringUtil;
import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.ToastManager;
import com.daunkredit.program.sulu.app.base.BaseFragment;
import com.daunkredit.program.sulu.common.InfoAdapter;
import com.daunkredit.program.sulu.common.InfoAdapterString;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.utils.StringFormatUtils;
import com.daunkredit.program.sulu.common.utils.XLeoSp;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.MainActivity;
import com.daunkredit.program.sulu.view.certification.status.InfoType;
import com.daunkredit.program.sulu.view.fragment.presenter.LoaningFraPreImp;
import com.daunkredit.program.sulu.view.fragment.presenter.LoaningFraPresenter;
import com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager;
import com.daunkredit.program.sulu.widget.selfdefedittext.OnCheckInputResultAdapter;
import com.daunkredit.program.sulu.widget.selfdefedittext.XLeoEditText;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.hwangjr.rxbus.RxBus;
import com.orhanobut.logger.Logger;
import com.sulu.kotlin.activity.ChooseCouponActivity;
import com.sulu.kotlin.data.CouponBean;
import com.sulu.kotlin.fragment.OnInfoAdapterItemClickListener;
import com.sulu.kotlin.utils.SpanBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import butterknife.BindView;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;


/**
 * Created by milo on 17-2-14.
 */

public class LoaningFragment extends BaseFragment<LoaningFraPresenter> implements LoaningFraView {

    private static final int REQUEST_COUPON_CHOOSE = 1532;
    @BindView(R.id.id_textview_repayment_amount)
    TextView idTextviewRepaymentAmount;
    @BindView(R.id.id_textview_receiving_bank)
    TextView idTextviewReceivingBank;
    @BindView(R.id.id_imagebutton_bank)
    ImageButton idImagebuttonBank;
    @BindView(R.id.id_edittext_bank_number)
    XLeoEditText idEdittextBankNumber;
    @BindView(R.id.id_edittext_use_loan)
    XLeoEditText idEdittextUseLoan;
    @BindView(R.id.id_button_current_loan_ing)
    Button idButtonCurrentLoanIing;
    @BindView(R.id.id_textview_loan_selected_amount)
    TextView idTextviewLoanSelectedAmount;
    @BindView(R.id.id_textview_loan_selected_day)
    TextView idTextviewLoanSelectedDay;
    @BindView(R.id.id_linearlayout_receiving_bank)
    LinearLayout idLinearlayoutReceivingBank;
    @BindView(R.id.id_textview_coupon_select)
    TextView idTextviewCouponSelect;
    @BindView(R.id.id_linearlayout_coupon_select)
    LinearLayout idLinearlayoutCouponSelect;
    @BindView(R.id.cb_aggreed)
    CheckBox CbAgreed;
    @BindView(R.id.tv_aggrement)
    TextView tv_aggrement;

    Dialog dialogPlus;
    static ApplyLoanInfo applyLoanInfo;
    private long mCouponId;


    @Override
    protected LoaningFraPresenter initPresenter() {
        return new LoaningFraPreImp();
    }

    @Override
    protected boolean doPreBuildHeader() {
        return true;
    }

    @Nullable
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_loaning;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RxBus.get().register(this);
        initInputCheck();
        idLinearlayoutCouponSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LoaningFragment.this.getContext(), ChooseCouponActivity.class), REQUEST_COUPON_CHOOSE);
            }
        });
        initLegalStatement();
        CbAgreed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CbAgreed.setSelected(isChecked);
            }
        });
    }

    private void initLegalStatement() {
        String statement = getString(R.string.textview_video_statement);
        String agree = getString(R.string.text_loan_agreement_title);
        int start = statement.indexOf(agree);
        Function0<Unit> func = new Function0<Unit>() {
            @Override
            public Unit invoke() {
                showLegalAgreement();
                return null;
            }
        };
        SpannableStringBuilder result = SpanBuilder.INSTANCE.init(statement)
                .setLinkSpan(start, start + agree.length(), getResources().getColor(R.color.colorProgress_blue), func)
                .result();
        tv_aggrement.setText(result);
        tv_aggrement.setMovementMethod(new LinkMovementMethod());
    }

    private void showLegalAgreement() {
        String fileName = "needcopy/loan_agreement.html";
        DialogManager.showHtmlDialogWithCheck(fileName,getContext(), getString(R.string.title_loan_agreement_title));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_COUPON_CHOOSE && resultCode == Activity.RESULT_OK) {
            CouponBean couponBean = TokenManager.checkoutMessage(FieldParams.COUPON_CHOOSED_NAME, CouponBean.class, true);
            if (couponBean != null) {
                double toReduce = couponBean.getDischargeInterestDay() * XLeoSp.getInstance(getContext()).getDouble(FieldParams.CURRENT_LOAN_RATE, 0.01);
                idTextviewCouponSelect.setText(couponBean.getTitle() + " " + "-" + toReduce);
                mCouponId = couponBean.getId();
                idTextviewRepaymentAmount.setText("" + MainActivity.selectedTotalAmount.getTotalRepayment()*(1 - toReduce));
            } else {
                idTextviewCouponSelect.setText("");
                mCouponId = -1;
            }
        }
    }

    private void initInputCheck() {
        idEdittextBankNumber.setOnCheckInputResult(new OnCheckInputResultAdapter() {
            @Override
            public boolean onCheckResult(EditText v) {
                //TODO:
                return true;
            }
        });
        idEdittextUseLoan.setOnCheckInputResult(new OnCheckInputResultAdapter() {
            @Override
            public boolean onCheckResult(EditText v) {
                //TODO:
                return true;
            }
        });
    }

    @Override
    protected void initData() {
        if (MainActivity.selectedTotalAmount == null) {
            Logger.d("Selected account is null");
            return;
        }
        idTextviewLoanSelectedAmount.setText(StringFormatUtils.indMoneyFormat(MainActivity.selectedTotalAmount.getAmount()));
        idTextviewLoanSelectedDay.setText(String.valueOf(MainActivity.selectedTotalAmount.getDay()) + "Days");
        idTextviewRepaymentAmount.setText(StringFormatUtils.indMoneyFormat(MainActivity.selectedTotalAmount.getTotalRepayment()));
    }


    /**
     * Set Loan bank adapter
     */

    public InfoAdapterString getLoanBankAdapter() {

        ArrayList<String> bankName = new ArrayList(Arrays.asList(
                "BCA",
                "MANDIRI",
                "BRI",
                "BNI",
                "CIMB"
        ));

        ArrayList<String> bankname2 = new ArrayList(Arrays.asList("DANAMON",
                "PERMATA",
                "PANIN",
                "BII",
                "ANGLOMAS",
                "BANGKOK",
                "AGRIS",
                "SINARMAS",
                "AGRONIAGA",
                "ANDARA",
                "ANTAR_DAERAH",
                "ANZ",
                "ARTHA",
                "ARTOS",
                "BISNIS_INTERNASIONAL",
                "BJB",
                "BJB_SYR",
                "BNI_SYR",
                "BNP_PARIBAS",
                "BUKOPIN",
                "BUMI_ARTA",
                "CAPITAL",
                "BCA_SYR",
                "CHINATRUST",
                "CIMB_UUS",
                "COMMONWEALTH",
                "DANAMON_UUS",
                "DBS",
                "DINAR_INDONESIA",
                "DKI",
                "DKI_UUS",
                "EKONOMI_RAHARJA",
                "FAMA",
                "GANESHA",
                "HANA",
                "HARDA_INTERNASIONAL",
                "HIMPUNAN_SAUDARA",
                "ICBC",
                "INA_PERDANA",
                "INDEX_SELINDO",
                "JASA_JAKARTA",
                "KESEJAHTERAAN_EKONOMI",
                "MASPION",
                "MAYAPADA",
//                "MAYBANK_SYR",
                "MAYORA",
                "MEGA",
                "MESTIKA_DHARMA",
                "METRO_EXPRESS",
                "MIZUHO",
                "MNC_INTERNASIONAL",
                "MUAMALAT",
                "MULTI_ARTA_SENTOSA",
                "MUTIARA",
                "NATIONALNOBU",
                "NUSANTARA_PARAHYANGAN",
                "OCBC",
                "OCBC_UUS",
                "BAML",
                "BOC",
                "INDIA",
                "TOKYO",
                "PANIN_SYR",
                "PERMATA_UUS",
                "PUNDI_INDONESIA",
                "QNB_KESAWAN",
                "RABOBANK",
                "RESONA",
                "ROYAL",
                "SAHABAT_PURBA_DANARTA",
                "SAHABAT_SAMPOERNA",
                "SBI_INDONESIA",
                "SINAR_HARAPAN_BALI",
                "MITSUI",
                "BRI_SYR",
                "BUKOPIN_SYR",
                "MANDIRI_SYR",
                "MEGA_SYR",
                "BTN",
                "BTN_UUS",
                "TABUNGAN_PENSIUNAN_NASIONAL",
                "TABUNGAN_PENSIUNAN_NASIONAL_UUS",
                "UOB",
                "VICTORIA_INTERNASIONAL",
                "VICTORIA_SYR",
                "WINDU",
                "WOORI",
                "YUDHA_BHAKTI",
                "ACEH",
                "ACEH_UUS",
                "BALI",
                "BENGKULU",
                "DAERAH_ISTIMEWA",
                "DAERAH_ISTIMEWA_UUS",
                "JAMBI",
                "JAMBI_UUS",
                "JAWA_TENGAH",
                "JAWA_TENGAH_UUS",
                "JAWA_TIMUR",
                "JAWA_TIMUR_UUS",
                "KALIMANTAN_BARAT",
                "KALIMANTAN_BARAT_UUS",
                "KALIMANTAN_SELATAN",
                "KALIMANTAN_SELATAN_UUS",
                "KALIMANTAN_TENGAH",
                "KALIMANTAN_TIMUR",
                "KALIMANTAN_TIMUR_UUS",
                "LAMPUNG",
                "MALUKU",
                "NUSA_TENGGARA_BARAT",
                "NUSA_TENGGARA_BARAT_UUS",
                "NUSA_TENGGARA_TIMUR",
                "PAPUA",
                "RIAU_DAN_KEPRI",
                "RIAU_DAN_KEPRI_UUS",
                "SULAWESI",
                "SULAWESI_TENGGARA",
                "SULSELBAR",
                "SULSELBAR_UUS",
                "SULUT",
                "SUMATERA_BARAT",
                "SUMATERA_BARAT_UUS",
                "SUMSEL_DAN_BABEL",
                "SUMSEL_DAN_BABEL_UUS",
                "SUMUT",
                "SUMUT_UUS",
                "CENTRATAMA",
                "CITIBANK",
                "DEUTSCHE",
                "HSBC",
                "HSBC_UUS",
                "JPMORGAN",
                "PRIMA_MASTER",
                "RBS",
                "STANDARD_CHARTERED",
                "MITRA_NIAGA",
                "EKSPOR_INDONESIA",
                "ARTA_NIAGA_KENCANA"

        ));

        Collections.sort(bankname2
        );

        bankName.addAll(bankname2);

        InfoAdapterString loanBankInfoAdapter = new InfoAdapterString(getActivity());
        for (String info : bankName
                ) {
            loanBankInfoAdapter.addItem(info, InfoType.RECEIVINGBANK);
        }
        loanBankInfoAdapter.setOnItemClickListener(new OnInfoAdapterItemClickListener() {
            @Override
            public void onItemClick(InfoAdapter.InfoItem data) {
                onReceivingBank(data);
            }
        });
        return loanBankInfoAdapter;
    }

    public boolean isCheckedField() {

        if (StringUtil.isNullOrEmpty(idEdittextBankNumber.getText().toString())) {
            ToastManager.showToast(getString(R.string.show_input_bank_number));
            return false;
        }
        if (StringUtil.isNullOrEmpty(idEdittextUseLoan.getText().toString())) {
            ToastManager.showToast(getString(R.string.show_input_loan_usage));
            return false;
        }
        return true;
    }


    public void onReceivingBank(InfoAdapter.InfoItem event) {
        if (event.getType() == InfoType.RECEIVINGBANK) {

            if (dialogPlus != null && dialogPlus.isShowing()) {
                dialogPlus.dismiss();
            }
            idTextviewReceivingBank.setText(event.getInfoStr());
        }
    }

    @OnClick(R.id.id_linearlayout_receiving_bank)
    public void setReceivingBank() {
        UserEventQueue.add(new ClickEvent(idLinearlayoutReceivingBank.toString(), ActionType.CLICK, idTextviewReceivingBank.getText().toString()));
        dialogPlus = DialogManager.newListViewDialog(getActivity())
//                .setContentHolder(new ListHolder())
                .setGravity(Gravity.CENTER)
                .setExpanded(false)
                .setAdapter(getLoanBankAdapter())
                .create();
        dialogPlus.show();


    }

    @Override
    public void resetButton() {
        idButtonCurrentLoanIing.setClickable(true);
    }


    public static class ApplyLoanInfo {
        String loanType;
        double amount;
        int peroid;
        String periodUnit;
        String bankCode;
        String cardNo;
        String applyFor;
        String applyChannel;
        String applyPlatform;

        public long getCouponId() {
            return couponId;
        }

        public void setCouponId(long couponId) {
            this.couponId = couponId;
        }

        long couponId;

        public String getLoanType() {
            return loanType;
        }

        public void setLoanType(String loanType) {
            this.loanType = loanType;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public int getPeroid() {
            return peroid;
        }

        public void setPeroid(int peroid) {
            this.peroid = peroid;
        }

        public String getPeriodUnit() {
            return periodUnit;
        }

        public void setPeriodUnit(String periodUnit) {
            this.periodUnit = periodUnit;
        }

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getCardNo() {
            return cardNo;
        }

        public void setCardNo(String cardNo) {
            this.cardNo = cardNo;
        }

        public String getApplyFor() {
            return applyFor;
        }

        public void setApplyFor(String applyFor) {
            this.applyFor = applyFor;
        }

        public String getApplyChannel() {
            return applyChannel;
        }

        public void setApplyChannel(String applyChannel) {
            this.applyChannel = applyChannel;
        }

        public String getApplyPlatform() {
            return applyPlatform;
        }

        public void setApplyPlatform(String applyPlatform) {
            this.applyPlatform = applyPlatform;
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            idButtonCurrentLoanIing.setClickable(true);

            idTextviewLoanSelectedAmount.setText(StringFormatUtils.indMoneyFormat(MainActivity.selectedTotalAmount.getAmount()));
            idTextviewLoanSelectedDay.setText(String.valueOf(MainActivity.selectedTotalAmount.getDay()) + "Days");
            idTextviewRepaymentAmount.setText(StringFormatUtils.indMoneyFormat(MainActivity.selectedTotalAmount.getTotalRepayment()));
        }
    }

    @OnClick(R.id.id_button_current_loan_ing)
    public void applyLoanAppSubmit() {
        UserEventQueue.add(new ClickEvent(idButtonCurrentLoanIing.toString(), ActionType.CLICK, idButtonCurrentLoanIing.getText().toString()));
        if (isCheckedField()&&isLawAgreed()) {
            applyLoanInfo = new ApplyLoanInfo();
            applyLoanInfo.loanType = "PAYDAY";
            applyLoanInfo.amount = MainActivity.selectedTotalAmount.getAmount();
            applyLoanInfo.peroid = MainActivity.selectedTotalAmount.getDay();
            applyLoanInfo.periodUnit = "D";
            applyLoanInfo.bankCode = idTextviewReceivingBank.getText().toString();
            applyLoanInfo.cardNo = idEdittextBankNumber.getText().toString();
            applyLoanInfo.applyFor = idEdittextUseLoan.getText().toString();
            applyLoanInfo.applyChannel = "NONE";
            applyLoanInfo.applyPlatform = "ANDROID";
            if (mCouponId != -1) {
                applyLoanInfo.couponId = mCouponId;
            }

            idButtonCurrentLoanIing.setClickable(false);
            mPresenter.applyLoan(applyLoanInfo);
        }


//        if (isCheckedField()) {
//            api.progress(TokenManager.getInstance().getToken())
//                    .doOnNext(new Action1<ProgressBean>() {
//                        @Override
//                        public void call(ProgressBean progressBean) {
//                            if (!progressBean.isCompleted()) {
//                                String msg;
//                                if (!progressBean.isPersonalInfoPart()) {
//                                    msg = "Please finished the personal info part.";
//                                } else if (!progressBean.isEmploymentPart()) {
//                                    msg = "Please finished the employment info part.";
//                                } else if (!progressBean.isContactPart()) {
//                                    msg = "Please finished the contract info part.";
//                                } else {
//                                    msg = "Please finished the photo uploading.";
//                                }
//                                Logger.d("Info not finished: msg = " + msg);
//                                RxBus.get().post(new LoaningFragment());
//                            }
//                            Logger.d("progressBean: " + progressBean.isCompleted());
//                        }
//                    })
//                    .filter(new Func1<ProgressBean, Boolean>() {
//                        @Override
//                        public Boolean call(ProgressBean progressBean) {
//                            return progressBean.isCompleted();
//                        }
//                    })
//                    .flatMap(new Func1<ProgressBean, Observable<ResponseBody>>() {
//                        @Override
//                        public Observable<ResponseBody> call(ProgressBean progressBean) {
//                            return api.applyLoanApp("PAYDAY",
//                                    LoanNormalFragment.totalAmount.getAmount() + "",
//                                    LoanNormalFragment.totalAmount.getDay() + "",
//                                    "D",
//                                    idTextviewReceivingBank.getText().toString(),
//                                    idEdittextBankNumber.getText().toString(),
//                                    idEdittextUseLoan.getText().toString(),
//                                    "NONE",
//                                    "ANDROID",
//                                    TokenManager.getInstance().getToken());
//                        }
//                    }).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Subscriber<ResponseBody>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                            Logger.d("e:" + e);
//
//                        }
//
//                        @Override
//                        public void onNext(ResponseBody responseBody) {
//                            Logger.d("Loan success!");
//                            Intent intent = new Intent(getActivity(), TakeVideoActivity.class);
//                            startActivity(intent);
//                        }
//                    });
//        }
//    }

        //
        //        if(isCheckedField()){
        //            api.applyLoanApp("PAYDAY",
        //                    LoanNormalFragment.totalAmount.getAmount()+"",
        //                    LoanNormalFragment.totalAmount.getDay()+"",
        //                    "D",
        //                    idTextviewReceivingBank.getText().toString(),
        //                    idEdittextBankNumber.getText().toString(),
        //                    idEdittextUseLoan.getText().toString(),
        //                    "NONE",
        //                    "ANDROID",
        //                    TokenManager.getInstance().getToken())
        //                    .subscribeOn(Schedulers.io())
        //                    .doOnNext(new Action1<ResponseBody>() {
        //                        @Override
        //                        public void call(ResponseBody responseBody) {
        //
        //                        }
        //                    })
        //                    .observeOn(AndroidSchedulers.mainThread())
        //                    .subscribe(new Subscriber<ResponseBody>() {
        //                        @Override
        //                        public void onCompleted() {
        //
        //                        }
        //
        //                        @Override
        //                        public void onError(Throwable e) {
        //                            Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();
        //                            Logger.d("e:" + e);
        //
        //                        }
        //
        //                        @Override
        //                        public void onNext(ResponseBody responseBody) {
        //                            Logger.d("Loan success!");
        //                            Intent intent = new Intent(getActivity(), TakeVideoActivity.class);
        //                            startActivity(intent);
        //                        }
        //                    });
    }

    private boolean isLawAgreed() {
        if (!CbAgreed.isChecked()) {
            XLeoToast.showMessage(R.string.show_read_agreement);
        }
        return CbAgreed.isChecked();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

}
