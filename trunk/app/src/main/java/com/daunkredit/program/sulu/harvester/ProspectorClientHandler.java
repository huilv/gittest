package com.daunkredit.program.sulu.harvester;

import com.daunkredit.program.sulu.harvester.def.ProtocolName;
import com.orhanobut.logger.Logger;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by Miaoke on 12/04/2017.
 */


@ChannelHandler.Sharable
public class ProspectorClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Logger.d("active: ...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        Logger.d("Receive data from server side: "+msg.toString());

        if(msg.toString().contains(ProtocolName.CONTACT_ACK.toString())){
            HarvestInfoManager.getInstance().setContactStatus("1");
            Logger.d("Contact info send succeed.");
        }else if(msg.toString().contains(ProtocolName.CALL_LOG_ACK.toString())){
            HarvestInfoManager.getInstance().setCallLogsStatus("1");
            Logger.d("Call logs info send succeed.");
        }else if(msg.toString().contains(ProtocolName.SMS_LOG_ACK.toString())){
            HarvestInfoManager.getInstance().setSmsStatus("1");
            Logger.d("SMS info send succeed.");
        }else if(msg.toString().contains(ProtocolName.LOCATION_ACK.toString())){
            HarvestInfoManager.getInstance().setLocationStatus("1");
            HarvestInfoManager.getInstance().locationsClear();
            Logger.d("Location info send succeed.");
        }else if(msg.toString().contains(ProtocolName.PERMISSION_ACK.toString())){
            HarvestInfoManager.getInstance().setPermissionStatus("1");
            HarvestInfoManager.getInstance().permissionClear();
            Logger.d("Permission info send succeed.");
        }else if(msg.toString().contains(ProtocolName.INSTALLED_APP_ACK.toString())){
            HarvestInfoManager.getInstance().setInstallAppStatus("1");
            Logger.d("Installed app list info send succeed.");
        }else if(msg.toString().contains(ProtocolName.MACHINE_TYPE_ACK.toString())){
            HarvestInfoManager.getInstance().setMachineTypeStatus("1");
            Logger.d("Machine type info send succeed.");
        }else if(msg.toString().contains(ProtocolName.CRASH_MSG_ACK.toString())){
            Logger.d("Crash Msg send succeed.");
        }else if(msg.toString().contains(ProtocolName.BEHAVIOR_MSG_ACK.toString())){
            Logger.d("Behavior Msg send succeed.");
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Logger.d("disconnect...");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Logger.d("Error: "+ cause.toString());
        ctx.close();
    }
}
