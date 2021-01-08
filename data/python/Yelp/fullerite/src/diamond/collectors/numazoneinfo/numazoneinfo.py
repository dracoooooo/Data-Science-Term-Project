# coding=utf-8

"""
This class collects data on NUMA Zone page stats

#### Dependencies

* /proc/zoneinfo

"""

import os
from re import compile as re_compile

import diamond.collector

PROC_ZONEINFO = '/proc/zoneinfo'
node_re = re_compile(r'^Node\s+(?P<node>\d+),\s+zone\s+(?P<zone>\w+)$')


class NUMAZoneInfoCollector(diamond.collector.Collector):

    def get_default_config(self):
        """
        Returns the default collector settings
        """
        config = super(NUMAZoneInfoCollector, self).get_default_config()
        config.update({
            'path': 'numazoneinfo',
            'proc_path': PROC_ZONEINFO,
        })
        return config

    def collect(self):
        try:
            filepath = self.config['proc_path']

            with open(filepath, 'r') as file_handle:
                node = ''
                zone = ''
                numlines_to_process = 0

                for line in file_handle:
                    match = node_re.match(line)

                    if numlines_to_process > 0:
                        numlines_to_process -= 1
                        statname, metric_value = line.split('pages')[-1].split()
                        metric_name = '.'.join(['numa', statname])

                        self.dimensions = {}
                        if node:
                            self.dimensions['node'] = node
                        if zone:
                            self.dimensions['zone'] = zone

                        self.publish(metric_name, metric_value)

                    if match:
                        self.log.debug("Matched: %s %s" %
                                      (match.group('node'), match.group('zone')))

                        node = match.group('node') or ''
                        zone = match.group('zone') or ''

                        # We get 4 lines afterwards for free, min, low, and high
                        # page thresholds
                        numlines_to_process = 4

        except Exception as e:
            self.log.error('Failed because: %s' % str(e))
            return None
