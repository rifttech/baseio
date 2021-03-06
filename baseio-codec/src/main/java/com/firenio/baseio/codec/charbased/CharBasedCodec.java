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
package com.firenio.baseio.codec.charbased;

import java.io.IOException;

import com.firenio.baseio.buffer.ByteBuf;
import com.firenio.baseio.common.Util;
import com.firenio.baseio.component.NioSocketChannel;
import com.firenio.baseio.protocol.Frame;
import com.firenio.baseio.protocol.ProtocolCodec;

/**
 * @author wangkai
 *
 */
public class CharBasedCodec extends ProtocolCodec {

    private int  limit;

    private byte splitor;

    public CharBasedCodec() {
        this(1024 * 8, (byte) '\n');
    }

    public CharBasedCodec(byte splitor) {
        this(1024 * 8, splitor);
    }

    public CharBasedCodec(int limit, byte splitor) {
        this.limit = limit;
        this.splitor = splitor;
    }

    @Override
    public Frame decode(NioSocketChannel ch, ByteBuf src) throws IOException {
        int p = src.indexOf(splitor);
        if (p == -1) {
            if (src.remaining() > limit) {
                throw new IOException("max length " + limit);
            }
            return null;
        }
        src.markL();
        src.limit(p);
        String readText = Util.decode(ch.getCharset(), src.nioBuffer());
        src.reverse();
        src.resetL();
        src.skip(1);
        return new CharBasedFrame(readText);
    }

    @Override
    public ByteBuf encode(NioSocketChannel ch, Frame frame) throws IOException {
        CharBasedFrame f = (CharBasedFrame) frame;
        int writeSize = f.getWriteSize();
        if (writeSize == 0) {
            throw new IOException("null write buffer");
        }
        ByteBuf buf = ch.alloc().allocate(writeSize + 1);
        buf.put(f.getWriteBuffer(), 0, writeSize);
        buf.putByte(splitor);
        return buf.flip();
    }

    @Override
    public String getProtocolId() {
        return "LineBased";
    }

}
