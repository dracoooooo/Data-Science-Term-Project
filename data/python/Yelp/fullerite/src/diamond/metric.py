# coding=utf-8

import time
import re
import logging
from error import DiamondException


class Metric(object):

    _METRIC_TYPES = ['COUNTER', 'GAUGE', 'CUMCOUNTER']

    def __init__(self, path, value, raw_value=None, timestamp=None, precision=0,
                 metric_type='COUNTER', ttl=None, host="ignored", dimensions=None):
        """
        Create new instance of the Metric class

        Takes:
            path=string: string the specifies the path of the metric
            value=[float|int]: the value to be submitted
            timestamp=[float|int]: the timestamp, in seconds since the epoch
            (as from time.time()) precision=int: the precision to apply.
            Generally the default (2) should work fine.
        """

        # Validate the path, value and metric_type submitted
        if (None in [path, value] or metric_type not in self._METRIC_TYPES):
            raise DiamondException(("Invalid parameter when creating new "
                                    "Metric with path: %r value: %r "
                                    "metric_type: %r")
                                   % (path, value, metric_type))

        # If no timestamp was passed in, set it to the current time
        if timestamp is None:
            timestamp = int(time.time())
        else:
            # If the timestamp isn't an int, then make it one
            if not isinstance(timestamp, int):
                try:
                    timestamp = int(timestamp)
                except ValueError, e:
                    raise DiamondException(("Invalid timestamp when "
                                            "creating new Metric %r: %s")
                                           % (path, e))

        # The value needs to be a float or an int.  If it is, great.  If not,
        # try to cast it to one of those.
        if not isinstance(value, (int, float)):
            try:
                if precision == 0:
                    value = round(float(value))
                else:
                    value = float(value)
            except ValueError, e:
                raise DiamondException(("Invalid value when creating new "
                                        "Metric %r: %s") % (path, e))

        # If dimensions were passed in make sure they are a dict
        if dimensions is not None:
            if not isinstance(dimensions, dict):
                raise DiamondException(("Invalid dimensions when "
                                        "creating new Metric %r: %s")
                                       % (path, dimensions))
            else:
                dimensions = dict(
                    (k, str(v)) for k, v in dimensions.iteritems()
                    if v is not None and isinstance(v, (int, float, str, unicode)) and k is not None and isinstance(k, str)
                )

        self.dimensions = dimensions
        self.path = path
        self.value = value
        self.raw_value = raw_value
        self.timestamp = timestamp
        self.precision = precision
        self.metric_type = metric_type
        self.ttl = ttl

    def __repr__(self):
        """
        Return the Metric as a string
        """
        if not isinstance(self.precision, (int, long)):
            log = logging.getLogger('diamond')
            log.warn('Metric %s does not have a valid precision', self.path)
            self.precision = 0

        # Set the format string
        fstring = "%%s %%0.%if %%i\n" % self.precision

        # Return formated string
        return fstring % (self.path, self.value, self.timestamp)

    @classmethod
    def parse(cls, string):
        """
        Parse a string and create a metric
        """
        match = re.match(r'^(?P<name>[A-Za-z0-9\.\-_]+)\s+'
                         + '(?P<value>[0-9\.]+)\s+'
                         + '(?P<timestamp>[0-9\.]+)(\n?)$',
                         string)
        try:
            groups = match.groupdict()
            # TODO: get precision from value string
            return Metric(groups['name'],
                          groups['value'],
                          float(groups['timestamp']))
        except:
            raise DiamondException(
                "Metric could not be parsed from string: %s." % string)

    def getPathPrefix(self):
        """
            Returns the path prefix path
            servers.cpu.total.idle
            return "servers"
        """
        return self.path.split('.')[0]

    def getCollectorPath(self):
        """
            Returns collector path
            servers.cpu.total.idle
            return "cpu"
        """
        return self.path.split('.')[1]

    def getMetricPath(self):
        """
            Returns the metric path after the collector name
            servers.cpu.total.idle
            return "total.idle"
        """
        path = self.path.split('.')[2:]
        return '.'.join(path)
