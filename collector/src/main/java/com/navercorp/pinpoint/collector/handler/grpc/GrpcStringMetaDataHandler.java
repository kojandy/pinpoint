/*
 * Copyright 2019 NAVER Corp.
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

package com.navercorp.pinpoint.collector.handler.grpc;

import com.google.protobuf.GeneratedMessageV3;
import com.navercorp.pinpoint.collector.handler.RequestResponseHandler;
import com.navercorp.pinpoint.collector.service.StringMetaDataService;
import com.navercorp.pinpoint.common.server.bo.StringMetaDataBo;
import com.navercorp.pinpoint.grpc.MessageFormatUtils;
import com.navercorp.pinpoint.grpc.trace.PResult;
import com.navercorp.pinpoint.grpc.trace.PStringMetaData;
import com.navercorp.pinpoint.io.request.ServerHeader;
import com.navercorp.pinpoint.io.request.ServerRequest;
import com.navercorp.pinpoint.io.request.ServerResponse;
import com.navercorp.pinpoint.io.util.MessageType;
import io.grpc.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author emeroad
 */
@Service
public class GrpcStringMetaDataHandler implements RequestResponseHandler<GeneratedMessageV3, GeneratedMessageV3> {
    private final Logger logger = LogManager.getLogger(getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private final StringMetaDataService stringMetaDataService;

    public GrpcStringMetaDataHandler(StringMetaDataService stringMetaDataService) {
        this.stringMetaDataService = Objects.requireNonNull(stringMetaDataService, "stringMetaDataService");
    }

    @Override
    public MessageType type() {
        return MessageType.STRINGMETADATA;
    }

    @Override
    public void handleRequest(ServerRequest<GeneratedMessageV3> serverRequest, ServerResponse<GeneratedMessageV3> serverResponse) {
        final GeneratedMessageV3 data = serverRequest.getData();
        final ServerHeader header = serverRequest.getHeader();
        if (data instanceof PStringMetaData stringMetaData) {
            PResult result = handleStringMetaData(header, stringMetaData);
            serverResponse.write(result);
        } else {
            logger.warn("Invalid request type. serverRequest={}", serverRequest);
            throw Status.INTERNAL.withDescription("Bad Request(invalid request type)").asRuntimeException();
        }
    }

    private PResult handleStringMetaData(ServerHeader header, final PStringMetaData stringMetaData) {
        if (logger.isDebugEnabled()) {
            logger.debug("Handle PStringMetaData={}", MessageFormatUtils.debugLog(stringMetaData));
        }

        try {
            final String agentId = header.getAgentId();
            final long agentStartTime = header.getAgentStartTime();

            final String stringValue = stringMetaData.getStringValue();

            final StringMetaDataBo stringMetaDataBo = new StringMetaDataBo(agentId, agentStartTime,
                    stringMetaData.getStringId(), stringValue);

            stringMetaDataService.insert(stringMetaDataBo);
            return PResult.newBuilder().setSuccess(true).build();
        } catch (Exception e) {
            logger.warn("Failed to handle stringMetaData={}", MessageFormatUtils.debugLog(stringMetaData), e);
            // Avoid detailed error messages.
            return PResult.newBuilder().setSuccess(false).setMessage("Internal Server Error").build();
        }
    }
}