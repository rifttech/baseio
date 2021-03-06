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

import com.firenio.baseio.codec.protobase.ProtobaseCodec;
import com.firenio.baseio.codec.protobase.ProtobaseFrame;
import com.firenio.baseio.component.ChannelAcceptor;
import com.firenio.baseio.component.ChannelAliveIdleEventListener;
import com.firenio.baseio.component.IoEventHandle;
import com.firenio.baseio.component.LoggerChannelOpenListener;
import com.firenio.baseio.component.NioSocketChannel;
import com.firenio.baseio.log.DebugUtil;
import com.firenio.baseio.protocol.Frame;

public class TestServer {

    public static void main(String[] args) throws Exception {

        IoEventHandle eventHandleAdaptor = new IoEventHandle() {

            @Override
            public void accept(NioSocketChannel channel, Frame frame) throws Exception {
                ProtobaseFrame f = (ProtobaseFrame) frame;
                DebugUtil.debug("receive text:" + f.getReadText());
                frame.write("yes server already accept your text message:", channel);
                frame.write(f.getReadText(), channel);
                if (f.getReadBinarySize() > 0) {
                    DebugUtil.debug("receive binary:" + new String(f.getReadBinary()));
                    f.writeBinary("yes server already accept your binary message:".getBytes());
                    f.writeBinary(f.getReadBinary());
                }
                channel.flush(frame);
            }
        };

        ChannelAcceptor context = new ChannelAcceptor(8300);
        context.addChannelEventListener(new LoggerChannelOpenListener());
        context.addChannelIdleEventListener(new ChannelAliveIdleEventListener());
        context.setIoEventHandle(eventHandleAdaptor);
        context.setProtocolCodec(new ProtobaseCodec());
        context.bind();
    }

}
