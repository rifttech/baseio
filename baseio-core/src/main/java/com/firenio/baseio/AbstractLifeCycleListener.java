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
package com.firenio.baseio;

public class AbstractLifeCycleListener implements LifeCycleListener {

    @Override
    public void lifeCycleFailure(LifeCycle lifeCycle, Exception exception) {

    }

    @Override
    public int lifeCycleListenerSortIndex() {
        return 0;
    }

    @Override
    public void lifeCycleStarted(LifeCycle lifeCycle) {

    }

    @Override
    public void lifeCycleStarting(LifeCycle lifeCycle) {

    }

    @Override
    public void lifeCycleStopped(LifeCycle lifeCycle) {

    }

    @Override
    public void lifeCycleStopping(LifeCycle lifeCycle) {

    }

}
