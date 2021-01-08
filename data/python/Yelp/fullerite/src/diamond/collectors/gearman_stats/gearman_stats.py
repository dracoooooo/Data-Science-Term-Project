# coding=utf-8

"""
Port of the ganglia gearman collector
Collects stats from gearman job server

#### Dependencies

 *  gearman

"""
from diamond.collector import str_to_bool

import diamond.collector
import os
import subprocess
import time

try:
    import gearman
except ImportError:
    gearman = None


class GearmanCollector(diamond.collector.Collector):

    def get_default_config_help(sef):
        config_help = super(GearmanCollector, self).get_default_config_help()
        config_help.update({
            'gearman_pid_path': 'Gearman PID file path',
            'url': 'Gearman endpoint to talk to',
            'bin': 'Path to ls command',
            'use_sudo': 'Use sudo?',
            'sudo_cmd': 'Path to sudo',
        })
        return config_help

    def get_default_config(self):
        """
        Returns the default collector settings
        """
        config = super(GearmanCollector, self).get_default_config()
        config.update({
            'path': 'gearman_stats',
            'gearman_pid_path': '/var/run/gearman/gearman-job-server.pid',
            'url': 'localhost',
            'bin': '/bin/ls',
            'use_sudo': False,
            'sudo_cmd': '/usr/bin/sudo',
        })
        return config

    def collect(self):
        """
        Collector gearman stats
        """

        def get_fds(gearman_pid_path):
            with open(gearman_pid_path) as fp:
                gearman_pid = fp.read().strip()
            proc_path = os.path.join('/proc', gearman_pid, 'fd')

            command = [self.config['bin'], proc_path]
            if str_to_bool(self.config['use_sudo']):
                command.insert(0, self.config['sudo_cmd'])

            process = subprocess.Popen(command,
                                       stdout=subprocess.PIPE,
                                       stderr=subprocess.PIPE)
            output, errors = process.communicate()
            if errors:
                raise Exception(errors)
            return len(output.splitlines())

        def publish_server_stats(gm_admin_client):
            #  Publish idle/running worker counts
            #  and no. of tasks queued per task
            for entry in gm_admin_client.get_status():
                total = entry.get('workers', 0)
                running = entry.get('running', 0)
                idle = total-running

                self.dimensions = {'task': entry['task']} # Internally, this dict is cleared on self.publish
                self.publish('gearman.queued', entry['queued'])

                self.dimensions = {'type': 'running'}
                self.publish('gearman.workers', running)

                self.dimensions = {'type': 'idle'}
                self.publish('gearman.workers', idle)

        try:
            if gearman is None:
                self.log.error("Unable to import python gearman client")
                return

            # Collect and Publish Metrics
            self.log.debug("Using pid file: %s & gearman endpoint : %s",
                    self.config['gearman_pid_path'], self.config['url'])

            gm_admin_client = gearman.GearmanAdminClient([self.config['url']])
            self.publish('gearman.ping', gm_admin_client.ping_server())
            self.publish('gearman.fds', get_fds(self.config['gearman_pid_path']))
            publish_server_stats(gm_admin_client)
        except Exception, e:
            self.log.error("GearmanCollector Error: %s", e)
