/*
 * Copyright 2018 Naver Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance,the License.
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

package com.navercorp.pinpoint.it.plugin.mongodb;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.navercorp.pinpoint.it.plugin.utils.AgentPath;
import com.navercorp.pinpoint.it.plugin.utils.PluginITConstants;
import com.navercorp.pinpoint.it.plugin.utils.TestcontainersOption;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.DriverProperties;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.JDBCTestConstants;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PluginTest;
import com.navercorp.pinpoint.test.plugin.shared.SharedDependency;
import com.navercorp.pinpoint.test.plugin.shared.SharedTestLifeCycleClass;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;

/**
 * @author Roy Kim
 */
@PluginTest
@PinpointAgent(AgentPath.PATH)
@Dependency({
        "org.mongodb:mongodb-driver:[3.7.0,]",
        PluginITConstants.VERSION, JDBCTestConstants.VERSION
})
@SharedDependency({PluginITConstants.VERSION, JDBCTestConstants.VERSION, TestcontainersOption.TEST_CONTAINER, TestcontainersOption.MONGODB})
@SharedTestLifeCycleClass(MongodbServer.class)
public class MongoDBIT_3_7_x_IT extends MongoDBITBase {
    @AutoClose
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static URI uri;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        DriverProperties driverProperties = getDriverProperties();
        uri = new URI(driverProperties.getUrl());
        mongoClient = MongoClients.create("mongodb://" + uri.getHost() + ":" + uri.getPort());
        database = mongoClient.getDatabase("myMongoDbFake").withReadPreference(ReadPreference.secondaryPreferred()).withWriteConcern(WriteConcern.MAJORITY);
    }

    @Override
    Class<?> getMongoDatabaseClazz() throws ClassNotFoundException {
        return Class.forName("com.mongodb.client.internal.MongoCollectionImpl");
    }

    @Test
    public void testStatements() throws Exception {
        final MongoDBITHelper helper = new MongoDBITHelper();
        final String address = uri.getHost() + ":" + uri.getPort();
        helper.testConnection34(this, address, database, getMongoDatabaseClazz(), "ACKNOWLEDGED");
    }
}
