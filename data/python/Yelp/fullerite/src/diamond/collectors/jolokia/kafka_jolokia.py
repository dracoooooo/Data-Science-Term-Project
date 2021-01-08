# -*- coding: utf-8 -*-

"""
Collect Kafka metrics using jolokia agent

### Example Configuration

```
    host = localhost
    port = 8778
```
"""

from jolokia import JolokiaCollector, MBean
import re
import sys

class KafkaJolokiaCollector(JolokiaCollector):
    TOTAL_TOPICS = re.compile('kafka\.server:name=.*PerSec,type=BrokerTopicMetrics')

    def collect_bean(self, prefix, obj):
        if isinstance(obj, dict) and "Count" in obj:
            counter_val = obj["Count"]
            self.parse_dimension_bean(prefix, "count", counter_val)
        else:
            for k, v in obj.iteritems():
                if type(v) in [int, float, long]:
                    self.parse_dimension_bean(prefix, k, v)
                elif isinstance(v, dict):
                    self.collect_bean("%s.%s" % (prefix, k), v)
                elif isinstance(v, list):
                    self.interpret_bean_with_list("%s.%s" % (prefix, k), v)

    def patch_dimensions(self, bean, dims):
        metric_name = dims.pop("name", None)
        metric_type = dims.pop("type", None)
        # If the prefix matches the TOTAL_TOPICS regular expression it means
        # that, metric has no topic associated with it and is really for all topics on that broker
        if re.match(self.TOTAL_TOPICS, bean.prefix):
            dims["topic"] = "_TOTAL_"
        return metric_name, metric_type, dims

    def patch_metric_name(self, bean, metric_name_list):
        if self.config.get('prefix', None):
            metric_name_list = [self.config['prefix']] + metric_name_list

        metric_name_list.append(bean.bean_key.lower())
        return metric_name_list
