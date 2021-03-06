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
package com.firenio.baseio.component;

import com.firenio.baseio.log.Logger;
import com.firenio.baseio.log.LoggerFactory;
import com.firenio.baseio.protocol.Frame;

/**
 * @author wangkai
 *
 */
public class LoggerExceptionCaughtHandle implements ExceptionCaughtHandle {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void exceptionCaught(NioSocketChannel ch, Frame frame, Exception ex) {
        logger.error(ex.getMessage(), ex);
    }

}
