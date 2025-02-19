package net.jlxxw.wechat.component.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import net.jlxxw.wechat.component.EventBus;
import net.jlxxw.wechat.properties.WeChatProperties;
import net.jlxxw.wechat.security.WeChatServerSecurityCheck;
import net.jlxxw.wechat.util.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

import static io.netty.buffer.Unpooled.copiedBuffer;

/**
 * netty微信回调处理接口
 *
 * @author chunyang.leng
 * @date 2021/1/25 9:46 上午
 */
@Component
@ChannelHandler.Sharable
public class WeChatChannel extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(WeChatChannel.class);
    @Autowired
    private EventBus eventBus;
    @Autowired(required = false)
    private WeChatServerSecurityCheck weChatServerSecurityCheck;
    @Autowired
    private WeChatProperties weChatProperties;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        LoggerUtils.debug(logger, "netty 开始处理");
        if (weChatProperties.isEnableWeChatCallBackServerSecurityCheck() && weChatServerSecurityCheck != null) {
            // 开启微信回调ip安全检查时执行

            // 获取远程socket信息
            InetSocketAddress socketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
            // 获取远程ip地址信息
            String ipAddress = socketAddress.getAddress().getHostAddress();
            LoggerUtils.debug(logger, "微信回调ip安全检查执行,远程ip:{}", ipAddress);
            if (!weChatServerSecurityCheck.isSecurity(ipAddress)) {
                LoggerUtils.warn(logger, "非法ip，不予处理:{}", ipAddress);
                // 非法ip，不予处理
                channelHandlerContext.writeAndFlush(responseOK(HttpResponseStatus.FORBIDDEN, copiedBuffer("IP FORBIDDEN", CharsetUtil.UTF_8))).addListener(ChannelFutureListener.CLOSE);
                return;
            }
        }
        // 获取请求体数据缓存
        ByteBuf content = fullHttpRequest.content();
        // 请求体数据转byte数组
        byte[] reqContent = new byte[content.readableBytes()];
        // 缓存数据加载至byte数组中
        content.readBytes(reqContent);
        // 获取请求的uri
        String uri = fullHttpRequest.uri();
        // 事件总线开始执行处理逻辑
        final String resultData = eventBus.dispatcher(reqContent, uri);
        // 响应数据刷新到缓冲区
        ByteBuf responseData = copiedBuffer(resultData, CharsetUtil.UTF_8);
        // 包装响应结果
        FullHttpResponse response = responseOK(HttpResponseStatus.OK, responseData);
        // 发送响应
        channelHandlerContext
                .writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 包装响应结果，使用http1.1协议格式
     *
     * @param status  响应状态码
     * @param content 响应内容
     * @return 包装后到对象
     */
    private FullHttpResponse responseOK(HttpResponseStatus status, ByteBuf content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        response.headers().set("Content-Type", "application/xml;charset=UTF-8");
        response.headers().set("Content_Length", response.content().readableBytes());
        return response;
    }


}
