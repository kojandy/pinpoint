/*
 * Copyright 2020 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.name;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Woonduk Kang(emeroad)
 */
public class AgentIdResolverBuilder {
    private final List<AgentProperties> agentProperties = new ArrayList<>();
    

    public void addProperties(AgentIdSourceType sourceType, Function<String, String> env) {
        Objects.requireNonNull(sourceType, "sourceType");
        Objects.requireNonNull(env, "env");

        AgentProperties properties = new AgentProperties(sourceType, env);
        this.agentProperties.add(properties);
    }

    public AgentIdResolver build() {
        List<AgentProperties> copy = new ArrayList<>(this.agentProperties);
        return new AgentIdResolver(copy);
    }
}
