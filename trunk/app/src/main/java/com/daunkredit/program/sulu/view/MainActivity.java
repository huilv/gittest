package com.daunkredit.program.sulu.view;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.app.base.BaseFragment;
import com.daunkredit.program.sulu.bean.LatestLoanAppBean;
import com.daunkredit.program.sulu.bean.LoanAppBeanFather;
import com.daunkredit.program.sulu.bean.TotalAmount;
import com.daunkredit.program.sulu.bean.VersionBean;
import com.daunkredit.program.sulu.common.EventCollection;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.enums.LoanStatus;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.service.SuluMainService;
import com.daunkredit.program.sulu.view.fragment.CertificationFragmentProgress;
import com.daunkredit.program.sulu.view.fragment.LoanInProcessFragment;
import com.daunkredit.program.sulu.view.fragment.LoanNormalFragment2;
import com.daunkredit.program.sulu.view.fragment.LoanRepaymentExpiryFragment;
import com.daunkredit.program.sulu.view.fragment.LoanRepaymentFragment;
import com.daunkredit.program.sulu.view.fragment.LoaningFragment;
import com.daunkredit.program.sulu.view.fragment.MeFragment;
import com.daunkredit.program.sulu.view.login.LoginActivity;
import com.daunkredit.program.sulu.view.presenter.MainActPreImp;
import com.daunkredit.program.sulu.view.presenter.MainActPresenter;
import com.daunkredit.program.sulu.widget.selfdefdialog.DialogManager;
import com.daunkredit.program.sulu.widget.selfdefdialog.XLeoProgressDialog;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.orhanobut.logger.Logger;
import com.sulu.kotlin.fragment.LockViewFragment;
import com.sulu.kotlin.fragment.OnlineServiceFragment;
import com.tencent.bugly.crashreport.CrashReport;

import butterknife.BindView;
import butterknife.OnClick;

import static com.daunkredit.program.sulu.enums.LoanStatus.LOANED;
import static com.daunkredit.program.sulu.enums.LoanStatus.LOANINPROCESS;
import static com.daunkredit.program.sulu.enums.LoanStatus.UNLOAN;


public class MainActivity extends BaseActivity<MainActPresenter> implements MainActView {


    private static final String TAG = "MainActivity";

    private static final int TABLOAN = 0;
    private static final int TABCERT = 1;
    private static final int TABME = 2;
    private static final int TABONLINE = 3;

    private static final int LOANING = 4;

    @BindView(R.id.id_textview_tab_loan)
    TextView textViewLoan;
    @BindView(R.id.id_textview_tab_certification)
    TextView textViewCertification;
    @BindView(R.id.id_textview_tab_me)
    TextView textViewMe;
    @BindView(R.id.id_textview_tab_online_qa)
    TextView textViewOnlineQA;

    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    LoanNormalFragment2 unLoanFrag;
    LoaningFragment loaningFrag;

    LoanInProcessFragment loanInProcessFrag;

    LoanRepaymentFragment repaymentFrag;
    LoanRepaymentExpiryFragment repaymentExpiryFrag;

    CertificationFragmentProgress certFrag;
    MeFragment meFrag;

    public static TotalAmount selectedTotalAmount;

    public static Boolean isSubmitVisible = false;
    private ServiceConnection mConn;
    private BroadcastReceiver myReciver;
    private int mSelect;
    private LoanStatus mTempStatus;
    private BaseFragment mCurrentFragment;
    private OnlineServiceFragment mOnlineServiceFrag;
    private BaseFragment mTestFragment;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        TokenManager instance = TokenManager.getInstance();
        Object message = instance.getAndRemove(FieldParams.SHOW_MY_LOAN_INFO);
        if (message != null && message instanceof Boolean && (boolean) message) {
            LoanAppBeanFather loanBean = (LoanAppBeanFather) instance.getMessage(FieldParams.LOAN_APP_INFO_BEAN);
            showLoanInfoFragment(loanBean);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            Object o = intent.getExtras().get(FieldParams.MainActivity.TAB_CHOOSE);
            if(o!=null&& ((int)o) != -1){
                setSelect((int)o);
            }
        }
    }

    @Override
    protected void init() {
        RxBus.get().register(this);
        doBindService();
        bindBroadcastReciver();
        setSelectLoan();
        mPresenter.initThirdService();
    }

    @Override
    protected MainActPresenter initPresenterImpl() {
        return new MainActPreImp();
    }


    private void bindBroadcastReciver() {
        myReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (FieldParams.BroadcastAction.BROADCASTACTION.equals(action)) {
                    TokenManager instance = TokenManager.getInstance();
                    LatestLoanAppBean latestLoanAppBean = (LatestLoanAppBean) instance.getMessage(FieldParams.LATESTBEAN);
                    if (latestLoanAppBean == null) {
                        return;
                    }
                    mPresenter.dealResult(latestLoanAppBean.getStatus());
                    if (mSelect == TABLOAN) {
                        setSelect(TABLOAN);
                    }
                } else if (FieldParams.BroadcastAction.BROADCASTACTION_UPDATE.equals(action)) {
                    VersionBean versonBean = intent.getParcelableExtra(FieldParams.UPDATE_VERSON_BEAN);
                    mPresenter.updateApk(versonBean);
                } else if (FieldParams.BroadcastAction.NEW_MESSAGE.equals(action)) {
                    showMessageUpdate();
                }
            }
        };
        IntentFilter filter = new IntentFilter(FieldParams.BroadcastAction.BROADCASTACTION);
        filter.addAction(FieldParams.BroadcastAction.BROADCASTACTION_UPDATE);
        filter.addAction(FieldParams.BroadcastAction.NEW_MESSAGE);
        registerReceiver(myReciver, filter);
    }

    private void showMessageUpdate() {
        if (mCurrentFragment != null) {
            mCurrentFragment.informMessage();
        }
    }

    private void doBindService() {
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        bindService(new Intent(this, SuluMainService.class), mConn, Context.BIND_AUTO_CREATE);
    }


    private void showLoanInfoFragment(LoanAppBeanFather bean) {
        String status = bean.getStatus();
        if (FieldParams.LoanStatus.EXPIRED.equals(status) || FieldParams.LoanStatus.OVERDUE.equals(status)) {
            mTempStatus = LoanStatus.EXPIRED;
        } else if (FieldParams.LoanStatus.LOANDED.equals(status)) {
            mTempStatus = LOANED;
        } else if (FieldParams.LoanStatus.IN_REVIEW.equals(status)) {
            mTempStatus = LoanStatus.LOANINPROCESS;
        } else if (FieldParams.LoanStatus.SUBMITTED.equals(status)) {
            mTempStatus = LoanStatus.UNLOAN;
        }
        setSelect(TABLOAN);
    }


    public void setSelect(int i) {

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();


        switch (i) {
            case TABLOAN:
                LoanStatus loanstatus = (LoanStatus) TokenManager.getInstance().getMessage(FieldParams.LOANSTATUS);
                reSet();
                hideFragment(transaction);
                if (mTempStatus != null) {
                    setLoanFragmentByState(transaction, mTempStatus);
                    mTempStatus = null;
                } else {
                    setLoanFragmentByState(transaction, loanstatus);
                }

                textViewLoan.setSelected(true);
                break;
            case TABCERT:
                reSet();
                hideFragment(transaction);
                if (certFrag == null) {
                    certFrag = new CertificationFragmentProgress();
                    transaction.add(R.id.id_fragment_loan, certFrag);
                    Log.d(TAG, "setSelect: 1 new fragment certFrag");
                } else {
                    transaction.show(certFrag);
                    Log.d(TAG, "setSelect: 1 show fragment certFrag");
                }
                mCurrentFragment = certFrag;
                textViewCertification.setSelected(true);
                break;
            case TABME:
                reSet();
                hideFragment(transaction);
                if (meFrag == null) {
                    meFrag = new MeFragment();
                    transaction.add(R.id.id_fragment_loan, meFrag);
                    Logger.d("SetSelect 2, new fragment of me");
                } else {
                    transaction.show(meFrag);
                    Logger.d("setSelect 2,show me fragment");
                }
                mCurrentFragment = meFrag;
                textViewMe.setSelected(true);
                break;
            case TABONLINE:
//                initYW();
               // testOnly();
                openOnlineService();
                break;
            default:
                break;
        }
        transaction.commitAllowingStateLoss();
        mSelect = i;
    }

    private void openOnlineService() {
        reSet();
        hideFragment(transaction);
        if (mOnlineServiceFrag == null) {
            mOnlineServiceFrag = new OnlineServiceFragment();
            transaction.add(R.id.id_fragment_loan, mOnlineServiceFrag);
            Logger.d("SetSelect 3, new fragment of OnlineServiceFrag");
        } else {
            transaction.show(mOnlineServiceFrag);
            Logger.d("setSelect 3,show onlineservice fragment");
        }
        mCurrentFragment = mOnlineServiceFrag;
        textViewOnlineQA.setSelected(true);
    }

    private void testOnly() {
        reSet();
        hideFragment(transaction);
        if (mTestFragment == null) {
            mTestFragment = new LockViewFragment();
            transaction.add(R.id.id_fragment_loan, mTestFragment);
            Logger.d("SetSelect 3, new fragment of OnlineServiceFrag");
        } else {
            transaction.show(mTestFragment);
            Logger.d("setSelect 3,show onlineservice fragment");
        }
        mCurrentFragment = mTestFragment;
        textViewOnlineQA.setSelected(true);

    }

    private void setLoanFragmentByState(FragmentTransaction transaction, LoanStatus status) {
        if (transaction == null) {
            return;
        }
        if (status == null || UNLOAN == status || LoanStatus.SUBMITTED == status) {

            if (unLoanFrag == null) {
                unLoanFrag = new LoanNormalFragment2();
                transaction.add(R.id.id_fragment_loan, unLoanFrag);
            } else {
                transaction.show(unLoanFrag);
            }
            mCurrentFragment = unLoanFrag;
        } else if (LOANINPROCESS == status) {

            if (loanInProcessFrag == null) {
                loanInProcessFrag = new LoanInProcessFragment();
                transaction.add(R.id.id_fragment_loan, loanInProcessFrag);
            } else {
                transaction.show(loanInProcessFrag);
            }
            mCurrentFragment = loanInProcessFrag;
        } else if (LoanStatus.LOANED == status) {
            if (repaymentFrag == null) {
                repaymentFrag = new LoanRepaymentFragment();
                transaction.add(R.id.id_fragment_loan, repaymentFrag);
            } else {
                transaction.show(repaymentFrag);
            }
            mCurrentFragment = repaymentFrag;
        } else if (LoanStatus.EXPIRED == status) {
            if (repaymentExpiryFrag == null) {
                repaymentExpiryFrag = new LoanRepaymentExpiryFragment();
                transaction.add(R.id.id_fragment_loan, repaymentExpiryFrag);
            } else {
                transaction.show(repaymentExpiryFrag);
            }
            mCurrentFragment = repaymentExpiryFrag;
        } else {
            if (unLoanFrag == null) {
                unLoanFrag = new LoanNormalFragment2();
                transaction.add(R.id.id_fragment_loan, unLoanFrag);
            } else {
                transaction.show(unLoanFrag);
            }
            mCurrentFragment = unLoanFrag;
        }
    }


    private void reSet() {
        textViewLoan.setSelected(false);
        textViewCertification.setSelected(false);
        textViewMe.setSelected(false);
        textViewOnlineQA.setSelected(false);
    }

    //show hide way
    private void hideFragment(FragmentTransaction transaction) {
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
    }

    @Subscribe
    public void setLoaningFragment(TotalAmount totalAmount) {
        selectedTotalAmount = totalAmount;

        Logger.d("Set Loaning Fragment");
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);
        setSelect(TABCERT);
    }

    @Subscribe
    public void onTokenExpried(TokenManager.TokenExpiredEvent event) {
        Logger.d("TokenExpired need to login");
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Subscribe
    public void onSelectLoanStatus(EventCollection.ReSetLoanStatusEvent event) {
        Logger.d("LoginActivity.ReSetLoanStatusEvent");
        String token = TokenManager.getInstance().getToken();
        if (token != null)
            mPresenter.updateStatus(token);
    }

    @Subscribe
    public void onSelectLoaningFragment(CertificationFragmentProgress.GotoLoaning event) {

        Logger.d("Set Loaning Fragment");
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        reSet();
        hideFragment(transaction);
        if (loaningFrag == null) {
            loaningFrag = new LoaningFragment();
            transaction.add(R.id.id_fragment_loan, loaningFrag);
        } else {
            transaction.show(loaningFrag);
        }
        mCurrentFragment = loaningFrag;
        transaction.commit();
    }


    /**
     * Listener of the Loan success event
     *
     * @param event
     */
    @Subscribe
    public void onLoanSuccess(EventCollection.LoanSuccess event) {
        TokenManager.getInstance().storeMessage(FieldParams.LOANSTATUS, LoanStatus.LOANINPROCESS);
        mPresenter.updateStatus(TokenManager.getInstance().getToken());
    }

    /**
     * Listener of thr Log out Event
     */
    @Subscribe
    public void onLogOut(EventCollection.LogoutEvent event) {
        TokenManager.getInstance().storeMessage(FieldParams.LOANSTATUS, LoanStatus.UNLOAN);
        setSelectLoan();
    }

    @Subscribe
    public void onVideoGiveUp(EventCollection.VideoGiveUp event) {
        mPresenter.updateStatus(TokenManager.getInstance().getToken());
    }

    @Subscribe
    public void onGiveUpLogin(EventCollection.GiveUpLogin event) {
        TokenManager.getInstance().storeMessage(FieldParams.LOANSTATUS, LoanStatus.UNLOAN);
        setSelect(TABLOAN);
    }

    @Subscribe
    public void onCancelLoan(EventCollection.CancelLoan event) {
        mPresenter.updateStatus(TokenManager.getInstance().getToken());
        TokenManager.getInstance().storeMessage(FieldParams.LOANSTATUS, LoanStatus.UNLOAN);
        setSelect(TABLOAN);
    }


    //    if ("CURRENT".equals(approved)) {
    //        titleTv.setTextColor(getResources().getColor(R.color.colorPrimary));
    //        title = getString(R.string.loan_result_title_approved);
    //        tips = getString(R.string.loan_result_tip_approved);
    //    } else
    public void showTipsDialog(String approved) {
        final Dialog dialog;
        if ("REJECTED".equals(approved)) {
            View view = View.inflate(this, R.layout.dialog_loan_result_failed, null);
            final Button buttonTv = (Button) view.findViewById(R.id.btn_result_failed);
            dialog = DialogManager.newDialog(this, view, false);
            buttonTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK,buttonTv.getText().toString()));
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else if ("CURRENT".equals(approved)) {
            View view = View.inflate(this, R.layout.dialog_show_current_success, null);
            dialog = new XLeoProgressDialog(this);
            view.findViewById(R.id.btn_dialog_current_oko).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            dialog.setContentView(view);
        } else if ("CLOSED".equals(approved)) {
            View view = View.inflate(this, R.layout.dialog_closed, null);
            dialog = new XLeoProgressDialog(this);
            TextView textView = (TextView) view.findViewById(R.id.id_textview_closed_dialog_content);

            final LatestLoanAppBean latestLoanAppBean = (LatestLoanAppBean) TokenManager.checkoutMessage(FieldParams.LATESTBEAN);

            String[] closedReasons = latestLoanAppBean.getComments().split(",");
            String closedReasonsStr = "";
            for (int j = 1; j <= closedReasons.length; j++) {
                closedReasonsStr = closedReasonsStr + j + ". " + closedReasons[j - 1] + "\n";
            }
            textView.setText(closedReasonsStr);

            view.findViewById(R.id.id_button_goto_modify).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    TotalAmount loanData = (TotalAmount) TokenManager.checkoutMessage(FieldParams.TO_LOAN_DATA);
                    loanData.setAmount((long) latestLoanAppBean.getAmount());
                    loanData.setDay(latestLoanAppBean.getPeriod());
                    MainActivity.isSubmitVisible = true;
                    RxBus.get().post(loanData);
                }
            });

            dialog.show();
            dialog.setContentView(view);

        }
    }


    @OnClick(R.id.id_textview_tab_loan)
    public void setSelectLoan() {
        UserEventQueue.add(new ClickEvent(textViewLoan.toString(), ActionType.CLICK, textViewLoan.getText().toString()));

        TokenManager tokenManager = TokenManager.getInstance();
        if (!tokenManager.hasLogin()) {
            Logger.d("Not get token, please login");
            tokenManager.storeMessage(FieldParams.LOANSTATUS, LoanStatus.UNLOAN);
            setSelect(TABLOAN);
        } else {
            showLoading(null);
//            int sleepTime = 0;
//            while (tokenManager.getMessage(FieldParams.LATESTBEAN) == null && sleepTime < 500) {
//                SystemClock.sleep(10);
//                sleepTime += 10;
//            }
//            if (sleepTime >= 500) {
            //                XLeoToast.showMessage(R.string.show_net_lame);
            mPresenter.updateStatus(tokenManager.getToken());
//            } else {
//                dismissLoading();
//                setSelect(TABLOAN);
//            }
        }

    }

    @OnClick(R.id.id_textview_tab_certification)
    public void setSelectCertification() {
        UserEventQueue.add(new ClickEvent(textViewCertification.toString(), ActionType.CLICK, textViewCertification.getText().toString()));

        isSubmitVisible = false;
        setSelect(TABCERT);
    }

    @OnClick(R.id.id_textview_tab_me)
    public void setSelectMe() {
        UserEventQueue.add(new ClickEvent(textViewMe.toString(), ActionType.CLICK, textViewMe.getText().toString()));
        setSelect(TABME);
    }

    @OnClick(R.id.id_textview_tab_online_qa)
    public void setSelectOnlineQa() {
        UserEventQueue.add(new ClickEvent(textViewOnlineQA.toString(), ActionType.CLICK, textViewOnlineQA.getText().toString()));
        setSelect(TABONLINE);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //          super.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
        unbindService(mConn);
        unregisterReceiver(myReciver);
    }
}