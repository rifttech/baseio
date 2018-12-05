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
package com.generallycloud.baseio.balance;

import com.generallycloud.baseio.balance.facade.FacadeSocketChannel;

public interface FacadeInterceptor {

    /**
     * 是否拦截
     * 
     * @param channel
     * @param future
     * @return true拦截，false放行
     * @throws Exception
     */
    public abstract boolean intercept(FacadeSocketChannel channel, BalanceFuture future)
            throws Exception;
}
