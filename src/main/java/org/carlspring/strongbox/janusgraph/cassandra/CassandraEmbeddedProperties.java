package org.carlspring.strongbox.janusgraph.cassandra;

import static org.carlspring.strongbox.janusgraph.cassandra.CassandraEmbeddedPropertiesLoader.config;

import java.util.Collections;

import org.apache.cassandra.config.Config.CommitLogSync;
import org.apache.cassandra.config.Config.DiskFailurePolicy;
import org.apache.cassandra.config.ParameterizedClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "strongbox.cassandra")
public class CassandraEmbeddedProperties
{

    public CassandraEmbeddedProperties(int port)
    {
        //config = new Config();

        config.cluster_name = "Test Cluster";
        config.hinted_handoff_enabled = true;
        config.max_hint_window_in_ms = 10800000; // 3 hours
        config.hinted_handoff_throttle_in_kb = 1024;
        config.max_hints_delivery_threads = 2;
        config.hints_directory = "target/embeddedCassandra/hints";
        config.authenticator = "AllowAllAuthenticator";
        config.authorizer = "AllowAllAuthorizer";
        config.permissions_validity_in_ms = 2000;
        config.partitioner = "org.apache.cassandra.dht.Murmur3Partitioner";
        config.data_file_directories = new String[] { "target/embeddedCassandra/data" };
        config.commitlog_directory = "target/embeddedCassandra/commitlog";
        config.cdc_raw_directory = "target/embeddedCassandra/cdc";
        config.disk_failure_policy = DiskFailurePolicy.stop;
        config.key_cache_save_period = 14400;
        config.row_cache_size_in_mb = 0;
        config.row_cache_save_period = 0;
        config.saved_caches_directory = "target/embeddedCassandra/saved_caches";
        config.commitlog_sync = CommitLogSync.periodic;
        config.commitlog_sync_period_in_ms = 10000;
        config.commitlog_segment_size_in_mb = 32;
        config.seed_provider = new ParameterizedClass("org.apache.cassandra.locator.SimpleSeedProvider",
                Collections.singletonMap("seeds", "127.0.0.1"));
        config.concurrent_reads = 32;
        config.concurrent_writes = 32;
        config.trickle_fsync = false;
        config.trickle_fsync_interval_in_kb = 10240;
        config.storage_port = 7010;
        config.ssl_storage_port = 7011;
        config.listen_address = "127.0.0.1";

        config.start_native_transport = true;
        config.native_transport_port = port;

        config.start_rpc = false;
        config.incremental_backups = false;
        config.snapshot_before_compaction = false;
        config.auto_snapshot = false;
        config.column_index_size_in_kb = 64;
        config.compaction_throughput_mb_per_sec = 16;
        config.read_request_timeout_in_ms = 5000;
        config.range_request_timeout_in_ms = 10000;
        config.write_request_timeout_in_ms = 2000;
        config.cas_contention_timeout_in_ms = 1000;
        config.truncate_request_timeout_in_ms = 60000;
        config.request_timeout_in_ms = 10000;
        config.cross_node_timeout = false;
        config.endpoint_snitch = "SimpleSnitch";
        config.dynamic_snitch_update_interval_in_ms = 100;
        config.dynamic_snitch_reset_interval_in_ms = 600000;
        config.dynamic_snitch_badness_threshold = 0.1;
        config.request_scheduler = "org.apache.cassandra.scheduler.NoScheduler";
        config.index_interval = 128;
    }

}