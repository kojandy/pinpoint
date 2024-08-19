package com.navercorp.pinpoint.profiler.context.grpc.mapper;

import com.navercorp.pinpoint.grpc.trace.PErrorContent;
import com.navercorp.pinpoint.profiler.context.ErrorInfo;
import com.navercorp.pinpoint.profiler.context.error.IntErrorInfo;
import com.navercorp.pinpoint.profiler.context.error.StringErrorInfo;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Qualifier;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapper(
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.COMPILE_ERROR,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ErrorContentMapper {
    PErrorContent.Builder errorContentBuilder = PErrorContent.newBuilder();

    default PErrorContent.Builder getErrorContentBuilder() {
        final PErrorContent.Builder builder = this.errorContentBuilder;
        builder.clear();
        return builder;
    }

    @Qualifier
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    @interface ToPErrorContent {
    }

    @ToPErrorContent
    @SubclassMapping(source = StringErrorInfo.class, target = PErrorContent.class)
    @SubclassMapping(source = IntErrorInfo.class, target = PErrorContent.class)
    PErrorContent map(ErrorInfo<?> errorInfo);

    @Mapping(source = "content", target = "stringValue")
    PErrorContent map(StringErrorInfo errorInfo);

    @Mapping(source = "content", target = "intValue")
    PErrorContent map(IntErrorInfo errorInfo);
}
