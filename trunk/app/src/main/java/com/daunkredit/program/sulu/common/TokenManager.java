package com.daunkredit.program.sulu.common;

import com.daunkredit.program.sulu.app.App;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.bean.LatestLoanAppBean;
import com.daunkredit.program.sulu.bean.LoginStatusBean;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.enums.LoginStatusEnum;
import com.hwangjr.rxbus.Bus;

import org.afinal.simplecache.ACache;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class TokenManager {


    public static String MOBILE_CACHE_KEY        = "mobile_cache_key";
    public static String TOKEN_CACHE_KEY         = "token_cache_key";
    public static String REFRESH_TOKEN_CACHE_KEY = "refresh_token_cahce_key";

    public static boolean isExpired         = false;
    public static boolean isReSetLoanStatus = false;
    public  long            mLoginTime;
    private LoginStatusBean mLoginStatus;

    public Map<String, Object> mTempStatus;

    Bus    bus;
    ACache cache;

    public static TokenManager getInstance() {
        return Holder.INSTANCE;
    }

    public static void putMessage(String key, Object obj) {
        Holder.INSTANCE.storeMessage(key, obj);
    }

    public static void putMessageToFile(String key, String obj) {
        Holder.INSTANCE.cache.put(key, obj);
    }

    public static String checkoutMessageFromFile(String key) {
        return Holder.INSTANCE.cache.getAsString(key);
    }

    public static Object checkoutMessage(String key) {
        return Holder.INSTANCE.getMessage(key);
    }

    public static <T> T checkoutMessage(String key, Class<T> clazz) {
       return checkoutMessage(key,clazz,false);
    }


    public static Object removeMessage(String key) {
        return Holder.INSTANCE.getAndRemove(key);
    }

    public void setMobile(String mobile) {
        if (!mobile.startsWith("0")) {
            mobile = "0" + mobile;
        }
        cache.put(MOBILE_CACHE_KEY, mobile);
    }

    public String getMobile() {
        return cache.getAsString(MOBILE_CACHE_KEY);
    }

    public void setToken(String token, int time) {
        if (time <= 0) {
            cache.put(TOKEN_CACHE_KEY, token);
        } else {
            cache.put(TOKEN_CACHE_KEY, token, time);
        }
        long l = System.currentTimeMillis();
        mLoginStatus.mChangTime = l;
        mLoginTime = l;
        mLoginStatus.mLoginStatus = LoginStatusEnum.UNLOGIN_LOGIN;
        isExpired = false;
    }

    public void setRefreshToken(String refreshToken, int time) {

        if (time <= 0) {
            cache.put(REFRESH_TOKEN_CACHE_KEY, refreshToken);
        } else {
            cache.put(REFRESH_TOKEN_CACHE_KEY, refreshToken, time);
        }
    }

    public String getToken() {
        return cache.getAsString(TOKEN_CACHE_KEY);
    }

    public String getRefreshToken() {
        return cache.getAsString(REFRESH_TOKEN_CACHE_KEY);
    }

    private TokenManager() {
        bus = new Bus();
        cache = ACache.get(App.instance);
        mTempStatus = Collections.synchronizedMap(new HashMap<String, Object>());
        mLoginStatus = new LoginStatusBean();
    }

    /**
     * 检查登录状态是否变化，确定是否需要更新界面
     *
     * @param lastUpdateTime 上次获取状态时间
     * @return
     */
    public boolean isLoginStateChange(long lastUpdateTime) {
        return mLoginStatus.mChangTime > lastUpdateTime;
    }

    /**
     * 获取具体变化方式
     *
     * @return
     */
    public LoginStatusEnum getLoginStateChange() {
        return mLoginStatus.mLoginStatus;
    }

    public synchronized void storeMessage(String key, Object message) {

        if (key != null) {
            mTempStatus.put(key, message);
        }
    }

    /**
     * 获取暂存的信息，不删除
     *
     * @param key
     * @return
     */
    public synchronized Object getMessage(String key) {
        if (key == null) {
            return null;
        }
        return mTempStatus.get(key);
    }

    /**
     * 获取暂存的信息并删除
     *
     * @param key
     * @return
     */
    public synchronized Object getAndRemove(String key) {
        if (key == null) {
            return null;
        }
        return mTempStatus.remove(key);
    }

    public static void removeMessageFromFile(String username) {
        Holder.INSTANCE.cache.remove(username);
    }

    public void addToNextPull(@NotNull Function1<? super LatestLoanAppBean, Unit> block) {
        block.invoke(new LatestLoanAppBean());
    }

    public void requestLatestLoanData(@NotNull Function1<? super LatestLoanAppBean, Unit> block) {
        block.invoke(new LatestLoanAppBean());
    }

    public <T> T getMessage(String key, Class<? extends T> clazz) {
        Object message = getMessage(key);
        if (message != null && message.getClass().isAssignableFrom(clazz)) {
            return clazz.cast(message);
        }
        return null;
    }

    public static void storeYWAccount(String userid, String password) {
        putMessage(FieldParams.YW_ACCOUNT_USERID,userid + Holder.INSTANCE.getMobile());
        putMessage(FieldParams.YW_ACCOUNT_PASSWORD,password);
    }

    public static String getYWAccountID() {
        String s = TokenManager.checkoutMessage(FieldParams.YW_ACCOUNT_USERID, String.class);
        if (s != null && s.contains(getInstance().getMobile())) {
            return s.replace(getInstance().getMobile(), "");
        }
        return null;
    }

    public static  <T> T checkoutMessage(String key, Class<T> clazz, boolean doRemove) {
        Object message;
        if (doRemove) {
            message = Holder.INSTANCE.getAndRemove(key);
        }else{
            message= Holder.INSTANCE.getMessage(key);
        }

        if (message != null && message.getClass().isAssignableFrom(clazz)) {
            return clazz.cast(message);
        }
        return null;
    }

    private static class Holder {
        private static final TokenManager INSTANCE = new TokenManager();
    }

    Subscription subscription;

    /**
     * 需要重新登录
     */
    public void refreshTokeExpired() {
        clear();
        subscription =
                Observable.just(1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                bus.post(new TokenExpiredEvent());

                                if (subscription != null && !subscription.isUnsubscribed()) {
                                    subscription.unsubscribe();
                                }
                            }
                        });
    }

    public void clear() {
        cache.remove(TOKEN_CACHE_KEY);
        cache.remove(REFRESH_TOKEN_CACHE_KEY);
        cache.remove(MOBILE_CACHE_KEY);
        mLoginStatus.mChangTime = System.currentTimeMillis();
        mLoginStatus.mLoginStatus = LoginStatusEnum.LOGIN_LOGOUT;
    }

    public boolean hasLogin() {
        return getToken() != null && !isExpired;
    }

    public static class TokenExpiredEvent {
    }

    public static class DataUpdateManager {
        private static Map<String, Long>                   timeStamp    = Collections.synchronizedMap(new LinkedHashMap<String, Long>(2));
        private static List<Subscriber<LatestLoanAppBean>> sSubscribers = Collections.synchronizedList(new LinkedList<Subscriber<LatestLoanAppBean>>());


        public static void requestLoanLatestData(long delayTime, Subscriber<LatestLoanAppBean> subscription) {
            LoggerWrapper.d("requestLoanLatestData");
            String token = getInstance().getToken();
            if (token == null) {
                return;
            }
            long startTime = System.currentTimeMillis();
            Long storedStartTime = timeStamp.get(FieldParams.DATA_OBTAIN_START_TIME);
            LatestLoanAppBean latestLoanAppBean = checkoutMessage(FieldParams.LATESTBEAN, LatestLoanAppBean.class);
            Long storedEndTime = timeStamp.get(FieldParams.DATA_OBTAIN_END_TIME);
            //            if (storedEndTime != null && storedStartTime != null && storedEndTime > storedStartTime && latestLoanAppBean != null && startTime - storedStartTime < delayTime / 2) {
            //                LoggerWrapper.d("onnext:directreturn");
            //                subscription.onNext(latestLoanAppBean);
            //            } else
            if (storedStartTime != null && latestLoanAppBean != null && startTime - storedStartTime < delayTime / 2) {
                LoggerWrapper.d("onnext:addtocurrentcall");
                sSubscribers.add(subscription);
            } else {
                LoggerWrapper.d("onnext:startnewcall");
                timeStamp.put(FieldParams.DATA_OBTAIN_START_TIME, startTime);
                sSubscribers.add(subscription);
                ServiceGenerator.createService(UserApi.class).getLatestLoanApp(token, "tokenManager")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<LatestLoanAppBean>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                for (Subscriber<LatestLoanAppBean> subscriber : sSubscribers) {
                                    subscriber.onError(e);
                                }
                                sSubscribers.clear();
                            }

                            @Override
                            public void onNext(LatestLoanAppBean latestLoanAppBean) {
                                LoggerWrapper.d("onnext");
                                timeStamp.put(FieldParams.DATA_OBTAIN_END_TIME, System.currentTimeMillis());
                                for (Subscriber<LatestLoanAppBean> subscriber : sSubscribers) {
                                    subscriber.onNext(latestLoanAppBean);
                                }
                                sSubscribers.clear();
                            }
                        });
            }
        }
    }
}
