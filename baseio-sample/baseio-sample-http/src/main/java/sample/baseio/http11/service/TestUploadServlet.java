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
package sample.baseio.http11.service;

import org.springframework.stereotype.Service;

import com.firenio.baseio.codec.http11.HttpFrame;
import com.firenio.baseio.codec.http11.HttpHeader;
import com.firenio.baseio.codec.http11.HttpStatic;
import com.firenio.baseio.component.NioSocketChannel;

import sample.baseio.http11.HttpFrameAcceptor;

@Service("/upload")
public class TestUploadServlet extends HttpFrameAcceptor {

    @Override
    protected void doAccept(NioSocketChannel ch, HttpFrame frame) throws Exception {
        String res;
        if (frame.hasContent()) {
            res = "yes server already accept your message :) " + frame.getRequestParams()
                    + " </BR><PRE style='font-size: 18px;color: #FF9800;'>"
                    + new String(frame.getContent()) + "</PRE>";
        } else {
            res = "yes server already accept your message :) " + frame.getRequestParams();
        }
        frame.setResponseHeader(HttpHeader.Content_Type, HttpStatic.text_html_utf8_bytes);
        frame.write(res, ch.getCharset());
        ch.flush(frame);
    }

}
