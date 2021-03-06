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
package com.generallycloud.baseio.container.rtp.server;

import com.generallycloud.baseio.codec.protobase.future.ProtobaseFuture;
import com.generallycloud.baseio.component.NioSocketChannel;

public class RTPLeaveRoomServlet extends RTPServlet {

    public static final String SERVICE_NAME = RTPLeaveRoomServlet.class.getSimpleName();

    @Override
    public void doAccept(NioSocketChannel channel, ProtobaseFuture future,
            RTPChannelAttachment attachment) throws Exception {

        RTPRoom room = attachment.getRtpRoom();

        if (room != null) {
            //			room.leave(channel.getDatagramChannel()); //FIXME udp 
        }

        future.write("1");

        channel.flush(future);
    }
}
