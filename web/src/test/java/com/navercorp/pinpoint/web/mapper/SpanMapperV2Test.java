package com.navercorp.pinpoint.web.mapper;

import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.buffer.OffsetFixedBuffer;
import com.navercorp.pinpoint.common.server.bo.AnnotationBo;
import com.navercorp.pinpoint.common.server.bo.ExceptionInfo;
import com.navercorp.pinpoint.common.server.bo.SpanBo;
import com.navercorp.pinpoint.common.server.bo.SpanEventBo;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.SpanDecoderV0;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.SpanDecodingContext;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.SpanEncoder;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.SpanEncoderV0;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.SpanEncodingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Woonduk Kang(emeroad)
 */
public class SpanMapperV2Test {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final SpanDecoderV0 decoder = new SpanDecoderV0();

    @Test
    public void test() {

        SpanBo span = new SpanBo();
        span.setServiceType((short) 1000);
        span.setExceptionInfo(new ExceptionInfo(1, "spanException"));

        SpanEventBo firstSpanEventBo = new SpanEventBo();
        firstSpanEventBo.setExceptionInfo(new ExceptionInfo(2, "first"));
        firstSpanEventBo.setEndElapsed(100);

        AnnotationBo annotationBo = AnnotationBo.of(200, "annotation");
        firstSpanEventBo.setAnnotationBoList(List.of(annotationBo));
        firstSpanEventBo.setServiceType((short) 1003);
        firstSpanEventBo.setSequence((short) 0);

        span.addSpanEvent(firstSpanEventBo);

        //// next
        SpanEventBo nextSpanEventBo = new SpanEventBo();
        nextSpanEventBo.setEndElapsed(200);
        nextSpanEventBo.setServiceType((short) 2003);
        nextSpanEventBo.setSequence((short) 1);


        span.addSpanEvent(nextSpanEventBo);

        SpanEncodingContext<SpanBo> encodingContext = new SpanEncodingContext<>(span);
        SpanEncoder encoder = new SpanEncoderV0();
        ByteBuffer byteBuffer = encoder.encodeSpanColumnValue(encodingContext);

        Buffer buffer = new OffsetFixedBuffer(byteBuffer.array(), byteBuffer.arrayOffset(), byteBuffer.remaining());

        SpanBo readSpan = new SpanBo();
        SpanDecodingContext decodingContext = new SpanDecodingContext();
        decoder.readSpanValue(buffer, readSpan, decodingContext);

        assertThat(readSpan.getSpanEventBoList()).hasSize(2);


        // span
        Assertions.assertEquals(1000, readSpan.getServiceType());
        Assertions.assertTrue(readSpan.hasException());
        final ExceptionInfo exceptionInfo = readSpan.getExceptionInfo();
        Assertions.assertEquals(1, exceptionInfo.id());
        Assertions.assertEquals("spanException", exceptionInfo.message());

        List<SpanEventBo> spanEventBoList = readSpan.getSpanEventBoList();
        SpanEventBo readFirst = spanEventBoList.get(0);
        SpanEventBo readNext = spanEventBoList.get(1);

        Assertions.assertEquals(100, readFirst.getEndElapsed());
        Assertions.assertEquals(200, readNext.getEndElapsed());

        ExceptionInfo exceptionInfo2 = readFirst.getExceptionInfo();
        Assertions.assertEquals(2, exceptionInfo2.id());
        Assertions.assertEquals("first", exceptionInfo2.message());

        Assertions.assertEquals(1003, readFirst.getServiceType());
        Assertions.assertEquals(2003, readNext.getServiceType());

        Assertions.assertEquals(0, readFirst.getSequence());
        Assertions.assertEquals(1, readNext.getSequence());

    }

}