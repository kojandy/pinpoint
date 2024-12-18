/*
 * Copyright 2018 NAVER Corp.
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
package com.navercorp.pinpoint.common.server.bo.codec.stat.join;

import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatDataPointCodec;
import com.navercorp.pinpoint.common.server.bo.codec.stat.ApplicationStatCodec;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.AgentStatHeaderDecoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.AgentStatHeaderEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.BitCountingHeaderDecoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.BitCountingHeaderEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.JoinLongFieldEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.JoinLongFieldStrategyAnalyzer;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.ApplicationStatDecodingContext;
import com.navercorp.pinpoint.common.server.bo.stat.join.JoinFileDescriptorBo;
import com.navercorp.pinpoint.common.server.bo.stat.join.JoinLongFieldBo;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Roy Kim
 */
@Component
public class FileDescriptorCodec implements ApplicationStatCodec<JoinFileDescriptorBo> {

    private static final byte VERSION = 1;

    private final AgentStatDataPointCodec codec;

    public FileDescriptorCodec(AgentStatDataPointCodec codec) {
        this.codec = Objects.requireNonNull(codec, "codec");
    }

    @Override
    public byte getVersion() {
        return VERSION;
    }

    @Override
    public void encodeValues(Buffer valueBuffer, List<JoinFileDescriptorBo> joinFileDescriptorBoList) {
        Assert.notEmpty(joinFileDescriptorBoList, "joinFileDescriptorBoList");

        final int numValues = joinFileDescriptorBoList.size();
        valueBuffer.putVInt(numValues);
        List<Long> timestamps = new ArrayList<>(numValues);
        JoinLongFieldStrategyAnalyzer.Builder openFileDescriptorCountAnalyzerBuilder = new JoinLongFieldStrategyAnalyzer.Builder();

        for (JoinFileDescriptorBo joinFileDescriptorBo : joinFileDescriptorBoList) {
            timestamps.add(joinFileDescriptorBo.getTimestamp());
            openFileDescriptorCountAnalyzerBuilder.addValue(joinFileDescriptorBo.getOpenFdCountJoinValue());
        }
        codec.encodeTimestamps(valueBuffer, timestamps);
        encodeDataPoints(valueBuffer, openFileDescriptorCountAnalyzerBuilder.build());
    }

    private void encodeDataPoints(Buffer valueBuffer, JoinLongFieldStrategyAnalyzer openFileDescriptorCountAnalyzer) {
        // encode header
        AgentStatHeaderEncoder headerEncoder = new BitCountingHeaderEncoder();

        final byte[] codes = openFileDescriptorCountAnalyzer.getBestStrategy().getCodes();
        for (byte code : codes) {
            headerEncoder.addCode(code);
        }

        final byte[] header = headerEncoder.getHeader();
        valueBuffer.putPrefixedBytes(header);
        // encode values
        this.codec.encodeValues(valueBuffer, openFileDescriptorCountAnalyzer.getBestStrategy(), openFileDescriptorCountAnalyzer.getValues());
    }

    @Override
    public List<JoinFileDescriptorBo> decodeValues(Buffer valueBuffer, ApplicationStatDecodingContext decodingContext) {
        final String id = decodingContext.getApplicationId();
        final long baseTimestamp = decodingContext.getBaseTimestamp();
        final long timestampDelta = decodingContext.getTimestampDelta();
        final long initialTimestamp = baseTimestamp + timestampDelta;

        int numValues = valueBuffer.readVInt();
        List<Long> timestamps = this.codec.decodeTimestamps(initialTimestamp, valueBuffer, numValues);

        // decode headers
        final byte[] header = valueBuffer.readPrefixedBytes();
        AgentStatHeaderDecoder headerDecoder = new BitCountingHeaderDecoder(header);
        JoinLongFieldEncodingStrategy openFileDescriptorCountEncodingStrategy = JoinLongFieldEncodingStrategy.getFromCode(headerDecoder.getCode(), headerDecoder.getCode(), headerDecoder.getCode(), headerDecoder.getCode(), headerDecoder.getCode());

        // decode values
        final List<JoinLongFieldBo> openFileDescriptorCounts = this.codec.decodeValues(valueBuffer, openFileDescriptorCountEncodingStrategy, numValues);

        List<JoinFileDescriptorBo> joinFileDescriptorBoList = new ArrayList<>(numValues);
        for (int i = 0; i < numValues; i++) {
            JoinFileDescriptorBo joinFileDescriptorBo = new JoinFileDescriptorBo();
            joinFileDescriptorBo.setId(id);
            joinFileDescriptorBo.setTimestamp(timestamps.get(i));
            joinFileDescriptorBo.setOpenFdCountJoinValue(openFileDescriptorCounts.get(i));
            joinFileDescriptorBoList.add(joinFileDescriptorBo);
        }
        return joinFileDescriptorBoList;
    }
}
