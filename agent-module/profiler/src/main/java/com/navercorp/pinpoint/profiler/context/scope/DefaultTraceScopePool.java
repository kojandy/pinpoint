/*
 * Copyright 2015 NAVER Corp.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.context.scope;

import com.navercorp.pinpoint.bootstrap.context.scope.TraceScope;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author jaehong.kim
 */
public class DefaultTraceScopePool {

    private final Map<String, TraceScope> map = new HashMap<>();

    public TraceScope get(String name) {
        Objects.requireNonNull(name, "name");

        return map.get(name);
    }

    public TraceScope add(String name) {
        Objects.requireNonNull(name, "name");

        return map.put(name, new DefaultTraceScope(name));
    }

    public TraceScope addBoundary(String name) {
        Objects.requireNonNull(name, "name");

        return map.put(name, new BoundaryTraceScope(name));
    }

    public void clear() {
        map.clear();
    }
}