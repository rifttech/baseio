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
package test.io.fixedlength;

import com.firenio.baseio.codec.fixedlength.FixedLengthCodec;
import com.firenio.baseio.codec.fixedlength.FixedLengthFrame;
import com.firenio.baseio.common.Util;
import com.firenio.baseio.component.ChannelConnector;
import com.firenio.baseio.component.IoEventHandle;
import com.firenio.baseio.component.LoggerChannelOpenListener;
import com.firenio.baseio.component.NioSocketChannel;
import com.firenio.baseio.component.SslContext;
import com.firenio.baseio.component.SslContextBuilder;
import com.firenio.baseio.log.DebugUtil;
import com.firenio.baseio.protocol.Frame;

public class TestFIxedLengthClient {

    public static void main(String[] args) throws Exception {

        IoEventHandle eventHandleAdaptor = new IoEventHandle() {

            @Override
            public void accept(NioSocketChannel channel, Frame frame) throws Exception {
                System.out.println();
                System.out.println("____________________" + frame);
                System.out.println();
            }
        };

        SslContext sslContext = SslContextBuilder.forClient(true).build();
        ChannelConnector context = new ChannelConnector(8300);
        context.setIoEventHandle(eventHandleAdaptor);
        context.addChannelEventListener(new LoggerChannelOpenListener());
        //		context.addChannelEventListener(new ChannelActiveSEListener());
        context.setProtocolCodec(new FixedLengthCodec());
        context.setSslContext(sslContext);
        NioSocketChannel channel = context.connect();
        FixedLengthFrame frame = new FixedLengthFrame();
        frame.write("hello server!", channel);
        channel.flush(frame);
        Util.sleep(100);
        Util.close(context);
        DebugUtil.debug("连接已关闭。。。");
    }

}
