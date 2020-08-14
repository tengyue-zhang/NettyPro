package com.zty.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 群聊服务器
 *
 * @author tengyue.zhang
 * @date 2020-08-14
 */
public class GroupChatServer {

    private static Logger LOGGER = LoggerFactory.getLogger(GroupChatServer.class);

    /**
     * 监听端口
     */
    private int port;

    public GroupChatServer(int port) {
        this.port = port;
    }

    /**
     * 处理客户端的请求
     */
    public void run() throws Exception {

        // 创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 8个NioEventLoop
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 获取pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            // 向pipeline加入编码器
                            pipeline.addLast("decoder", new StringDecoder());
                            // 向pipeline加入解码器
                            pipeline.addLast("encoder", new StringEncoder());
                            pipeline.addLast(null);
                        }
                    });
            LOGGER.info("netty 服务器启动");
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            // 监听关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        // 启动
        try {
            new GroupChatServer(7000).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
