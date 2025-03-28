package com.navercorp.pinpoint.collector.uid.dao.hbase;

import com.navercorp.pinpoint.collector.uid.config.ApplicationUidConfig;
import com.navercorp.pinpoint.collector.uid.dao.ApplicationUidDao;
import com.navercorp.pinpoint.common.hbase.HbaseColumnFamily;
import com.navercorp.pinpoint.common.hbase.HbaseOperations;
import com.navercorp.pinpoint.common.hbase.HbaseTables;
import com.navercorp.pinpoint.common.hbase.RowMapper;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.common.hbase.async.AsyncHbaseOperations;
import com.navercorp.pinpoint.common.server.uid.ApplicationUid;
import com.navercorp.pinpoint.common.server.uid.ServiceUid;
import com.navercorp.pinpoint.common.server.util.ApplicationUidRowKeyUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.CheckAndMutate;
import org.apache.hadoop.hbase.client.CheckAndMutateResult;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Repository
@ConditionalOnProperty(value = "pinpoint.collector.application.uid.enable", havingValue = "true")
public class HbaseApplicationUidDao implements ApplicationUidDao {

    private static final HbaseColumnFamily APPLICATION_ID = HbaseTables.APPLICATION_UID;

    private final HbaseOperations hbaseOperations;
    private final AsyncHbaseOperations asyncHbaseOperations;
    private final TableNameProvider tableNameProvider;

    private final RowMapper<ApplicationUid> applicationIdMapper;

    public HbaseApplicationUidDao(@Qualifier("uidHbaseTemplate") HbaseOperations hbaseOperations,
                                  @Qualifier("uidAsyncTemplate") AsyncHbaseOperations asyncHbaseOperations,
                                  TableNameProvider tableNameProvider,
                                  @Qualifier("applicationUidMapper") RowMapper<ApplicationUid> applicationIdMapper) {
        this.hbaseOperations = Objects.requireNonNull(hbaseOperations, "hbaseOperations");
        this.asyncHbaseOperations = Objects.requireNonNull(asyncHbaseOperations, "asyncHbaseOperations");
        this.tableNameProvider = Objects.requireNonNull(tableNameProvider, "tableNameProvider");
        this.applicationIdMapper = Objects.requireNonNull(applicationIdMapper, "applicationIdMapper");
    }

    @Override
    @Cacheable(cacheNames = "applicationUidCache", key = "{#serviceUid, #applicationName}", cacheManager = ApplicationUidConfig.APPLICATION_UID_CACHE_NAME, unless = "#result == null")
    public ApplicationUid selectApplicationUid(ServiceUid serviceUid, String applicationName) {
        Get get = createGet(serviceUid, applicationName);

        TableName applicationIdTableName = tableNameProvider.getTableName(APPLICATION_ID.getTable());
        return hbaseOperations.get(applicationIdTableName, get, applicationIdMapper);
    }

    @Override
    public CompletableFuture<ApplicationUid> asyncSelectApplicationUid(ServiceUid serviceUid, String applicationName) {
        Get get = createGet(serviceUid, applicationName);

        TableName applicationIdTableName = tableNameProvider.getTableName(APPLICATION_ID.getTable());
        return asyncHbaseOperations.get(applicationIdTableName, get, applicationIdMapper);
    }

    private Get createGet(ServiceUid serviceUid, String applicationName) {
        byte[] rowKey = ApplicationUidRowKeyUtils.makeRowKey(serviceUid, applicationName);

        Get get = new Get(rowKey);
        get.addColumn(APPLICATION_ID.getName(), APPLICATION_ID.getName());
        return get;
    }

    @Override
    public boolean insertApplicationUidIfNotExists(ServiceUid serviceUid, String applicationName, ApplicationUid applicationUid) {
        CheckAndMutate checkAndMutate = createCheckAndPut(serviceUid, applicationName, applicationUid);

        TableName applicationIdTableName = tableNameProvider.getTableName(APPLICATION_ID.getTable());
        CheckAndMutateResult checkAndMutateResult = hbaseOperations.checkAndMutate(applicationIdTableName, checkAndMutate);
        return checkAndMutateResult.isSuccess();
    }

    @Override
    public CompletableFuture<Boolean> asyncInsertApplicationUidIfNotExists(ServiceUid serviceUid, String applicationName, ApplicationUid applicationUid) {
        CheckAndMutate checkAndMutate = createCheckAndPut(serviceUid, applicationName, applicationUid);

        TableName applicationIdTableName = tableNameProvider.getTableName(APPLICATION_ID.getTable());
        return asyncHbaseOperations.checkAndMutate(applicationIdTableName, checkAndMutate)
                .thenApply(CheckAndMutateResult::isSuccess);
    }

    private CheckAndMutate createCheckAndPut(ServiceUid serviceUid, String applicationName, ApplicationUid applicationUid) {
        byte[] rowKey = ApplicationUidRowKeyUtils.makeRowKey(serviceUid, applicationName);

        Put put = new Put(rowKey);
        put.addColumn(APPLICATION_ID.getName(), APPLICATION_ID.getName(), Bytes.toBytes(applicationUid.getUid()));

        CheckAndMutate.Builder builder = CheckAndMutate.newBuilder(rowKey);
        builder.ifNotExists(APPLICATION_ID.getName(), APPLICATION_ID.getName());
        return builder.build(put);
    }
}
