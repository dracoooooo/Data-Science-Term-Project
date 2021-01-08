#!/usr/bin/python
# coding=utf-8
################################################################################

from test import CollectorTestCase
from test import get_collector_config
from test import unittest
from mock import Mock
from mock import patch

from diamond.collector import Collector
from postfix import PostfixCollector

################################################################################

class TestYelpPostfixCollector(CollectorTestCase):
    def setUp(self):
        config = get_collector_config('PostfixCollector', {
            'host':     'localhost',
            'port':     7777,
            'interval': '1',
            'include_clients': True,
            'relay_mode': True,
        })

        self.collector = PostfixCollector(config, None)

    def test_import(self):
        self.assertTrue(PostfixCollector)

    @patch.object(Collector, 'publish_cumulative_counter')
    def test_should_work_with_synthetic_data(self, publish_mock):
        first_resp = self.getFixture('postfix-stats.1.json').getvalue()
        patch_collector = patch.object(
            PostfixCollector,
            'get_json',
            Mock(return_value=first_resp))

        patch_collector.start()
        self.collector.collect()
        patch_collector.stop()

        self.assertPublishedMany(publish_mock, {})

        second_resp = self.getFixture('postfix-stats.2.json').getvalue()
        patch_collector = patch.object(PostfixCollector,
                                       'get_json',
                                       Mock(return_value=second_resp))

        patch_collector.start()
        self.collector.collect()
        patch_collector.stop()

        metrics = {
            'postfix.incoming': 2,
        }
        self.assertPublishedMany(publish_mock, metrics)

        self.setDocExample(collector=self.collector.__class__.__name__,
                           metrics=metrics,
                           defaultpath=self.collector.config['path'])

    @patch.object(Collector, 'publish_cumulative_counter')
    def test_should_export_queue_metrics(self, publish_mock):
        first_resp = self.getFixture('postfix-queue.json').getvalue()
        patch_collector = patch.object(
            PostfixCollector,
            'get_json',
            Mock(return_value=first_resp))

        with patch_collector:
            self.collector.config['relay_mode'] = True
            self.collector.collect()
            self.collector.config['relay_mode'] = False

        metrics = [
            ('postfix.incoming', 38),
            ('postfix.incoming', 77),
            ('postfix.incoming', 10),
            ('postfix.incoming', 20),
            ('postfix.send', 542),
            ('postfix.send', 499)
        ]

        for expected_metric in metrics:
            k, v = expected_metric
            message = "metric : {0} is not equal to {1}".format(k, v)
            self.assertTrue(self.check_for_call(publish_mock, k, v), message)

    # Check in entire call_arg_list for given key and value
    def check_for_call(self, mock, key, value):
        call_found = False
        for call in mock.call_args_list:
            if call[0][0] == key and call[0][1] == value:
                call_found = True
        return call_found


################################################################################
if __name__ == "__main__":
    unittest.main()
