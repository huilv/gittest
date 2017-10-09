package com.daunkredit.program.sulu.view.certification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.FieldParams;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.bean.RegionBean;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.view.certification.presenter.PersonalInfoActPreImpl;
import com.daunkredit.program.sulu.view.certification.presenter.PersonalInfoActPresenter;
import com.daunkredit.program.sulu.view.certification.status.RegionLevel;
import com.daunkredit.program.sulu.widget.xleotoast.XLeoToast;
import com.hwangjr.rxbus.RxBus;
import com.sulu.kotlin.fragment.AreaActivityInterface;
import com.sulu.kotlin.fragment.AreaFragment;
import com.sulu.kotlin.fragment.PersonalInfoFragment;
import com.x.leo.rollview.FragmentAdapter;
import com.x.leo.rollview.OnFragmentNeeded;
import com.x.leo.rollview.UnDragableViewPager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by Miaoke on 2017/2/27.
 */

public class PersonalInfoActivity extends BaseActivity<PersonalInfoActPresenter> implements PersonalInfoActView, AreaActivityInterface {


    private static final int FRAGMENT_SIZE = 5;
    @BindView(R.id.ugvp_personal)
    UnDragableViewPager ugvpPersonal;
    private PagerAdapter mAdapter;
    private ArrayList<Fragment> fragments;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_personal_info;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void init() {
        fragments = new ArrayList<>(5);
        mAdapter = new FragmentAdapter(new OnFragmentNeeded() {
            @NotNull
            @Override
            public Fragment onFragmentNeeded(int position) {
                return getFragmentByPosition(position);
            }
        }, FRAGMENT_SIZE, getSupportFragmentManager());
        ugvpPersonal.setAdapter(mAdapter);
    }

    @NotNull
    private Fragment getFragmentByPosition(int position) {
        Fragment fragment = fragments.size() > position ? fragments.get(position) : null;
        if (fragment == null) {
            Bundle args = new Bundle();
            switch (position) {
                case 0:
                    fragment = new PersonalInfoFragment();
                    fragment.setArguments(args);
                    fragments.add(0, fragment);
                    break;
                case 1:
                    fragment = new AreaFragment(RegionLevel.province);

                    fragment.setArguments(args);
                    fragments.add(1, fragment);
                    break;
                case 2:
                    fragment = new AreaFragment(RegionLevel.city);
                    fragment.setArguments(args);
                    fragments.add(2, fragment);
                    break;
                case 3:
                    fragment = new AreaFragment(RegionLevel.district);
                    fragment.setArguments(args);
                    fragments.add(3, fragment);
                    break;
                case 4:
                    fragment = new AreaFragment(RegionLevel.area);
                    fragment.setArguments(args);
                    fragments.add(4, fragment);
                    break;
                default:
                    throw new IllegalArgumentException("wrong position:" + position);
            }
        }
        if (fragment instanceof AreaFragment) {
            Log.d("AreaFragment", "getFragmentByPosition: " + fragment.toString() + "==" + ((AreaFragment) fragment).getRegionLevel().toString() + ":" + position);
        }
        return fragment;
    }


    @Override
    protected PersonalInfoActPresenter initPresenterImpl() {
        return new PersonalInfoActPreImpl();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

//    @OnClick(R.id.id_imagebutton_back)
//    public void back() {
//        UserEventQueue.add(new ClickEvent(findViewById(R.id.id_imagebutton_back).toString(), ActionType.CLICK, "Back"));
//        finish();
//    }

    public String getStringById(int id) {
        return getResources().getText(id).toString();
    }

    @Override
    public void onLastLocationObtainError() {
        LoggerWrapper.d("lastlocation error");
    }

    @Override
    public void onPermissionDenied() {
        LoggerWrapper.d("no permission");
    }

    private Map<String, RegionBean.RegionsBean> location = new HashMap<>();
    private int level = RegionLevel.province.ordinal();
    @Override
    public void forwardToNextFragment(RegionBean.RegionsBean regionsBean) {
        int item = (ugvpPersonal.getCurrentItem() + 1) % FRAGMENT_SIZE;
        Fragment fragmentByPosition = getFragmentByPosition(item);
        if (regionsBean != null) {
            Bundle arguments = fragmentByPosition.getArguments();
            arguments.putInt(FieldParams.AREA_UPPER_AREA_ID, regionsBean.getId());
            location.put(regionsBean.getLevel(), regionsBean);
            if (RegionLevel.area.toString().equals(regionsBean.getLevel())) {
                level = RegionLevel.area.ordinal();
            } else if (RegionLevel.district.toString().equals(regionsBean.getLevel())) {
                level = RegionLevel.district.ordinal();
            } else if (RegionLevel.city.toString().equals(regionsBean.getLevel())) {
                level = RegionLevel.city.ordinal();
            } else if (RegionLevel.province.toString().equals(regionsBean.getLevel())) {
                level = RegionLevel.province.ordinal();
            } else {
                XLeoToast.showMessage(R.string.show_input_error);
            }
        }
        if (item != 0 && fragmentByPosition instanceof AreaFragment) {
            ((AreaFragment)fragmentByPosition).obtainData();
        }
        if (item == 0) {
            Bundle arguments = fragmentByPosition.getArguments();
            arguments.putBoolean(FieldParams.NEED_UPDATE_REGION, true);
        }
        ugvpPersonal.setCurrentItem(item);
    }

    public int getRegionDatas(Map<String, RegionBean.RegionsBean> regionDatas) {
        if (regionDatas == null) {
            throw new IllegalArgumentException("regionDatas is used to setdatas can't be null");
        }
        regionDatas.clear();
        regionDatas.putAll(location);
        return level;
    }

    public void returnLastFragment() {
        ugvpPersonal.setCurrentItem(ugvpPersonal.getCurrentItem() > 0 ? ugvpPersonal.getCurrentItem() - 1 : 0);
    }
}
