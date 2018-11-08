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
package com.generallycloud.test.io.charbased;

import com.generallycloud.baseio.codec.charbased.CharBasedCodec;
import com.generallycloud.baseio.component.ChannelAcceptor;
import com.generallycloud.baseio.component.IoEventHandle;
import com.generallycloud.baseio.component.LoggerChannelOpenListener;
import com.generallycloud.baseio.component.NioSocketChannel;
import com.generallycloud.baseio.protocol.Frame;

public class TestLineBasedBroadcastServer {

    public static void main(String[] args) throws Exception {

        IoEventHandle eventHandleAdaptor = new IoEventHandle() {

            @Override
            public void accept(NioSocketChannel channel, Frame frame) throws Exception {
                long old = System.currentTimeMillis();
                String res = "hello world!";
                frame.write(res, channel);
                channel.getContext().getChannelManager().broadcast(frame);
                long now = System.currentTimeMillis();
                System.out.println("广播花费时间：" + (now - old) + ",连接数："
                        + channel.getContext().getChannelManager().getManagedChannelSize());
            }
        };

        ChannelAcceptor context = new ChannelAcceptor(8300);
        context.addChannelEventListener(new LoggerChannelOpenListener());
        context.setIoEventHandle(eventHandleAdaptor);
        context.setProtocolCodec(new CharBasedCodec());
        context.bind();
    }

}
