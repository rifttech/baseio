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
package com.firenio.baseio.codec.http11;

import com.firenio.baseio.common.BASE64Util;
import com.firenio.baseio.common.Util;

public class WsUpgradeRequestFrame extends ClientHttpFrame {

    public WsUpgradeRequestFrame(String url) {
        super(url);
        this.setRequestHeaders();
    }

    private void setRequestHeaders() {
        setRequestHeader(HttpHeader.Connection, "Upgrade");
        setRequestHeader(HttpHeader.Upgrade, "websocket");
        setRequestHeader(HttpHeader.Sec_WebSocket_Version, "13");
        setRequestHeader(HttpHeader.Sec_WebSocket_Key,
                BASE64Util.byteArrayToBase64(Util.randomMostSignificantBits().getBytes()));
        setRequestHeader(HttpHeader.Sec_WebSocket_Extensions,
                "permessage-deflate; client_max_window_bits");
    }
}
