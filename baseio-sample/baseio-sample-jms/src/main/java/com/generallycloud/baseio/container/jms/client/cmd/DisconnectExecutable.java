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
package com.generallycloud.baseio.container.jms.client.cmd;

import java.util.HashMap;

import com.generallycloud.baseio.common.CloseUtil;
import com.generallycloud.baseio.connector.ChannelConnector;
import com.generallycloud.baseio.container.jms.cmd.CmdResponse;
import com.generallycloud.baseio.container.jms.cmd.CommandContext;

@Deprecated
public class DisconnectExecutable extends MQCommandExecutor {

    @Override
    public CmdResponse exec(CommandContext context, HashMap<String, String> params) {

        CmdResponse response = new CmdResponse();

        ChannelConnector connector = getClientConnector(context);

        if (connector == null) {
            response.setResponse("请先登录！");
            return response;
        }

        //FXIME logout
        //		connector.logout();

        CloseUtil.close(connector);

        setMessageBrowser(context, null);
        setClientConnector(context, null);

        response.setResponse("已断开连接！");
        return response;
    }
}
