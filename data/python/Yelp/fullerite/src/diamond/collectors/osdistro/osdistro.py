# coding=utf-8

"""
The OSDistroCollector collects
thedistribution of the OS on a machine.

#### Dependencies

 * /usr/bin/lsb_release

"""

import diamond.collector
from subprocess import Popen, PIPE


class OSDistroCollector(diamond.collector.Collector):

    def collect(self):
        try:
            p = Popen(['/usr/bin/lsb_release', '-sir'], stdout=PIPE, stderr=PIPE)
            output, errors = p.communicate()
            if errors:
                self.log.error(
                    "Could not run lsb_release: {0}".format(errors)
                )
                return None

            dimensions_value = output.replace('\n', ' ').strip().strip('"').strip("'")

            self.dimensions = { 'os_distro': dimensions_value }
            self.publish('OSDistribution', 1)
        except Exception as e:
            self.log.error(
                "Failed to get os distro release due to {0!s}".format(e)
            )
