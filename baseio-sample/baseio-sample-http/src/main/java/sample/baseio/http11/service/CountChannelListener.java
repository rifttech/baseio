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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.firenio.baseio.component.ChannelEventListener;
import com.firenio.baseio.component.NioSocketChannel;

/**
 * @author wangkai
 *
 */
public class CountChannelListener implements ChannelEventListener {

    public static final Map<Integer, NioSocketChannel> chs = new ConcurrentHashMap<>();

    @Override
    public void channelOpened(NioSocketChannel ch) throws Exception {
        chs.put(ch.getChannelId(), ch);
    }

    @Override
    public void channelClosed(NioSocketChannel ch) {
        chs.remove(ch.getChannelId());
    }

}
