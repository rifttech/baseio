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

import com.firenio.baseio.buffer.ByteBuf;
import com.firenio.baseio.buffer.ByteBufAllocator;
import com.firenio.baseio.buffer.ByteBufUtil;

/**
 * @author wangkai
 *
 */
public class TestDuplicatedByteBuf {

    public static void main(String[] args) throws Exception {
        
        
//        testDirectP();
//        testHeapP();
        testDirect();
        testHeap();
        
        
    }
    
    static void testDirect() throws Exception{
        ByteBuf buf = ByteBufUtil.direct(16);
        
        buf.put("abcdef".getBytes());
        ByteBuf buf2 = buf.duplicate();
        
        System.out.println(new String(buf2.flip().getBytes()));
    }
    
    static void testHeap() throws Exception{
        ByteBuf buf = ByteBufUtil.direct(16);
        
        buf.put("abcdef".getBytes());
        ByteBuf buf2 = buf.duplicate();
        
        System.out.println(new String(buf2.flip().getBytes()));
    }
    
    static void testDirectP() throws Exception{
        ByteBufAllocator a = TestAlloc.direct();
        ByteBuf buf = a.allocate(16);
        
        buf.put("abcdef".getBytes());
        ByteBuf buf2 = buf.duplicate();
        
        System.out.println(new String(buf2.flip().getBytes()));
    }
    
    static void testHeapP() throws Exception{
        ByteBufAllocator a = TestAlloc.heap();
        ByteBuf buf = a.allocate(16);
        
        buf.put("abcdef".getBytes());
        ByteBuf buf2 = buf.duplicate();
        
        System.out.println(new String(buf2.flip().getBytes()));
    }
    
    
}
