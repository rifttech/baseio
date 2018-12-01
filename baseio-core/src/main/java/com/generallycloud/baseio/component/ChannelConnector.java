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
package com.generallycloud.baseio.component;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.generallycloud.baseio.LifeCycleUtil;
import com.generallycloud.baseio.TimeoutException;
import com.generallycloud.baseio.collection.DelayedQueue.DelayTask;
import com.generallycloud.baseio.common.Assert;
import com.generallycloud.baseio.common.Util;
import com.generallycloud.baseio.concurrent.Callback;
import com.generallycloud.baseio.concurrent.Waiter;

/**
 * @author wangkai
 *
 */
public final class ChannelConnector extends ChannelContext implements Closeable {

    private NioSocketChannel                    ch;
    private NioEventLoop                        eventLoop;
    private SocketChannel                       javaChannel;
    private volatile boolean                    callbacked = true;
    private volatile Callback<NioSocketChannel> callback;
    private volatile DelayTask                  timeoutTask;

    public ChannelConnector(int port) {
        this("127.0.0.1", port);
    }

    public ChannelConnector(NioEventLoop eventLoop, String host, int port) {
        this(eventLoop.getGroup(), host, port);
        this.eventLoop = eventLoop;
    }

    public ChannelConnector(NioEventLoopGroup group) {
        this(group, "127.0.0.1", 0);
    }

    public ChannelConnector(NioEventLoopGroup group, String host, int port) {
        super(group, host, port);
        if (!group.isSharable() && !group.isRunning()) {
            group.setEventLoopSize(1);
        }
    }

    public ChannelConnector(String host, int port) {
        this(new NioEventLoopGroup(1), host, port);
    }

    @Override
    public synchronized void close() throws IOException {
        Util.close(ch);
        Util.close(javaChannel);
        LifeCycleUtil.stop(this);
        if (!getProcessorGroup().isSharable()) {
            this.eventLoop = null;
        }
        this.ch = null;
    }

    public synchronized NioSocketChannel connect() throws IOException {
        return connect(3000);
    }

    public synchronized NioSocketChannel connect(long timeout) throws IOException {
        ConnectCallback<NioSocketChannel> callback = new ConnectCallback<>();
        this.connect(callback, timeout);
        if (eventLoop.inEventLoop()) {
            throw new IOException("can not blocking connect in its event loop");
        }
        if (callback.await(timeout)) {
            Util.close(this);
            throw new TimeoutException("connect to " + getServerAddress() + " time out");
        }
        if (callback.isFailed()) {
            Util.close(this);
            Throwable ex = callback.getThrowable();
            if (ex instanceof IOException) {
                throw (IOException) callback.getThrowable();
            }
            throw new IOException("connect failed", ex);
        }
        return getChannel();
    }

    public synchronized void connect(Callback<NioSocketChannel> callback) throws IOException {
        connect(callback, 3000);
    }

    public synchronized void connect(Callback<NioSocketChannel> callback, long timeout)
            throws IOException {
        Assert.notNull(callback, "null callback");
        if (isConnected()) {
            callback.call(ch, null);
            return;
        }
        if (!callbacked) {
            throw new IOException("connect is pending");
        }
        this.callbacked = false;
        this.timeoutTask = new TimeoutTask(this, timeout);
        this.callback = callback;
        this.getProcessorGroup().setContext(this);
        LifeCycleUtil.start(getProcessorGroup());
        LifeCycleUtil.start(this);
        if (eventLoop == null) {
            eventLoop = getProcessorGroup().getNext();
        }
        this.javaChannel = SocketChannel.open();
        this.javaChannel.configureBlocking(false);
        this.eventLoop.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    javaChannel.connect(getServerAddress());
                    eventLoop.registSelector(ChannelConnector.this, SelectionKey.OP_CONNECT);
                    eventLoop.schedule(timeoutTask);
                } catch (Throwable e) {
                    channelEstablish(null, e);
                }
            }
        });
    }

    @Override
    protected void channelEstablish(NioSocketChannel ch, Throwable ex) {
        if (!callbacked) {
            this.ch = ch;
            this.callbacked = true;
            this.timeoutTask.cancel();
            try {
                this.callback.call(ch, ex);
            } catch (Throwable e) {
                if (e instanceof Error) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public NioSocketChannel getChannel() {
        return ch;
    }

    public NioEventLoop getEventLoop() {
        return eventLoop;
    }

    @Override
    public SocketChannel getSelectableChannel() {
        return javaChannel;
    }

    @Override
    public boolean isActive() {
        return isConnected();
    }

    public boolean isConnected() {
        NioSocketChannel ch = this.ch;
        return ch != null && ch.isOpened();
    }

    @Override
    public String toString() {
        NioSocketChannel ch = this.ch;
        if (ch == null) {
            return super.toString();
        }
        return ch.toString();
    }

    class ConnectCallback<T> extends Waiter<T> {

        @Override
        public void call(T res, Throwable ex) {
            synchronized (this) {
                this.isDone = true;
                this.response = res;
                this.throwable = ex;
                this.notify();
                if (isTimeouted()) {
                    Util.close((Closeable) res);
                }
            }
        }
    }

    class TimeoutTask extends DelayTask {

        final ChannelConnector connector;
        final NioEventLoop     eventLoop;
        final SocketChannel    channel;

        public TimeoutTask(ChannelConnector connector, long delay) {
            super(delay);
            this.connector = connector;
            this.eventLoop = connector.eventLoop;
            this.channel = connector.javaChannel;
        }

        @Override
        public void run() {
            this.eventLoop.cancelConnect(this.channel);
            this.connector.channelEstablish(null, new TimeoutException("connect timeout"));
        }

    }

}
