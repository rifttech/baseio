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
package com.firenio.baseio.protocol;

import java.nio.charset.Charset;

import com.firenio.baseio.component.ChannelContext;
import com.firenio.baseio.component.NioSocketChannel;

public interface Frame {

    byte[] getWriteBuffer();

    int getWriteSize();

    boolean isPing();

    boolean isPong();

    boolean isSilent();

    boolean isType(byte type);

    boolean isTyped();

    Frame reset();

    Frame setPing();

    Frame setPong();

    void setSilent();

    void setType(byte type);

    void write(byte b);

    void write(byte b[]);

    void write(byte b[], int off, int len);

    void write(String text, ChannelContext context);

    void write(String text, Charset charset);

    void write(String text, NioSocketChannel ch);

}
