/*
 * Copyright 2015 The Baseio Project
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
package sample.baseio.http11.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.firenio.baseio.buffer.ByteBufAllocatorGroup;
import com.firenio.baseio.buffer.PooledByteBufAllocatorGroup;
import com.firenio.baseio.codec.http11.HttpFrame;
import com.firenio.baseio.codec.http11.HttpHeader;
import com.firenio.baseio.codec.http11.HttpStatic;
import com.firenio.baseio.common.DateUtil;
import com.firenio.baseio.common.Util;
import com.firenio.baseio.component.ChannelContext;
import com.firenio.baseio.component.NioEventLoopGroup;
import com.firenio.baseio.component.NioSocketChannel;

import sample.baseio.http11.HtmlUtil;
import sample.baseio.http11.HttpFrameAcceptor;

@Service("/test-show-memory")
public class TestShowMemoryServlet extends HttpFrameAcceptor {

    @Override
    protected void doAccept(NioSocketChannel ch, HttpFrame frame) throws Exception {
        TestWebSocketChatServlet chatServlet = ContextUtil.getBean(TestWebSocketChatServlet.class);
        TestWebSocketRumpetrollServlet rumpetrollServlet = ContextUtil
                .getBean(TestWebSocketRumpetrollServlet.class);

        WebSocketMsgAdapter chatMsgAdapter = chatServlet.getMsgAdapter();
        WebSocketMsgAdapter rumpetrollMsgAdapter = rumpetrollServlet.getMsgAdapter();

        ChannelContext context = ch.getContext();
        
        String kill = frame.getRequestParam("kill");
        if (!Util.isNullOrBlank(kill)) {
            Integer id = Integer.valueOf(kill,16);
            Util.close(context.getChannelManager().getChannel(id));
        }

        BigDecimal time = new BigDecimal(System.currentTimeMillis() - context.getStartupTime());
        BigDecimal anHour = new BigDecimal(60 * 60 * 1000);
        BigDecimal hour = time.divide(anHour, 3, RoundingMode.HALF_UP);

        NioEventLoopGroup group = ch.getEventLoop().getGroup();

        ByteBufAllocatorGroup allocator = group.getAllocatorGroup();

        String allocatorDes = "unpooled";

        if (allocator instanceof PooledByteBufAllocatorGroup) {
            allocatorDes = ((PooledByteBufAllocatorGroup) allocator).toDebugString();
        }

        int eventLoopSize = group.getEventLoopSize();
        int SERVER_MEMORY_POOL_CAPACITY = group.getMemoryPoolCapacity() * eventLoopSize;
        int SERVER_MEMORY_POOL_UNIT = group.getMemoryPoolUnit();

        double MEMORY_POOL_SIZE = new BigDecimal(
                SERVER_MEMORY_POOL_CAPACITY * SERVER_MEMORY_POOL_UNIT)
                        .divide(new BigDecimal(1024 * 1024), 2, BigDecimal.ROUND_HALF_UP)
                        .doubleValue();

        int M = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        StringBuilder builder = new StringBuilder(HtmlUtil.HTML_HEADER);

        builder.append("		<div style=\"margin-left:20px;\">\n");
        builder.append("服务器内存使用情况：</BR>\n");
        builder.append("虚拟机占用内存：");
        builder.append(runtime.totalMemory() / M);
        builder.append("M;\n</BR>已占用内存：");
        builder.append((runtime.totalMemory() - runtime.freeMemory()) / M);
        builder.append("M;\n</BR>空闲内存：");
        builder.append(runtime.freeMemory() / M);
        builder.append("M;\n</BR>内存池大小：");
        builder.append(MEMORY_POOL_SIZE);
        builder.append("M;\n</BR>内存池状态（Heap）：");
        builder.append(allocatorDes);
        builder.append("\n</BR>聊天室（WebSocket）客户端数量：");
        builder.append(chatMsgAdapter.getClientSize());
        builder.append("\n</BR>小蝌蚪（WebSocket）客户端数量：");
        builder.append(rumpetrollMsgAdapter.getClientSize());
        builder.append("\n</BR>服务器当前连接数（io-session）：");
        builder.append(CountChannelListener.chs.size());
        for (NioSocketChannel s : CountChannelListener.chs.values()) {
            builder.append("\n</BR>");
            builder.append(s);
            builder.append(",opened:");
            builder.append(DateUtil.get().formatYyyy_MM_dd_HH_mm_ss(new Date(s.getCreationTime())));
        }
        builder.append(";\n</BR>服务运行时间：");
        builder.append(hour + "H;");
        builder.append("		</div>\n");

        builder.append(HtmlUtil.HTML_POWER_BY);
        builder.append(HtmlUtil.HTML_BOTTOM);

        frame.write(builder.toString(), ch);

        frame.setResponseHeader(HttpHeader.Content_Type, HttpStatic.text_html_utf8_bytes);

        ch.flush(frame);
    }

}
