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
import com.firenio.baseio.buffer.ByteBufUtil;
import com.firenio.baseio.common.Util;

/**
 * @author wangkai
 *
 */
public class TestUnpooledByteBufAllocator {

    public static void main(String[] args) {
        test();
    }

    static void test() {
        byte[] data = "你好啊，world".getBytes();
        ByteBuf buf = ByteBufUtil.direct(data.length);
        buf.put(data);
        buf.flip();
        Util.release(buf);
        for (;;) {
            ByteBuf buf2 = buf.duplicate();
            byte[] bb = buf2.getBytes();
            String s = new String(bb);
            System.out.println(s);
        }

    }
}
