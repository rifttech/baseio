/*
 * Copyright 2015-2017 GenerallyCloud.com
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
package com.generallycloud.baseio.protocol;

import java.nio.charset.Charset;

import com.generallycloud.baseio.common.Releasable;
import com.generallycloud.baseio.component.ByteArrayBuffer;
import com.generallycloud.baseio.component.IoEventHandle;
import com.generallycloud.baseio.component.SocketChannelContext;

public interface Future extends Releasable {

	public abstract IoEventHandle getIoEventHandle();

	public abstract void setIoEventHandle(IoEventHandle ioEventHandle);

	public abstract SocketChannelContext getContext();

	public abstract boolean flushed();

	public abstract String getReadText();

	public abstract ByteArrayBuffer getWriteBuffer();

	public abstract void write(String text);
	
	public abstract void write(String text,Charset charset);

	public abstract void write(byte b);

	public abstract void write(byte b[]);

	public abstract void write(byte b[], int off, int len);

}
