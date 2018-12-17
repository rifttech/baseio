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
package test.others;

import java.io.IOException;
import java.util.HashSet;

import com.firenio.baseio.common.ByteUtil;

public class Test {

    public static final byte PROTOCOL_RESPONSE = 1;
    public static final byte PROTOCOL_PUSH     = 2;
    public static final byte PROTOCOL_BRODCAST = 3;

    public static void main(String[] args) throws IOException {

        byte b = 127;

        System.out.println(ByteUtil.byte2BinaryString(b));
        System.out.println(ByteUtil.byte2BinaryString((byte) (b & 0x3f)));

        System.out.println(ByteUtil.byte2BinaryString((byte) -1));
        System.out.println(ByteUtil.byte2BinaryString((byte) -2));

        System.out.println(Integer.MAX_VALUE >> 3);

        System.out.println(ByteUtil.binaryString2HexString("00100000"));

        //test branch   tes22222

        HashSet<String> set = new HashSet<>();

        set.add("11");
        set.add("22");

        for (String s : set) {

            System.out.println(s);

        }

        for (String s : set) {

            System.out.println(s);

        }
    }
}