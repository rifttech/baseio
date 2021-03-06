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
package com.firenio.baseio.codec.redis;

import com.firenio.baseio.component.IoEventHandle;
import com.firenio.baseio.component.NioSocketChannel;
import com.firenio.baseio.concurrent.Waiter;
import com.firenio.baseio.protocol.Frame;

public class RedisIOEventHandle extends IoEventHandle {

    private Waiter<RedisCmdFrame> waiter;

    @Override
    public void accept(NioSocketChannel ch, Frame frame) throws Exception {
        RedisCmdFrame f = (RedisCmdFrame) frame;
        Waiter<RedisCmdFrame> waiter = this.waiter;
        if (waiter != null) {
            this.waiter = null;
            waiter.call(f, null);
        }
    }

    public Waiter<RedisCmdFrame> newWaiter() {
        this.waiter = new Waiter<RedisCmdFrame>();
        return this.waiter;
    }

}
