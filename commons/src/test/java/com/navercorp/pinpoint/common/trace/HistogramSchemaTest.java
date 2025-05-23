/*
 * Copyright 2019 NAVER Corp.
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

package com.navercorp.pinpoint.common.trace;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author emeroad
 */
public class HistogramSchemaTest {

    @Test
    public void testFindHistogramSlot() {
        HistogramSchema histogramSchema = ServiceType.STAND_ALONE.getHistogramSchema();
        Assertions.assertEquals(1000, histogramSchema.findHistogramSlot(999, false).getSlotTime());
        Assertions.assertEquals(1000, histogramSchema.findHistogramSlot(1000, false).getSlotTime());
        Assertions.assertEquals(3000, histogramSchema.findHistogramSlot(1111, false).getSlotTime());
    }

}
