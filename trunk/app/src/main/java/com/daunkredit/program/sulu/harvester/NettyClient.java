package com.daunkredit.program.sulu.harvester;

import com.sulu.harvester.message.IncomeMessageProto;
import com.orhanobut.logger.Logger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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

/**
 * Created by Miaoke on 12/04/2017.
 */

public class NettyClient {

    private Channel channel;
    private EventLoopGroup group;
    private boolean mIsStarted = false;
    private final Bootstrap bootstrap;

    private NettyClient(){
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
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
    }
    public void initStart(String host, int port) throws InterruptedException{
        channel = bootstrap.connect(host,port).sync().channel();
        mIsStarted = true;
        Logger.d("connect to %s : %s",host,port);
    }

    public  boolean isReusable(){
        return mIsStarted && channel.isOpen() && channel.isWritable();
    }

    public void send(IncomeMessageProto.Message msg) throws InterruptedException{
        Logger.d("mobile client send message: %s %s %s %s",msg.getBody(),msg.getImei(),msg.getCTimestamp(),msg.getType());
        channel.writeAndFlush(msg).sync();
        if (channel != null) {
            tearDown();
        }
    }

    public void tearDown(){
        mIsStarted = false;
        channel.close();
        if (channel.parent() != null) {
            channel.parent().close();
        }
    }
    public static class Holder{
        private static NettyClient sNettyClient;
        public static NettyClient getNettyClient(){
            if (sNettyClient == null) {
                synchronized(Holder.class){
                    if (sNettyClient == null) {
                        sNettyClient = new NettyClient();
                    }
                }
            }
            return sNettyClient;
        }
        public static void releaseClient(){
            sNettyClient = null;
        }
    }
}
