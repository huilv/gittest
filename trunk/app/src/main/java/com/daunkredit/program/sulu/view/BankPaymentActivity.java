package com.daunkredit.program.sulu.view;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.view.fragment.LoanRepaymentFragment;
import com.daunkredit.program.sulu.view.presenter.BankPayActPresenter;
import com.daunkredit.program.sulu.view.presenter.BankPaymentActPresenterImpl;
import com.daunkredit.program.sulu.widget.xleotextview.BothEndFitText;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by Miaoke on 27/03/2017.
 */

public class BankPaymentActivity extends BaseActivity<BankPayActPresenter> implements BankPayActView {

    @BindView(R.id.id_textview_repayment_atm)
    TextView       idTextviewRepaymentAtm;
    @BindView(R.id.id_textview_repayment_online)
    TextView       idTextviewRepaymentOnline;
    @BindView(R.id.id_textview_repayment_mbanking)
    TextView       idTextviewRepaymentMbanking;
    @BindView(R.id.id_textview_repayment_steps)
    BothEndFitText idTextviewRepaymentSteps;
    @BindView(R.id.id_linearlayout_bank_repayment)
    LinearLayout   idLinearLayoutBankRepayment;
    @BindView(R.id.id_textview_bank_repayment_price)
    TextView       idTextviewBankRepaymentPrice;
    @BindView(R.id.id_textview_bank_repayment_va)
    TextView       idTextviewBankRepaymentVa;

    @BindView(R.id.id_textview_line_atm)
    TextView idtextviewLineAtm;
    @BindView(R.id.id_textview_line_online)
    TextView idtextviewLineOnline;
    @BindView(R.id.id_textview_line_mbanking)
    TextView idtextviewLineMbanking;

    @BindView(R.id.id_image_repayment_bank_logo)
    ImageView idImagebuttonRepaymentBank;
    @BindView(R.id.id_textview_transaction_code_tips)
    TextView  idTextviewTransactionCodeTips;
    @BindView(R.id.id_textview_tc_va_text)
    TextView  idTextviewRepaymentVaTc;

    @BindView(R.id.id_textview_repayment_title)
    TextView   idTextviewRepaymentTitle;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_bank_repayment;
    }

    @Override
    public void init() {
        Logger.d("Bank repayment Init.");
        idTextviewRepaymentAtm.setSelected(true);

        if ("ALFAMART".equals(LoanRepaymentFragment.depositRB.getDepositMethod()) && "BLUEPAY".equals(LoanRepaymentFragment.depositRB.getDepositChannel())) {
            if (idLinearLayoutBankRepayment.getVisibility() != View.GONE) {
                idLinearLayoutBankRepayment.setVisibility(View.GONE);
            }

            if (idTextviewRepaymentMbanking.getVisibility() != View.VISIBLE) {
                idTextviewRepaymentMbanking.setVisibility(View.VISIBLE);
            }

            if (idImagebuttonRepaymentBank.getVisibility() != View.GONE) {
                idImagebuttonRepaymentBank.setVisibility(View.GONE);
            }

            if (idTextviewTransactionCodeTips.getVisibility() != View.VISIBLE) {
                idTextviewTransactionCodeTips.setVisibility(View.VISIBLE);
            }
            idTextviewTransactionCodeTips.setText(getString(R.string.alfamarts_transaction_code_tips));
            idTextviewRepaymentVaTc.setText(getString(R.string.repayment_transation_code_text));


            String alfamart = getResources().getString(R.string.pay_in_alfamart);
            setRepaymentStepsWords(alfamart, LoanRepaymentFragment.paymentCode, LoanRepaymentFragment.price);
            idTextviewBankRepaymentPrice.setText(LoanRepaymentFragment.price);
            idTextviewBankRepaymentVa.setText(LoanRepaymentFragment.paymentCode);
            idTextviewRepaymentTitle.setText(getString(R.string.pay_in_alfamart_title));
        } else {
            if (idLinearLayoutBankRepayment.getVisibility() == View.GONE) {
                idLinearLayoutBankRepayment.setVisibility(View.VISIBLE);
            }

            if (("OTHERS".equals(LoanRepaymentFragment.depositRB.getDepositMethod()) && "BLUEPAY".equals(LoanRepaymentFragment.depositRB.getDepositChannel())) ||
                    ("BCA".equals(LoanRepaymentFragment.depositRB.getDepositMethod()) && "BLUEPAY".equals(LoanRepaymentFragment.depositRB.getDepositChannel())) ||
                    ("MANDIRI".equals(LoanRepaymentFragment.depositRB.getDepositMethod()) && "BLUEPAY".equals(LoanRepaymentFragment.depositRB.getDepositChannel())) ||
                    ("BNI".equals(LoanRepaymentFragment.depositRB.getDepositMethod()) && "BLUEPAY".equals(LoanRepaymentFragment.depositRB.getDepositChannel()))


                    ) {
                if (idTextviewRepaymentMbanking.getVisibility() != View.VISIBLE) {
                    idTextviewRepaymentMbanking.setVisibility(View.VISIBLE);
                }

                if (idtextviewLineMbanking.getVisibility() != View.VISIBLE) {
                    idtextviewLineMbanking.setVisibility(View.VISIBLE);
                }

                if (idImagebuttonRepaymentBank.getVisibility() != View.GONE) {
                    idImagebuttonRepaymentBank.setVisibility(View.GONE);
                }

                if (idTextviewTransactionCodeTips.getVisibility() != View.VISIBLE) {
                    idTextviewTransactionCodeTips.setVisibility(View.VISIBLE);
                }
                idTextviewTransactionCodeTips.setText(getString(R.string.other_banks_transaction_code_tips));
                idTextviewRepaymentVaTc.setText(getString(R.string.repayment_transation_code_text));

                Logger.d("Bluepay paymentCode:" + LoanRepaymentFragment.paymentCode);

                if("BNI".equals(LoanRepaymentFragment.depositRB.getDepositMethod())){
                    setRepaymentSteps(R.string.bni_transaction_id_atm, R.string.bni_transaction_id_online, R.string.bni_transaction_id_mobile_banking, LoanRepaymentFragment.paymentCode);
                    String bniTransactionAtm = getResources().getString(R.string.bni_transaction_id_atm);
                    setRepaymentStepsWords(bniTransactionAtm, LoanRepaymentFragment.paymentCode, LoanRepaymentFragment.price);
                }else{
                    setRepaymentSteps(R.string.other_banks_atm, R.string.other_banks_online, R.string.other_banks_mbanking, LoanRepaymentFragment.paymentCode);
                    String others = getResources().getString(R.string.other_banks_atm);
                    setRepaymentStepsWords(others, LoanRepaymentFragment.paymentCode, LoanRepaymentFragment.price);
                }

                idTextviewBankRepaymentPrice.setText(LoanRepaymentFragment.price);
                idTextviewBankRepaymentVa.setText(LoanRepaymentFragment.paymentCode);

                if ("BCA".equals(LoanRepaymentFragment.depositRB.getDepositMethod())) {
                    if (idImagebuttonRepaymentBank.getVisibility() != View.VISIBLE) {
                        idImagebuttonRepaymentBank.setVisibility(View.VISIBLE);
                    }
                    idImagebuttonRepaymentBank.setImageResource(R.drawable.ic_bca);
                    idTextviewRepaymentTitle.setText(getString(R.string.bca_title));
                } else if ("MANDIRI".equals(LoanRepaymentFragment.depositRB.getDepositMethod())) {
                    idTextviewRepaymentTitle.setText(getString(R.string.mandiri_title));
                    if (idImagebuttonRepaymentBank.getVisibility() != View.VISIBLE) {
                        idImagebuttonRepaymentBank.setVisibility(View.VISIBLE);
                    }
                    idImagebuttonRepaymentBank.setImageResource(R.drawable.ic_mandiri);

                } else if ("BNI".equals(LoanRepaymentFragment.depositRB.getDepositMethod())) {
                    idTextviewRepaymentTitle.setText(getString(R.string.bni_title));
                    if (idImagebuttonRepaymentBank.getVisibility() != View.VISIBLE) {
                        idImagebuttonRepaymentBank.setVisibility(View.VISIBLE);
                    }
                    idImagebuttonRepaymentBank.setImageResource(R.drawable.ic_bni);
                } else {
                    idTextviewRepaymentTitle.setText(getString(R.string.other_banks_title));

                    if (idImagebuttonRepaymentBank.getVisibility() != View.GONE) {
                        idImagebuttonRepaymentBank.setVisibility(View.GONE);
                    }
                }

                idTextviewBankRepaymentPrice.setText(LoanRepaymentFragment.price);
                idTextviewBankRepaymentVa.setText(LoanRepaymentFragment.paymentCode);


            } else if ("BCA".equals(LoanRepaymentFragment.depositRB.getDepositMethod()) && "XENDIT".equals(LoanRepaymentFragment.depositRB.getDepositChannel())) {

                if (idTextviewRepaymentMbanking.getVisibility() != View.VISIBLE) {
                    idTextviewRepaymentMbanking.setVisibility(View.VISIBLE);
                }

                if (idtextviewLineMbanking.getVisibility() != View.VISIBLE) {
                    idtextviewLineMbanking.setVisibility(View.VISIBLE);
                }

                if (idImagebuttonRepaymentBank.getVisibility() != View.VISIBLE) {
                    idImagebuttonRepaymentBank.setVisibility(View.VISIBLE);
                }
                idImagebuttonRepaymentBank.setImageResource(R.drawable.ic_bca);

                if (idTextviewTransactionCodeTips.getVisibility() != View.GONE) {
                    idTextviewTransactionCodeTips.setVisibility(View.GONE);
                }
                idTextviewRepaymentVaTc.setText(getString(R.string.repayment_virtual_account_text));

                Logger.d("BCA VA: " + LoanRepaymentFragment.depositRB.getPaymentCode());
                setRepaymentSteps(R.string.bca_virtual_account_atm, R.string.bca_virtual_account_internet_banking, R.string.bca_virtual_account_mobile_banking, LoanRepaymentFragment.depositRB.getPaymentCode());

                String bca = getResources().getString(R.string.bca_virtual_account_atm);
                setRepaymentStepsWords(bca, LoanRepaymentFragment.depositRB.getPaymentCode(), LoanRepaymentFragment.price);
                idTextviewBankRepaymentPrice.setText(LoanRepaymentFragment.price);
                idTextviewBankRepaymentVa.setText(LoanRepaymentFragment.depositRB.getPaymentCode());
                idTextviewRepaymentTitle.setText(getString(R.string.bca_title));
            } else if ("MANDIRI".equals(LoanRepaymentFragment.depositRB.getDepositMethod()) && "XENDIT".equals(LoanRepaymentFragment.depositRB.getDepositChannel())) {
                if (idTextviewRepaymentMbanking.getVisibility() != View.GONE) {
                    idTextviewRepaymentMbanking.setVisibility(View.GONE);
                }

                if (idtextviewLineMbanking.getVisibility() != View.GONE) {
                    idtextviewLineMbanking.setVisibility(View.GONE);
                }

                if (idImagebuttonRepaymentBank.getVisibility() != View.VISIBLE) {
                    idImagebuttonRepaymentBank.setVisibility(View.VISIBLE);
                }
                idImagebuttonRepaymentBank.setImageResource(R.drawable.ic_mandiri);

                if (idTextviewTransactionCodeTips.getVisibility() != View.GONE) {
                    idTextviewTransactionCodeTips.setVisibility(View.GONE);
                }

                idTextviewRepaymentVaTc.setText(getString(R.string.repayment_virtual_account_text));


                Logger.d("MANDIRI VA: " + LoanRepaymentFragment.depositRB.getPaymentCode());
                setRepaymentSteps(R.string.mandiri_virtual_account_atm, R.string.mandiri_virtual_account_internet_banking, LoanRepaymentFragment.depositRB.getPaymentCode());
                String mandiri = getResources().getString(R.string.mandiri_virtual_account_atm);
                setRepaymentStepsWords(mandiri, LoanRepaymentFragment.depositRB.getPaymentCode(), LoanRepaymentFragment.price);
                idTextviewBankRepaymentPrice.setText(LoanRepaymentFragment.price);
                idTextviewBankRepaymentVa.setText(LoanRepaymentFragment.depositRB.getPaymentCode());

                idTextviewRepaymentTitle.setText(getString(R.string.mandiri_title));

            } else if ("BNI".equals(LoanRepaymentFragment.depositRB.getDepositMethod()) && "XENDIT".equals(LoanRepaymentFragment.depositRB.getDepositChannel())) {
                if (idTextviewRepaymentMbanking.getVisibility() != View.GONE) {
                    idTextviewRepaymentMbanking.setVisibility(View.GONE);
                }
                if (idtextviewLineMbanking.getVisibility() != View.GONE) {
                    idtextviewLineMbanking.setVisibility(View.GONE);
                }

                if (idImagebuttonRepaymentBank.getVisibility() != View.VISIBLE) {
                    idImagebuttonRepaymentBank.setVisibility(View.VISIBLE);
                }
                idImagebuttonRepaymentBank.setImageResource(R.drawable.ic_bni);

                if (idTextviewTransactionCodeTips.getVisibility() != View.GONE) {
                    idTextviewTransactionCodeTips.setVisibility(View.GONE);
                }
                idTextviewRepaymentVaTc.setText(getString(R.string.repayment_virtual_account_text));


                Logger.d("BNI VA: " + LoanRepaymentFragment.depositRB.getPaymentCode());
                setRepaymentSteps(R.string.bni_virtual_account_atm, R.string.bni_virtual_account_online, LoanRepaymentFragment.depositRB.getPaymentCode());
                String bni = getResources().getString(R.string.bni_virtual_account_atm);
                setRepaymentStepsWords(bni, LoanRepaymentFragment.depositRB.getPaymentCode(), LoanRepaymentFragment.price);
                idTextviewBankRepaymentPrice.setText(LoanRepaymentFragment.price);
                idTextviewBankRepaymentVa.setText(LoanRepaymentFragment.depositRB.getPaymentCode());
                idTextviewRepaymentTitle.setText(getString(R.string.bni_title));
            } else {
                Logger.d("Method or channel not correct");
            }
        }
    }


    private void setRepaymentSteps(final int atm, final int online, final int mbanking, final String paCodeOrVA) {
        RxView.clicks(idTextviewRepaymentAtm).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                String otherAtm = getResources().getString(atm);
                setRepaymentStepsWords(otherAtm, paCodeOrVA, LoanRepaymentFragment.price);
                setUnSelected();
                idTextviewRepaymentAtm.setSelected(true);
                idtextviewLineAtm.setSelected(true);
            }
        });

        RxView.clicks(idTextviewRepaymentOnline).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                String otherOnline = getResources().getString(online);
                setRepaymentStepsWords(otherOnline, paCodeOrVA, LoanRepaymentFragment.price);
                Logger.d(otherOnline + "-->" + paCodeOrVA);

                setUnSelected();
                idTextviewRepaymentOnline.setSelected(true);
                idtextviewLineOnline.setSelected(true);
            }
        });

        RxView.clicks(idTextviewRepaymentMbanking).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                String otherMBanking = getResources().getString(mbanking);
                setRepaymentStepsWords(otherMBanking, paCodeOrVA, LoanRepaymentFragment.price);

                setUnSelected();
                idTextviewRepaymentMbanking.setSelected(true);
                idtextviewLineMbanking.setSelected(true);
            }
        });

    }


    private void setRepaymentSteps(final int atm, final int online, final String paCodeOrVA) {
        RxView.clicks(idTextviewRepaymentAtm).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                String otherAtm = getResources().getString(atm);
                setRepaymentStepsWords(otherAtm, paCodeOrVA, LoanRepaymentFragment.price);
                setUnSelected();
                idTextviewRepaymentAtm.setSelected(true);
                idtextviewLineAtm.setSelected(true);
            }
        });

        RxView.clicks(idTextviewRepaymentOnline).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                String otherOnline = getResources().getString(online);
                setRepaymentStepsWords(otherOnline, paCodeOrVA, LoanRepaymentFragment.price);
                setUnSelected();
                idTextviewRepaymentOnline.setSelected(true);
                idtextviewLineOnline.setSelected(true);
            }
        });
    }

    private void setRepaymentStepsWords(final String otherAtm, final String paCodeOrVA) {

        setRepaymentStepsWords(otherAtm, paCodeOrVA, null);

    }

    private void setRepaymentStepsWords(final String otherAtm, final String paCodeOrVA, final String moneyCount) {
        idTextviewRepaymentSteps.post(new Runnable() {
            @Override
            public void run() {
                String format = String.format(otherAtm, paCodeOrVA, moneyCount);
//                format = idTextviewRepaymentSteps.justify(format);
                SpannableStringBuilder builder = new SpannableStringBuilder(format);
                builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAlerm_red)),
                        format.indexOf(paCodeOrVA), format.indexOf(paCodeOrVA) + paCodeOrVA.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (moneyCount != null && format.contains(moneyCount)) {
                    builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAlerm_red)),
                            format.indexOf(moneyCount), format.indexOf(moneyCount) + moneyCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                String doku = getString(R.string.text_special_doku);
                String mandiriNumber = getString(R.string.mandiri_hightlight_number);
                if (format.contains(doku)) {
                    builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAlerm_red)),
                            format.indexOf(doku),format.indexOf(doku) + doku.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if(format.contains(mandiriNumber)){
                    builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAlerm_red)), format.indexOf(mandiriNumber), format.indexOf(mandiriNumber) + mandiriNumber.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                idTextviewRepaymentSteps.setMovementMethod(new ScrollingMovementMethod());
                idTextviewRepaymentSteps.getParent().requestDisallowInterceptTouchEvent(true);
                idTextviewRepaymentSteps.setText(builder);
            }
        });
    }

    private void setUnSelected() {
        idTextviewRepaymentAtm.setSelected(false);
        idtextviewLineAtm.setSelected(false);

        idTextviewRepaymentOnline.setSelected(false);
        idtextviewLineOnline.setSelected(false);

        idTextviewRepaymentMbanking.setSelected(false);
        idtextviewLineMbanking.setSelected(false);
    }

    @OnClick(R.id.id_imagebutton_repayment_back)
    public void bankReBack() {
        finish();
    }

    @Override
    protected BankPayActPresenter initPresenterImpl() {
        return new BankPaymentActPresenterImpl();
    }
}
