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

import java.net.StandardSocketOptions;

import com.firenio.baseio.component.ChannelEventListener;
import com.firenio.baseio.component.NioSocketChannel;

/**
 * @author wangkai
 *
 */
public class SetOptionListener implements ChannelEventListener {

    @Override
    public void channelOpened(NioSocketChannel channel) throws Exception {
        channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
    }

    @Override
    public void channelClosed(NioSocketChannel channel) {

    }

}
