package com.navercorp.pinpoint.common.server.bo;

import com.google.protobuf.Descriptors;
import com.navercorp.pinpoint.grpc.trace.PErrorContent;
import com.navercorp.pinpoint.grpc.trace.PErrorInfo;

public record ErrorInfoBo(int category, Object content) {
    public ErrorInfoBo(PErrorInfo errorInfo) {
        this(errorInfo.getCategory(), getContent(errorInfo));
    }

    private static Object getContent(PErrorInfo errorInfo) {
        if (!errorInfo.hasContent()) {
            return null;
        }
        PErrorContent content = errorInfo.getContent();

        Descriptors.Descriptor descriptorForType = content.getDescriptorForType();
        int number = content.getFieldCase().getNumber();
        Descriptors.FieldDescriptor fieldByNumber = descriptorForType.findFieldByNumber(number);
        return content.getField(fieldByNumber);
    }
}
