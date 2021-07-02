/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

package com.example.longpollingdemo.executor;

import java.util.concurrent.*;

/**
 * Unified thread pool creation factory, and actively create thread pool resources by ThreadPoolManager for unified life
 * cycle management {@link Managed}.
 *
 * <p>Unified thread pool creation factory without life cycle management {@link ExecutorFactory}.
 *
 * <p>two check style ignore will be removed after issue#2856 finished.
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
@SuppressWarnings({"PMD.ThreadPoolCreationRule", "checkstyle:overloadmethodsdeclarationorder",
        "checkstyle:missingjavadocmethod"})
public final class ExecutorFactory {

    public static final class Managed {
        
        private static final String DEFAULT_NAMESPACE = "nacos";
        
        private static final ThreadPoolManager THREAD_POOL_MANAGER = ThreadPoolManager.getInstance();

        /**
         * Create a new single scheduled executor service with input thread factory and register to manager.
         *
         * @param group         group name
         * @param threadFactory thread factory
         * @return new single scheduled executor service
         */
        public static ScheduledExecutorService newSingleScheduledExecutorService(final String group,
                final ThreadFactory threadFactory) {
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, threadFactory);
            THREAD_POOL_MANAGER.register(DEFAULT_NAMESPACE, group, executorService);
            return executorService;
        }

    }
}
