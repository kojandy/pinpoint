/*
 * Copyright 2024 NAVER Corp.
 *
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

package com.navercorp.pinpoint.metric.common.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.metric.common.mybatis.typehandler.TagSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonSerialize(using = TagSerializer.class)
public class Tags {
    private final List<Tag> tags;

    public Tags() {
        this.tags = new ArrayList<>();
    }

    public Tags(List<Tag> tags) {
        this.tags = Objects.requireNonNull(tags, "tags");
    }

    @JsonAnySetter
    public void add(String name, String value) {
        tags.add(new Tag(name, value));
    }

    public List<Tag> getTags() {
        return tags;
    }
}
