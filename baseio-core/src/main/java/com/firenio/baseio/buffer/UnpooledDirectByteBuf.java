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
final class UnpooledDirectByteBuf extends AbstractDirectByteBuf {

    UnpooledDirectByteBuf(ByteBufAllocator allocator, ByteBuffer memory) {
        super(allocator, memory);
        this.produce(memory.capacity());
    }

    @Override
    public ByteBuf clear() {
        memory.clear();
        return this;
    }

    @Override
    public ByteBuf duplicate() {
        if (isReleased()) {
            throw new ReleasedException("released");
        }
        //请勿移除此行，DirectByteBuffer需要手动回收，release要确保被执行
        addReferenceCount();
        return new DuplicatedDirectByteBuf(memory.duplicate(), this);
    }

    @Override
    public ByteBuf flip() {
        memory.flip();
        return this;
    }

    protected void produce(int capacity) {
        this.capacity = capacity;
        this.limit(capacity);
        this.referenceCount = 1;
    }

    @Override
    public void release() {
        int referenceCount = this.referenceCount;
        if (referenceCount < 1) {
            return;
        }
        if (refCntUpdater.compareAndSet(this, referenceCount, referenceCount - 1)) {
            if (referenceCount == 1) {
                ByteBufUtil.release(memory);
                return;
            }
        }
        for (;;) {
            referenceCount = this.referenceCount;
            if (referenceCount < 1) {
                return;
            }
            if (refCntUpdater.compareAndSet(this, referenceCount, referenceCount - 1)) {
                if (referenceCount == 1) {
                    ByteBufUtil.release(memory);
                    return;
                }
            }
        }
    }

}
