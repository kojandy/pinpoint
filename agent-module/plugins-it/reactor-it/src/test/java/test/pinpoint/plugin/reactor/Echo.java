/*
 * Copyright 2018 NAVER Corp.
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

package test.pinpoint.plugin.reactor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class Echo {
    private final Logger logger = LogManager.getLogger(this.getClass());
    public String get(String message) {
        logger.info("echo:{}", message);
        return message;
    }

    public String get(String message, Exception exception) throws Exception {
        Objects.requireNonNull(exception, "exception");

        logger.info("echo:{}", message, exception);
        throw exception;
    }
}
