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

public final class EmptyByteBuf extends UnpooledHeapByteBuf {

    private static final EmptyByteBuf INSTANCE = new EmptyByteBuf();

    private EmptyByteBuf() {
        super(ByteBufUtil.heap(), new byte[] {});
    }

    @Override
    public ByteBuf duplicate() {
        return this;
    }

    @Override
    public boolean isReleased() {
        return true;
    }

    public static final ByteBuf get() {
        return INSTANCE;
    }
}
