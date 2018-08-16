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
package com.generallycloud.baseio.component;

import com.generallycloud.baseio.common.CloseUtil;
import com.generallycloud.baseio.log.Logger;
import com.generallycloud.baseio.log.LoggerFactory;
import com.generallycloud.baseio.protocol.Frame;
import com.generallycloud.baseio.protocol.ProtocolCodec;

public class ChannelActiveIdleEventListener implements ChannelIdleEventListener {

    private Logger logger = LoggerFactory.getLogger(ChannelActiveIdleEventListener.class);

    @Override
    public void channelIdled(NioSocketChannel ch, long lastIdleTime, long currentTime) {
        if (ch.isClosed()) {
            logger.info("closed ch");
            return;
        }
        if (ch.getLastAccessTime() < lastIdleTime) {
            logger.info(
                    "Did not detect heartbeat messages in heartbeat cycle, prepare to disconnect {}",
                    ch);
            CloseUtil.close(ch);
        } else {
            ProtocolCodec codec = ch.getCodec();
            Frame frame = codec.ping(ch);
            if (frame == null) {
                // 该channel无需心跳,比如HTTP协议
                return;
            }
            ch.flush(frame);
        }
    }
}
