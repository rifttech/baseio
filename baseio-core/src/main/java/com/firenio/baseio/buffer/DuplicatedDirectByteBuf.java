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
package com.firenio.baseio.buffer;

import java.nio.ByteBuffer;

/**
 * @author wangkai
 *
 */
//FIXME ..make this readonly
final class DuplicatedDirectByteBuf extends AbstractDirectByteBuf {

    private ByteBuf proto;

    DuplicatedDirectByteBuf(ByteBuffer memory, ByteBuf proto) {
        super(null, memory);
        this.proto = proto;
        this.produce(proto);
    }

    @Override
    public ByteBuf duplicate() {
        return proto.duplicate();
    }
    
    private ByteBuf produce(ByteBuf buf) {
        this.offset = buf.offset();
        this.capacity = buf.capacity();
        this.limit(buf.limit());
        this.position(buf.position());
        this.referenceCount = 1;
        return this;
    }

    @Override
    public void release() {
        proto.release();
    }

}
