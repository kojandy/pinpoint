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

package com.navercorp.pinpoint.collector.dao.hbase;

import com.navercorp.pinpoint.collector.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.common.hbase.HbaseColumnFamily;
import com.navercorp.pinpoint.common.hbase.HbaseOperations;
import com.navercorp.pinpoint.common.hbase.HbaseTables;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.common.server.bo.AgentInfoBo;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * application names list.
 *
 * @author netspider
 * @author emeroad
 */
@Repository
public class HbaseApplicationIndexDao implements ApplicationIndexDao {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private static final HbaseColumnFamily DESCRIPTOR = HbaseTables.APPLICATION_INDEX_AGENTS;

    private final HbaseOperations hbaseTemplate;

    private final TableNameProvider tableNameProvider;

    public HbaseApplicationIndexDao(HbaseOperations hbaseTemplate, TableNameProvider tableNameProvider) {
        this.hbaseTemplate = Objects.requireNonNull(hbaseTemplate, "hbaseTemplate");
        this.tableNameProvider = Objects.requireNonNull(tableNameProvider, "tableNameProvider");
    }

    @Override
    public void insert(final AgentInfoBo agentInfo) {
        Objects.requireNonNull(agentInfo, "agentInfo");

        byte[] rowKey = Bytes.toBytes(agentInfo.getApplicationName());
        final Put put = new Put(rowKey, true);
        final byte[] qualifier = Bytes.toBytes(agentInfo.getAgentId());
        final byte[] value = Bytes.toBytes((short) agentInfo.getServiceTypeCode());
        put.addColumn(DESCRIPTOR.getName(), qualifier, value);

        final TableName applicationIndexTableName = tableNameProvider.getTableName(DESCRIPTOR.getTable());
        hbaseTemplate.put(applicationIndexTableName, put);

        logger.debug("Insert ApplicationIndex: {}", agentInfo);
    }
}