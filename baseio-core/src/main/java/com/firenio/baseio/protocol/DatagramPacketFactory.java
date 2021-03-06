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

public class DatagramPacketFactory {

    private Calculagraph calculagraph;

    public DatagramPacketFactory(int markInterval) {
        this.calculagraph = new Calculagraph(markInterval);
    }

    public DatagramPacketFactory(int markInterval, long currentMark) {
        this.calculagraph = new Calculagraph(markInterval, currentMark);
    }

    public DatagramPacket createDatagramPacket(byte[] data) {
        return DatagramPacket.createSendPacket(DatagramPacket.TYPE_DATA,
                calculagraph.getTimestamp(), calculagraph.getSequenceNO(), data);
    }

    public Calculagraph getCalculagraph() {
        return calculagraph;
    }

}
