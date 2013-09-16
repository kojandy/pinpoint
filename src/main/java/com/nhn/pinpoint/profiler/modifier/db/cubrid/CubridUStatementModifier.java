package com.nhn.pinpoint.profiler.modifier.db.cubrid;

import java.security.ProtectionDomain;

import com.nhn.pinpoint.profiler.Agent;
import com.nhn.pinpoint.profiler.interceptor.bci.ByteCodeInstrumentor;
import com.nhn.pinpoint.profiler.interceptor.bci.InstrumentClass;
import com.nhn.pinpoint.profiler.interceptor.bci.InstrumentException;
import com.nhn.pinpoint.profiler.modifier.AbstractModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CubridUStatementModifier extends AbstractModifier {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public CubridUStatementModifier(ByteCodeInstrumentor byteCodeInstrumentor, Agent agent) {
		super(byteCodeInstrumentor, agent);
	}

	public String getTargetClass() {
		return "cubrid/jdbc/jci/UStatement";
	}

	public byte[] modify(ClassLoader classLoader, String javassistClassName, ProtectionDomain protectedDomain, byte[] classFileBuffer) {
		if (logger.isInfoEnabled()) {
			logger.info("Modifing. {}", javassistClassName);
		}
		this.byteCodeInstrumentor.checkLibrary(classLoader, javassistClassName);
		try {
			InstrumentClass ustatementClass = byteCodeInstrumentor.getClass(javassistClassName);

			return ustatementClass.toBytecode();
		} catch (InstrumentException e) {
			if (logger.isWarnEnabled()) {
				logger.warn(this.getClass().getSimpleName() + " modify fail. Cause:" + e.getMessage(), e);
			}
			return null;
		}
	}
}
