/*
 * Copyright 2015-2017 GenerallyCloud.com
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.generallycloud.sample.baseio.http11.proxy4cloud;

import java.io.IOException;
import java.util.Map.Entry;

import com.generallycloud.baseio.buffer.ByteBuf;
import com.generallycloud.baseio.buffer.ByteBufUtil;
import com.generallycloud.baseio.codec.http11.ClientHttpCodec;
import com.generallycloud.baseio.codec.http11.ClientHttpFrame;
import com.generallycloud.baseio.codec.http11.HttpCodec;
import com.generallycloud.baseio.codec.http11.HttpFrame;
import com.generallycloud.baseio.codec.http11.HttpHeader;
import com.generallycloud.baseio.codec.http11.HttpMethod;
import com.generallycloud.baseio.codec.http11.HttpStatus;
import com.generallycloud.baseio.common.CloseUtil;
import com.generallycloud.baseio.common.ReleaseUtil;
import com.generallycloud.baseio.component.ChannelAcceptor;
import com.generallycloud.baseio.component.ChannelConnector;
import com.generallycloud.baseio.component.IoEventHandle;
import com.generallycloud.baseio.component.LoggerChannelOpenListener;
import com.generallycloud.baseio.component.NioEventLoop;
import com.generallycloud.baseio.component.NioEventLoopGroup;
import com.generallycloud.baseio.component.NioSocketChannel;
import com.generallycloud.baseio.protocol.Frame;
import com.generallycloud.baseio.protocol.ProtocolCodec;

public class HttpProxy4CloudServer {

//    static final String                netHost         = "47.52.62.51";
//    static final int                   netPort         = 18088;
    static final String                netHost         = "127.0.0.1";
    static final int                   netPort         = 18088;
    static final String                CONNECT_RES     = "HTTP/1.1 200 Connection Established\r\n\r\n";
    static final ByteBuf               CONNECT_RES_BUF = ByteBufUtil.wrap(CONNECT_RES.getBytes());
    static final HttpProxy4CloudServer server          = new HttpProxy4CloudServer();
    private ChannelAcceptor            context;
    private volatile boolean           enable          = true;

    public synchronized void stop() {
        CloseUtil.unbind(context);
    }

    public void enable() {
        enable = true;
    }

    public void disable() {
        enable = false;
    }

    public synchronized void strtup(NioEventLoopGroup serverG, int port) throws Exception {
        if (context != null && context.isActive()) {
            return;
        }

        IoEventHandle eventHandle = new IoEventHandle() {

            @Override
            public void accept(NioSocketChannel ch_src, Frame frame) throws Exception {
                final HttpFrame f = (HttpFrame) frame;
                if (!enable) {
                    f.setStatus(HttpStatus.C503);
                    f.write("503 service unavailable".getBytes());
                    ch_src.flush(f);
                    return;
                }
                if (f.getMethod() == HttpMethod.CONNECT) {
                    ch_src.flush(CONNECT_RES_BUF.duplicate());
                    ProxySession4Cloud s = ProxySession4Cloud.get(ch_src);
                    String[] arr = f.getHost().split(":");
                    s.mask = (byte) arr[0].getBytes().length;
                    s.host = arr[0];
                    s.port = Integer.parseInt(arr[1]);
                    s.handshakeFinished = true;
                } else {
                    String host = f.getHost();
                    String[] arr = host.split(":");
                    final int port;
                    if (arr.length == 2) {
                        port = Integer.parseInt(arr[1]);
                    } else {
                        port = 80;
                    }
                    if (f.getRequestHeaders().remove(HttpHeader.Proxy_Connection) == null) {
                        return;
                    }
                    NioEventLoop el = ch_src.getEventLoop();
                    ChannelConnector context = new ChannelConnector(el, netHost, netPort);
                    context.setProtocolCodec(new ClientHttpCodec() {

                        public Frame decode(NioSocketChannel ch, ByteBuf src) throws IOException {
                            ProxySession4Cloud s = ProxySession4Cloud.get(ch);
                            NetDataTransferServer.mask(src, s.mask);
                            return super.decode(ch, src);
                        };

                    });

                    context.setIoEventHandle(new IoEventHandle() {

                        @Override
                        public void accept(NioSocketChannel ch, Frame frame) throws Exception {
                            ClientHttpFrame res = (ClientHttpFrame) frame;
                            for (Entry<HttpHeader, String> header : res.getResponse_headers()
                                    .entrySet()) {
                                if (header.getValue() == null) {
                                    continue;
                                }
                                f.setResponseHeader(header.getKey(), header.getValue().getBytes());
                            }
                            if (res.getBodyContent() != null) {
                                f.write(res.getBodyContent());
                            } else if ("chunked".equalsIgnoreCase(
                                    res.getResponse_headers().get(HttpHeader.Transfer_Encoding))) {
                                f.getResponseHeaders().remove(HttpHeader.Transfer_Encoding);
                                f.getResponseHeaders().remove(HttpHeader.Content_Encoding);
                                f.write("server response is chunked, not supported now."
                                        .getBytes());

                            }
                            ch_src.flush(f);
                            ch.close();
                        }
                    });
                    context.addChannelEventListener(new LoggerChannelOpenListener());
                    context.connect((ch, ex) -> {
                        if (ex == null) {
                            ProxySession4Cloud s = ProxySession4Cloud.get(ch);
                            HttpFrame req = new ClientHttpFrame(f.getRequestURI(), f.getMethod());
                            req.setRequestParams(f.getRequestParams());
                            req.setRequestHeaders(f.getRequestHeaders());
                            ByteBuf body = null;
                            ByteBuf head = null;
                            try {
                                byte[] realHost = arr[0].getBytes();
                                body = ch.encode(req);
                                int len = 5 + realHost.length;
                                s.mask = (byte) realHost.length;
                                head = ch.alloc().allocate(len);
                                head.putByte((byte) realHost.length);
                                head.putByte((byte) 83);
                                head.putByte((byte) 38);
                                head.putUnsignedShort(port);
                                head.put(realHost);
                                NetDataTransferServer.mask(body, s.mask);
                                ch.flush(head.flip());
                                ch.flush(body);
                            } catch (Exception e) {
                                ReleaseUtil.release(head);
                                ReleaseUtil.release(body);
                            }
                        }
                    });
                }
            }
        };

        context = new ChannelAcceptor(serverG, 8088);
        context.setProtocolCodec(new HttpProxy4CloudCodec());
        context.setIoEventHandle(eventHandle);
        context.addChannelEventListener(new LoggerChannelOpenListener());
        context.bind();
    }

    public static HttpProxy4CloudServer get() {
        return server;
    }

    class HttpProxy4CloudCodec extends HttpCodec {

        @Override
        public Frame decode(final NioSocketChannel ch_src, ByteBuf src) throws IOException {
            ProxySession4Cloud s = ProxySession4Cloud.get(ch_src);
            if (s.handshakeFinished) {
                if (s.connector == null || !s.connector.isConnected()) {
                    NioEventLoop el = ch_src.getEventLoop();
                    ChannelConnector context = new ChannelConnector(el, netHost, netPort);
                    context.setProtocolCodec(new ProtocolCodec() {

                        @Override
                        public String getProtocolId() {
                            return "http-proxy-connect";
                        }

                        @Override
                        public ByteBuf encode(NioSocketChannel ch, Frame frame) throws IOException {
                            return null;
                        }

                        @Override
                        public Frame decode(NioSocketChannel ch, ByteBuf src) throws IOException {
                            ByteBuf buf = ch_src.alloc().allocate(src.remaining());
                            buf.read(src);
                            buf.flip();
                            NetDataTransferServer.mask(buf, s.mask);
                            ch_src.flush(buf);
                            return null;
                        }
                    });
                    context.addChannelEventListener(new LoggerChannelOpenListener());
                    ByteBuf buf = ch_src.alloc().allocate(src.remaining());
                    buf.read(src);
                    s.connector = context;
                    s.connector.connect((ch_target, ex) -> {
                        if (ex == null) {
                            byte[] realHost = s.host.getBytes();
                            int len = 5 + realHost.length;
                            ByteBuf head = ch_target.alloc().allocate(len);
                            head.putByte((byte) realHost.length);
                            head.putByte((byte) 83);
                            head.putByte((byte) 38);
                            head.putUnsignedShort(s.port);
                            head.put(realHost);
                            ch_target.flush(head.flip());
                            buf.flip();
                            NetDataTransferServer.mask(buf, (byte) realHost.length);
                            ch_target.flush(buf);
                        } else {
                            buf.release();
                            ProxySession4Cloud.remove(ch_src);
                            CloseUtil.close(ch_src);
                        }
                    });
                } else {
                    ByteBuf buf = ch_src.alloc().allocate(src.remaining());
                    buf.read(src);
                    buf.flip();
                    NetDataTransferServer.mask(buf, s.mask);
                    s.connector.getChannel().flush(buf);
                }
                return null;
            }
            return super.decode(ch_src, src);
        }

        @Override
        protected void parseFirstLine(HttpFrame f, StringBuilder line) {
            if (line.charAt(0) == 'C' && line.charAt(1) == 'O' && line.charAt(2) == 'N'
                    && line.charAt(3) == 'N' && line.charAt(4) == 'E' && line.charAt(5) == 'C'
                    && line.charAt(6) == 'T' && line.charAt(7) == ' ') {
                f.setMethod(HttpMethod.CONNECT);
                parseRequestURL(f, 8, line);
            } else {
                super.parseFirstLine(f, line);
            }
        }

    }

    public static class ProxySession4Cloud {

        static final String     ProxySessionChAttr = "_ProxySessionChAttr";
        public boolean          handshakeFinished;
        public String           host;
        public int              port;
        public byte             mask;
        public ChannelConnector connector;

        @Override
        public String toString() {
            return host + ":" + port;
        }

        public static void remove(NioSocketChannel ch) {
            ProxySession4Cloud s = (ProxySession4Cloud) ch.removeAttribute(ProxySessionChAttr);
            if (s != null) {
                s.connector = null;
            }
        }

        public static ProxySession4Cloud get(NioSocketChannel ch) {
            ProxySession4Cloud s = (ProxySession4Cloud) ch.getAttribute(ProxySessionChAttr);
            if (s == null) {
                s = new ProxySession4Cloud();
                ch.setAttribute(ProxySessionChAttr, s);
            }
            return s;
        }
    }

    public static void main(String[] args) throws Exception {

        get().strtup(new NioEventLoopGroup(), 8088);

    }

}
