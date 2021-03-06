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
package com.generallycloud.baseio.container.http11;

import java.util.Map;

import com.generallycloud.baseio.codec.http11.HttpFrame;
import com.generallycloud.baseio.component.NioSocketChannel;
import com.generallycloud.baseio.concurrent.EventLoop;

/**
 * @author wangkai
 *
 */
public interface HttpSessionManager extends EventLoop {

    void putSession(String sessionId, HttpSession ch);

    void removeSession(String sessionId);

    HttpSession getHttpSession(HttpFrameAcceptor context, NioSocketChannel ioSession,
            HttpFrame frame);

    Map<String, HttpSession> getManagedSessions();

    int getManagedSessionSize();

}
