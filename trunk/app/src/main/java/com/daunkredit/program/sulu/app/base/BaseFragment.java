package com.daunkredit.program.sulu.app.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.presenter.BaseFragmentPresenter;
import com.daunkredit.program.sulu.common.utils.XLeoSp;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.PageView;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.MsgBoxActivity;
import com.daunkredit.program.sulu.view.login.LoginActivity;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/5/31 0031.
 * <p>
 * 基类fragment
 *
 * @author tj
 */
public abstract class BaseFragment<T extends BaseFragmentPresenter> extends Fragment implements BaseFragmentView {
    @BindView(R.id.id_imagebutton_back)
    @Nullable ImageButton idImagebuttonBack;
    @BindView(R.id.id_textview_title)
    @Nullable TextView idTextviewTitle;
    @BindView(R.id.id_imagebutton_info_list)
    @Nullable ImageButton idImagebuttonInfoList;
    @BindView(R.id.id_main_top)
    @Nullable RelativeLayout idMainTop;
    @Nullable LinearLayout llFragmentBase;
    private Unbinder unbinder;
    protected BaseActivity mActivity;
    protected LayoutInflater inflater;

    protected T mPresenter;

    public PageView pageView;

    @Override
    public BaseActivity getBaseActivity() {
        return mActivity;
    }


    protected abstract T initPresenter();

    /**
     * 获得全局的，防止使用getActivity()为空
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (BaseActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Optional
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPresenter = initPresenter();
        mPresenter.attach(this);
        this.inflater = inflater;

        View rootView = null;
        View view = LayoutInflater.from(mActivity).inflate(getLayoutId(), container, false);
        if (doPreBuildHeader()) {
            rootView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_base, container, false);
            llFragmentBase = (LinearLayout) rootView.findViewById(R.id.ll_fragment_base);
            llFragmentBase.addView(view,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            rootView = view;
        }
        unbinder = ButterKnife.bind(this, rootView);
        if (doPreBuildHeader()) {
            if (!initHeader(idTextviewTitle)) {
                idImagebuttonInfoList.setVisibility(View.GONE);
            }else{
                idImagebuttonInfoList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserEventQueue.add(new ClickEvent(idImagebuttonInfoList.toString(), ActionType.CLICK));
                        idImagebuttonInfoList.setActivated(false);
                        XLeoSp.getInstance(mActivity).remove(FieldParams.PageStatus.HAS_NEW_MESSAGE);
                        Intent intent = new Intent(mActivity, MsgBoxActivity.class);
                        startActivity(intent);
                    }
                });
                initPageStatus();
            }
            if (getBackPressListener() != null) {
                idImagebuttonBack.setOnClickListener(getBackPressListener());
            }else{
                idImagebuttonBack.setVisibility(View.GONE);
            }
        }
        initView(view, savedInstanceState);
        initData();

        //监听第一次显示
        pageView = new PageView(this.getClass().getName(), ActionType.PAGE);
        pageView.startPage();
        Logger.d(pageView);

        return rootView;
    }

    @Optional
    public void informMessage(){
        if (idImagebuttonInfoList != null) {
            idImagebuttonInfoList.setActivated(true);
        }
    }
    @Optional
    private void initPageStatus() {
//        XLeoSp.getInstance(this).putBoolean(FieldParams.PageStatus.HAS_NEW_MESSAGE,true);
        boolean aBoolean = XLeoSp.getInstance(mActivity).getBoolean(FieldParams.PageStatus.HAS_NEW_MESSAGE);
        idImagebuttonInfoList.setActivated(aBoolean);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                sendBroadcast(new Intent(FieldParams.BroadcastAction.NEW_MESSAGE));
//                mHandler.postDelayed(this, 1000);
//            }
//        },1000);
    }

    /**
     * set back button OnClickListener
     * @return Onclicklistener or null present no back button default no back button
     */
    protected  View.OnClickListener getBackPressListener(){
     return null;
    }

    /**
     *
     * @param view header to set
     * @return has info icon or not
     */
    protected  boolean initHeader(TextView view){
        view.setText(R.string.app_name);
        return true;
    }

    @Override
    public void toLogin() {
        startActivity(new Intent(getContext(), LoginActivity.class));
    }

    /**
     *
     * @return prebuildHeader or not
     */
    protected  boolean doPreBuildHeader(){
        return false;
    }

    /**
     * 该抽象方法就是 onCreateView中需要的layoutID
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 该抽象方法就是 初始化view
     *
     * @param view
     * @param savedInstanceState
     */
    protected abstract void initView(View view, Bundle savedInstanceState);

    /**
     * 执行数据的加载
     */
    protected abstract void initData();

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            pageView.endPage();
            Logger.d(pageView);
        } else {
            pageView = new PageView(this.getClass().getName(), ActionType.PAGE);
            pageView.startPage();
            Logger.d(pageView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mPresenter.detach();
        mPresenter = null;
    }

}