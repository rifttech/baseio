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
package test.io.protobase;

import com.firenio.baseio.codec.protobase.ParamedProtobaseFrame;
import com.firenio.baseio.codec.protobase.ProtobaseCodec;
import com.firenio.baseio.common.Util;
import com.firenio.baseio.component.ChannelConnector;
import com.firenio.baseio.component.IoEventHandle;
import com.firenio.baseio.component.LoggerChannelOpenListener;
import com.firenio.baseio.component.NioSocketChannel;
import com.firenio.baseio.protocol.Frame;

public class TestSimple {

    public static void main(String[] args) throws Exception {
        String serviceKey = "/test-simple";
        String param = "ttt";
        IoEventHandle eventHandle = new IoEventHandle() {

            @Override
            public void accept(NioSocketChannel channel, Frame frame) throws Exception {
                System.out.println("________________________" + frame);
            }
        };
        ChannelConnector context = new ChannelConnector(8300);
        context.setProtocolCodec(new ProtobaseCodec());
        context.addChannelEventListener(new LoggerChannelOpenListener());
        context.setIoEventHandle(eventHandle);
        NioSocketChannel channel = context.connect();
        ParamedProtobaseFrame f = new ParamedProtobaseFrame(serviceKey);
        f.write(param, channel);
        channel.flush(f);
        Util.sleep(500);
        Util.close(context);
    }

}
