/*
 * Copyright 2014 NAVER Corp.
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

package com.navercorp.pinpoint.profiler.instrument.interceptor;

import com.navercorp.pinpoint.bootstrap.context.TraceBlock;
import com.navercorp.pinpoint.bootstrap.interceptor.ApiIdAwareAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor0;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor1;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor2;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor3;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor4;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor5;
import com.navercorp.pinpoint.bootstrap.interceptor.BlockApiIdAwareAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.interceptor.BlockAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.interceptor.BlockAroundInterceptor0;
import com.navercorp.pinpoint.bootstrap.interceptor.BlockAroundInterceptor1;
import com.navercorp.pinpoint.bootstrap.interceptor.BlockAroundInterceptor2;
import com.navercorp.pinpoint.bootstrap.interceptor.BlockAroundInterceptor3;
import com.navercorp.pinpoint.bootstrap.interceptor.BlockAroundInterceptor4;
import com.navercorp.pinpoint.bootstrap.interceptor.BlockAroundInterceptor5;
import com.navercorp.pinpoint.bootstrap.interceptor.BlockStaticAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.interceptor.Interceptor;
import com.navercorp.pinpoint.bootstrap.interceptor.StaticAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.interceptor.annotation.IgnoreMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Woonduk Kang(emeroad)
 */
public class InterceptorDefinitionFactory {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private final List<TypeHandler> detectHandlers;

    public InterceptorDefinitionFactory() {
        this.detectHandlers = register();
    }

    public InterceptorDefinition createInterceptorDefinition(Class<?> interceptorClazz) {
        Objects.requireNonNull(interceptorClazz, "interceptorClazz");

        for (TypeHandler typeHandler : detectHandlers) {
            final InterceptorDefinition interceptorDefinition = typeHandler.resolveType(interceptorClazz);
            if (interceptorDefinition != null) {
                return interceptorDefinition;
            }
        }
        throw new RuntimeException("unsupported Interceptor Type. " + interceptorClazz.getName());
    }


    private List<TypeHandler> register() {
        final List<TypeHandler> typeHandlerList = new ArrayList<TypeHandler>();

        addTypeHandler(typeHandlerList, AroundInterceptor.class, InterceptorType.ARRAY_ARGS);
        addTypeHandler(typeHandlerList, AroundInterceptor0.class, InterceptorType.BASIC);
        addTypeHandler(typeHandlerList, AroundInterceptor1.class, InterceptorType.BASIC);
        addTypeHandler(typeHandlerList, AroundInterceptor2.class, InterceptorType.BASIC);
        addTypeHandler(typeHandlerList, AroundInterceptor3.class, InterceptorType.BASIC);
        addTypeHandler(typeHandlerList, AroundInterceptor4.class, InterceptorType.BASIC);
        addTypeHandler(typeHandlerList, AroundInterceptor5.class, InterceptorType.BASIC);
        addTypeHandler(typeHandlerList, StaticAroundInterceptor.class, InterceptorType.STATIC);
        addTypeHandler(typeHandlerList, ApiIdAwareAroundInterceptor.class, InterceptorType.API_ID_AWARE);
        // block
        addTypeHandler(typeHandlerList, BlockAroundInterceptor.class, InterceptorType.ARRAY_ARGS);
        addTypeHandler(typeHandlerList, BlockAroundInterceptor0.class, InterceptorType.BASIC);
        addTypeHandler(typeHandlerList, BlockAroundInterceptor1.class, InterceptorType.BASIC);
        addTypeHandler(typeHandlerList, BlockAroundInterceptor2.class, InterceptorType.BASIC);
        addTypeHandler(typeHandlerList, BlockAroundInterceptor3.class, InterceptorType.BASIC);
        addTypeHandler(typeHandlerList, BlockAroundInterceptor4.class, InterceptorType.BASIC);
        addTypeHandler(typeHandlerList, BlockAroundInterceptor5.class, InterceptorType.BASIC);
        addTypeHandler(typeHandlerList, BlockStaticAroundInterceptor.class, InterceptorType.STATIC);
        addTypeHandler(typeHandlerList, BlockApiIdAwareAroundInterceptor.class, InterceptorType.API_ID_AWARE);

        return typeHandlerList;
    }

    private void addTypeHandler(List<TypeHandler> typeHandlerList, Class<? extends Interceptor> interceptorClazz, InterceptorType arrayArgs) {
        final TypeHandler typeHandler = createInterceptorTypeHandler(interceptorClazz, arrayArgs);
        typeHandlerList.add(typeHandler);
    }

    private TypeHandler createInterceptorTypeHandler(Class<? extends Interceptor> interceptorClazz, InterceptorType interceptorType) {
        Objects.requireNonNull(interceptorClazz, "interceptorClazz");
        Objects.requireNonNull(interceptorType, "interceptorType");

        final Method[] declaredMethods = interceptorClazz.getDeclaredMethods();
        if (declaredMethods.length != 2) {
            throw new RuntimeException("invalid Type");
        }
        final String before = "before";
        final Method beforeMethod = findMethodByName(declaredMethods, before);
        final Class<?>[] beforeParamList = beforeMethod.getParameterTypes();

        final String after = "after";
        final Method afterMethod = findMethodByName(declaredMethods, after);
        final Class<?>[] afterParamList = afterMethod.getParameterTypes();

        return new TypeHandler(interceptorClazz, interceptorType, before, beforeParamList, after, afterParamList);
    }


    private Method findMethodByName(Method[] declaredMethods, String methodName) {
        Method findMethod = null;
        int count = 0;
        for (Method method : declaredMethods) {
            if (method.getName().equals(methodName)) {
                count++;
                findMethod = method;
            }
        }
        if (findMethod == null) {
            throw new RuntimeException(methodName + " not found");
        }
        if (count > 1 ) {
            throw new RuntimeException("duplicated method exist. methodName:" + methodName);
        }
        return findMethod;
    }


    private class TypeHandler {
        private final Class<? extends Interceptor> interceptorClazz;
        private final InterceptorType interceptorType;
        private final String before;
        private final Class<?>[] beforeParamList;
        private final String after;
        private final Class<?>[] afterParamList;

        public TypeHandler(Class<? extends Interceptor> interceptorClazz, InterceptorType interceptorType, String before, final Class<?>[] beforeParamList, final String after, final Class<?>[] afterParamList) {
            this.interceptorClazz = Objects.requireNonNull(interceptorClazz, "interceptorClazz");
            this.interceptorType = Objects.requireNonNull(interceptorType, "interceptorType");
            this.before = Objects.requireNonNull(before, "before");
            this.beforeParamList = Objects.requireNonNull(beforeParamList, "beforeParamList");
            this.after = Objects.requireNonNull(after, "after");
            this.afterParamList = Objects.requireNonNull(afterParamList, "afterParamList");
        }


        public InterceptorDefinition resolveType(Class<?> targetClazz) {
            if(!this.interceptorClazz.isAssignableFrom(targetClazz)) {
                return null;
            }
            @SuppressWarnings("unchecked")
            final Class<? extends Interceptor> casting = (Class<? extends Interceptor>) targetClazz;
            return createInterceptorDefinition(casting);
        }

        private InterceptorDefinition createInterceptorDefinition(Class<? extends Interceptor> targetInterceptorClazz) {

            final Method beforeMethod = searchMethod(targetInterceptorClazz, before, beforeParamList);
            if (beforeMethod == null) {
                throw new RuntimeException(before + " method not found. " + Arrays.toString(beforeParamList));
            }
            final boolean beforeIgnoreMethod = beforeMethod.isAnnotationPresent(IgnoreMethod.class);
            final boolean blockType = beforeMethod.getReturnType() == TraceBlock.class;
            final Method afterMethod = searchMethod(targetInterceptorClazz, after, afterParamList);
            if (afterMethod == null) {
                throw new RuntimeException(after + " method not found. " + Arrays.toString(afterParamList));
            }
            final boolean afterIgnoreMethod = afterMethod.isAnnotationPresent(IgnoreMethod.class);


            if (beforeIgnoreMethod && afterIgnoreMethod) {
                return new DefaultInterceptorDefinition(interceptorClazz, targetInterceptorClazz, interceptorType, CaptureType.NON, null, null);
            }
            if (beforeIgnoreMethod) {
                if (blockType) {
                    throw new RuntimeException(before + " not allowed return. " + Arrays.toString(beforeParamList));
                }
                return new DefaultInterceptorDefinition(interceptorClazz, targetInterceptorClazz, interceptorType, CaptureType.AFTER, null, afterMethod);
            }
            if (afterIgnoreMethod) {
                if (blockType) {
                    throw new RuntimeException(after + " not allowed return. " + Arrays.toString(afterParamList));
                }
                return new DefaultInterceptorDefinition(interceptorClazz, targetInterceptorClazz, interceptorType, CaptureType.BEFORE, beforeMethod, null);
            }
            if (blockType) {
                return new DefaultInterceptorDefinition(interceptorClazz, targetInterceptorClazz, interceptorType, CaptureType.BLOCK_AROUND, beforeMethod, afterMethod);
            }

            return new DefaultInterceptorDefinition(interceptorClazz, targetInterceptorClazz, interceptorType, CaptureType.AROUND, beforeMethod, afterMethod);
        }

        private Method searchMethod(Class<?> interceptorClazz, String searchMethodName, Class<?>[] searchMethodParameter) {
            Objects.requireNonNull(searchMethodName, "searchMethodName");

//          only DeclaredMethod search ?
//            try {
//                return targetInterceptorClazz.getDeclaredMethod(searchMethodName, searchMethodParameter);
//            } catch (NoSuchMethodException ex) {
//                logger.debug(searchMethodName + " DeclaredMethod not found. search parent class");
//            }
            // search all class
            try {
                return interceptorClazz.getMethod(searchMethodName, searchMethodParameter);
            } catch (NoSuchMethodException ex) {
                logger.debug(searchMethodName +" DeclaredMethod not found.");
            }
            return null;
        }
    }



}
