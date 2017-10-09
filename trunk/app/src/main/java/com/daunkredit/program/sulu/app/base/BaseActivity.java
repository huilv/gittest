package com.daunkredit.program.sulu.app.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MenuItem;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenter;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.PageView;
import com.daunkredit.program.sulu.view.login.LoginActivity;
import com.daunkredit.program.sulu.widget.selfdefdialog.XLeoProgressDialog;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;

import java.util.List;

import javax.annotation.Nonnull;

import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public abstract class BaseActivity<T extends BaseActivityPresenter> extends FragmentActivity implements LifecycleProvider<ActivityEvent>, BaseActivityView {
    private static final int    LOGIN_INTERCEPT = 1005;
    public    T        mPresenter;
    protected Context  context;
    public    PageView pageView;

    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = initPresenterImpl();
        mPresenter.attach(this);
        initBeforeSetContentView();
        context = this;
        setContentView(getLayoutResId());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);
        init();
        this.lifecycleSubject.onNext(ActivityEvent.CREATE);
    }

    protected abstract int getLayoutResId();

    protected abstract void init();

    protected void initBeforeSetContentView() {
    }

    protected abstract T initPresenterImpl();


    @Override
    protected void onStart() {
        super.onStart();
        this.lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.lifecycleSubject.onNext(ActivityEvent.RESUME);
        pageView = new PageView(this.getClass().getName(), ActionType.PAGE);
        pageView.startPage();
        Logger.d(pageView);
    }

    @Override
    protected void onPause() {
        this.lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
        pageView.endPage();
        Logger.d(pageView);
    }

    @Override
    protected void onStop() {
        this.lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        this.lifecycleSubject.onNext(ActivityEvent.DESTROY);
        mPresenter.disAttach();
        mPresenter = null;
        super.onDestroy();
    }

    @Nonnull
    @Override
    public Observable<ActivityEvent> lifecycle() {
        return this.lifecycleSubject.asObservable();
    }

    @Nonnull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(this.lifecycleSubject, event);
    }

    @Nonnull
    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(this.lifecycleSubject);
    }

    public <T> Observable.Transformer<T, T> applySchedulers() {
        return new Observable.Transformer<T, T>() {

            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable
                        .unsubscribeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    //    protected void initToolBar(Toolbar toolbar) {
    //        if (toolbar != null) {
    //            setSupportActionBar(toolbar);
    //            toolbar.setTitleTextColor(Color.WHITE);
    //        }
    //        //        else{
    //        //            DialogUtil.showToastCust(this, R.string.msg_toolbar_no_found);
    //        //        }
    //    }
    //
    //    protected void initToolBarWithBack(Toolbar toolbar) {
    //        if (toolbar != null) {
    //            setSupportActionBar(toolbar);
    //            //toolbar.setNavigationIcon(R.drawable.ic_action_back);
    //            toolbar.setTitleTextColor(Color.WHITE);
    //            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    //        }
    //        //        else{
    //        //            DialogUtil.showToastCust(this,R.string.msg_toolbar_no_found);
    //        //        }
    //
    //    }
    //
    //    protected void initToolBarWithBack(Toolbar toolbar, String title) {
    //        if (toolbar != null) {
    //            setSupportActionBar(toolbar);
    //            //toolbar.setNavigationIcon(R.drawable.ic_action_back);
    //            toolbar.setTitleTextColor(Color.WHITE);
    //            super.setTitle(title);
    //            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    //        }
    //        //        else{
    //        //            DialogUtil.showToastCust(this,R.string.msg_toolbar_no_found);
    //        //        }
    //
    //    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // RegionLevel you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeTo(Class cls) {
        changeTo(cls, null);

    }

    public void changeToForResult(Class cls, int requestCode) {
        changeToForResult(cls, requestCode, false);
    }

    public void changeToForResult(Class cls, int requestCode, boolean checkLogin) {
        Intent intent = new Intent(this, cls);
        if (checkLogin && !isLogin()) {
            intent.putExtra(FieldParams.REQUEST_CODE, requestCode);
            changeTo(intent, true);
        } else {
            startActivityForResult(intent, requestCode);
        }
    }

    public void changeTo(Class cls, Bundle b) {

        Intent i = new Intent(this, cls);
        if (b != null) {
            i.putExtras(b);
        }
        startActivity(i);

    }

    public void changeTo(Class cls, String paramKey, Parcelable p) {
        Bundle b = new Bundle();
        b.putParcelable(paramKey, p);
        changeTo(cls, b);
    }


    /**
     * 检查登录状态的跳转，登录则跳转，否则留在本页
     *
     * @param intent
     * @param checkLogin 是否检查登录状态
     */
    public void changeTo(Intent intent, boolean checkLogin) {
        if (checkLogin) {
            if (!isLogin()) {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.putExtra(FieldParams.TEMP_INTENT, intent);
                startActivityForResult(loginIntent, LOGIN_INTERCEPT);
            } else {
                startActivity(intent);
            }
        } else {
            startActivity(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null) {
            return;
        }
        for (int i = 0; i < fragments.size(); i++) {
            if (!fragments.get(i).isHidden()) {
                fragments.get(i).onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_INTERCEPT) {
            if (isLogin() && data != null) {
                //XLeoToast.showMessage("reskip");
                Intent newIntent = data.getParcelableExtra(FieldParams.TEMP_INTENT);
                int requestCode2 = newIntent.getIntExtra(FieldParams.REQUEST_CODE, -100);
                if (requestCode2 == -100) {
                    startActivity(newIntent);
                } else {
                    startActivityForResult(newIntent, requestCode2);
                }
            } else {
                XLeoToast.showMessage(R.string.show_not_login_yet);
            }
        } else if (requestCode != -1) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments == null) {
                return;
            }
            for (int i = 0; i < fragments.size(); i++) {
                if (!fragments.get(i).isHidden()) {
                    fragments.get(i).onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }


    public boolean isLogin() {
        String token = TokenManager.getInstance().getToken();
        return !TextUtils.isEmpty(token);
    }

    AlertDialog progressDialog;

    private AlertDialog createProgressDialog(Activity activity) {
        AlertDialog progressDialog = new XLeoProgressDialog(activity);
        //        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public void showLoading(String message) {
        if (progressDialog == null) {
            progressDialog = createProgressDialog(this);
        }
        progressDialog.setMessage(message);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public boolean isDialogShowing() {
        if (progressDialog == null) {
            return false;
        }
        return progressDialog.isShowing();
    }

    public void hideLoading() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    public void dismissLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public String getStringById(int id) {
        return getResources().getText(id).toString();
    }

    public void initYW() {
        mPresenter.initYW();
    }
}
