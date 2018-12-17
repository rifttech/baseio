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
package test.io.buffer;

import com.firenio.baseio.buffer.ByteBufAllocator;
import com.firenio.baseio.buffer.PooledByteBufAllocator;

/**
 * @author wangkai
 *
 */
public class TestAlloc {

    public static ByteBufAllocator direct() throws Exception {
        return direct(1024);
    }

    public static ByteBufAllocator direct(int cap) throws Exception {
        ByteBufAllocator a = new PooledByteBufAllocator(1024, 1, true, 1);
        a.start();
        return a;
    }

    public static ByteBufAllocator heap() throws Exception {
        return heap(1024);
    }

    public static ByteBufAllocator heap(int cap) throws Exception {
        ByteBufAllocator a = new PooledByteBufAllocator(64, 1, false, 1);
        a.start();
        return a;
    }

}