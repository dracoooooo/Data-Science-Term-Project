#!/usr/bin/python
# coding=utf-8
################################################################################

from test import CollectorTestCase
from test import get_collector_config
from test import unittest
from mock import Mock
from mock import patch

from diamond.collector import Collector

from cassandra_jolokia import CassandraJolokiaCollector
import re

################################################################################

def find_metric(metric_list, metric_name):
    return filter(lambda metric:metric["name"].find(metric_name) > -1, metric_list)

def find_by_dimension(metric_list, key, val):
    return filter(lambda metric:metric["dimensions"][key] == val, metric_list)[0]

def list_request():
    return {'value': {'com.yelp':'bla'}, 'status':200}

class TestCassandraJolokiaCollector(CollectorTestCase):
    def setUp(self):
        config = get_collector_config('CassandraJolokiaCollector', {})

        self.collector = CassandraJolokiaCollector(config, None)

    def test_import(self):
        self.assertTrue(CassandraJolokiaCollector)

    @patch.object(Collector, 'flush')
    def test_should_create_dimension(self, publish_mock):
        self.collector.list_request = list_request

        def se(url):
            return self.getFixture("metrics.json")

        patch_urlopen = patch('urllib2.urlopen', Mock(side_effect=se))

        with patch_urlopen:
            self.collector.collect()
        self.assertEquals(len(self.collector.payload), 3827)

        metrics = find_metric(self.collector.payload, "org.apache.cassandra.metrics.ColumnFamily.LiveSSTableCount")
        self.assertNotEqual(len(metrics), 0)
        metric = find_by_dimension(metrics, "type", "compaction_history")
        self.assertEquals(metric["type"], "GAUGE")

        pending_task = find_metric(self.collector.payload,
                                   "org.apache.cassandra.metrics.CommitLog4.2.PendingTasks")
        self.assertNotEqual(len(pending_task), 0)

    @patch.object(Collector, 'flush')
    def test_should_create_type(self, publish_mock):
        self.collector.list_request = list_request
        def se(url):
            return self.getFixture("metrics.json")

        patch_urlopen = patch('urllib2.urlopen', Mock(side_effect=se))

        with patch_urlopen:
            self.collector.collect()
        self.assertEquals(len(self.collector.payload), 3827)

        metrics = find_metric(self.collector.payload, "org.apache.cassandra.metrics.ColumnFamily.CoordinatorReadLatency.count")
        self.assertNotEqual(len(metrics), 0)
        metric = find_by_dimension(metrics, "keyspace", "OpsCenter")
        self.assertEquals(metric["type"], "CUMCOUNTER")

    @patch.object(Collector, 'flush')
    def test_mbean_blacklisting(self, publish_mock):
        def se(url):
            if url.find("org.apache.cassandra.metrics") > 0:
                return self.getFixture("metrics.json")
            elif url.find("list/org.apache.cassandra.db") > 0:
                return self.getFixture("cas_db.json")
            elif url.find("org.apache.cassandra.db:type=StorageService") > 0:
                return Exception('storage service should be blacklisted')
            elif url.find("list?ifModifiedSince") > 0:
                return self.getFixture("cas_list.json")
            else:
                return self.getFixture("storage_proc.json")
        patch_urlopen = patch('urllib2.urlopen', Mock(side_effect=se))
        self.collector.config['mbean_blacklist'] = [
            'org.apache.cassandra.db:type=StorageService'
        ]

        with patch_urlopen:
            self.collector.collect()
        metrics = find_metric(self.collector.payload, "org.apache.cassandra.db.StorageProxy.cascontentiontimeout")
        self.assertNotEqual(len(metrics), 0)


################################################################################
if __name__ == "__main__":
    unittest.main()
