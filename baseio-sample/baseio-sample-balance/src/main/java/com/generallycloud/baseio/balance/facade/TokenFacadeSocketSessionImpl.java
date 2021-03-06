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
package com.generallycloud.baseio.balance.facade;

import java.util.Random;

import com.generallycloud.baseio.component.NioSocketChannel;

/**
 * @author wangkai
 *
 */
public class TokenFacadeSocketChannelImpl extends FacadeSocketChannelImpl
        implements TokenFacadeSocketChannel {

    public TokenFacadeSocketChannelImpl(NioSocketChannel channel) {
        super(channel);
        this.token = generateToken();
    }

    private Long token;

    private Long generateToken() {
        long r = new Random().nextInt();
        if (r < 0) {
            r *= -1;
        }
        return (r << 32) | getChannelId();
    }

    @Override
    public Object getChannelKey() {
        return getToken();
    }

    @Override
    public Long getToken() {
        return token;
    }
}
