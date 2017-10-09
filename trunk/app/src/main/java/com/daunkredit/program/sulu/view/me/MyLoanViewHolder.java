package com.daunkredit.program.sulu.view.me;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.bean.HistoryLoanAppInfoBean;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.utils.StringFormatUtils;
import com.daunkredit.program.sulu.common.utils.TimeFormat;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.sulu.kotlin.activity.LoanInfoActivity;


/**
 * @作者:XJY
 * @创建日期: 2017/3/15 11:49
 * @描述:MyLoan功能模块Adapter的ViewHolder
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */
public class MyLoanViewHolder extends RecyclerView.ViewHolder{

    TextView mRppId;
    TextView mAmount;
    TextView mStatus;
    TextView mTime;
    TextView mRepaymentTime;

    public MyLoanViewHolder(View itemView) {
        super(itemView);
        mRppId = (TextView) itemView.findViewById(R.id.tv_item_myloan_id);
        mAmount = (TextView) itemView.findViewById(R.id.tv_item_myloan_loanamount);
        mStatus = (TextView) itemView.findViewById(R.id.tv_item_myloan_status);
        mTime = (TextView) itemView.findViewById(R.id.tv_item_myloan_time);
        mRepaymentTime = (TextView) itemView.findViewById(R.id.tv_item_myloan_repayment_time);
    }

    public void injectView(final Context ctx, final HistoryLoanAppInfoBean historyLoanAppInfoBean) {
        mTime.setText(TimeFormat.convertTimeDay(historyLoanAppInfoBean.getCreateTime()));
        mRppId.setText(historyLoanAppInfoBean.getLoanAppId() + "");
        mAmount.setText(StringFormatUtils.indMoneyFormat(historyLoanAppInfoBean.getAmount()));
        mRepaymentTime.setText(StringFormatUtils.dayTimeFormat(ctx,historyLoanAppInfoBean.getPeriod()));
        final String status = historyLoanAppInfoBean.getStatus();
        if (TextUtils.equals(FieldParams.LoanStatus.REJECTED,status)||TextUtils.equals(FieldParams.LoanStatus.WITHDRAWN,status)||TextUtils.equals(FieldParams.LoanStatus.SUBMITTED,status)||
                TextUtils.equals(FieldParams.LoanStatus.PAID_OFF,status) || TextUtils.equals(FieldParams.LoanStatus.CLOSED,status)) {
            mStatus.setTextColor(ctx.getResources().getColor(R.color.color_tab_text));
        }else{
            mStatus.setTextColor(ctx.getResources().getColor(R.color.color_red));
        }
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UserEventQueue.add(new ClickEvent(v.toString(), ActionType.CLICK, historyLoanAppInfoBean.getLoanAppId().toString()));
//                if (TextUtils.equals(FieldParams.LoanStatus.WITHDRAWN,status)) {
//                    ToastManager.showToast(ctx.getString(R.string.show_already_colosed));
//                }else if(TextUtils.equals(FieldParams.LoanStatus.SUBMITTED,status)){
//                    ToastManager.showToast(ctx.getString(R.string.show_submitted));
//                }else if(TextUtils.equals(FieldParams.LoanStatus.PAID_OFF,status)){
//                    ToastManager.showToast(ctx.getString(R.string.show_paid_off));
//                }else{
//                    TokenManager instance = TokenManager.getInstance();
//                    instance.storeMessage(FieldParams.SHOW_MY_LOAN_INFO,true);
//                    instance.storeMessage(FieldParams.LOAN_APP_INFO_BEAN,historyLoanAppInfoBean);
//                    if (ctx instanceof Activity) {
//                        ((Activity) ctx).finish();
//                    }else{
//                        ctx.startActivity(new Intent(ctx, MainActivity.class));
//                    }
//                }
                TokenManager.putMessage(FieldParams.SHOW_MY_LOAN_INFO,historyLoanAppInfoBean);
                ctx.startActivity(new Intent(ctx,LoanInfoActivity.class));
            }
        });
        if (TextUtils.equals(FieldParams.LoanStatus.WITHDRAWN,status)) {
            mStatus.setAllCaps(true);
            mStatus.setText(R.string.text_cancel);
        }else {
            mStatus.setText(status);
        }
    }
}
