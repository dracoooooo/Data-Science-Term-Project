# coding=utf-8

import logging
import logging.config
import json
import multiprocessing
import optparse
import os
import signal
import sys
import time

sys.path = [os.path.dirname(__file__)] + sys.path

try:
    from setproctitle import getproctitle, setproctitle
except ImportError:
    setproctitle = None

# Path Fix
sys.path.append(
    os.path.abspath(
        os.path.join(
            os.path.dirname(__file__), "../")))

from diamond.utils.classes import initialize_collector
from diamond.utils.classes import load_collectors

from diamond.utils.scheduler import collector_process

from diamond.utils.signals import signal_to_exception
from diamond.utils.signals import SIGHUPException


LOG_FORMAT = '%(asctime)s - %(name)s - %(levelname)s - %(message)s'


def load_config(configfile):
    configfile_path = os.path.abspath(configfile)
    with open(configfile_path, "r") as f:
        return json.load(f)

class Server(object):
    """
    Server class loads and starts Handlers and Collectors
    """

    def __init__(self, configfile):
        # Initialize Logging
        self.log = logging.getLogger('diamond')
        # Initialize Members
        self.configfile = configfile
        self.config = None

        # We do this weird process title swap around to get the sync manager
        # title correct for ps
        if setproctitle:
            oldproctitle = getproctitle()
            setproctitle('%s - SyncManager' % getproctitle())
        if setproctitle:
            setproctitle(oldproctitle)

    def run(self):
        """
        Load handler and collector classes and then start collectors
        """

        ########################################################################
        # Config
        ########################################################################
        self.config = load_config(self.configfile)

        collectors = load_collectors(self.config['diamondCollectorsPath'])

        ########################################################################
        # Signals
        ########################################################################

        signal.signal(signal.SIGHUP, signal_to_exception)

        ########################################################################

        while True:
            try:
                active_children = multiprocessing.active_children()
                running_processes = []
                for process in active_children:
                    running_processes.append(process.name)
                running_processes = set(running_processes)

                ##############################################################
                # Collectors
                ##############################################################

                running_collectors = []
                for collector in self.config['diamondCollectors']:
                    running_collectors.append(collector)
                running_collectors = set(running_collectors)
                self.log.debug("Running collectors: %s" % running_collectors)

                # Collectors that are running but shouldn't be
                for process_name in running_processes - running_collectors:
                    if 'Collector' not in process_name:
                        continue
                    for process in active_children:
                        if process.name == process_name:
                            process.terminate()

                collector_classes = dict(
                    (cls.__name__.split('.')[-1], cls)
                    for cls in collectors.values()
                )

                for process_name in running_collectors - running_processes:
                    # To handle running multiple collectors concurrently, we
                    # split on white space and use the first word as the
                    # collector name to spin
                    collector_name = process_name.split()[0]

                    if 'Collector' not in collector_name:
                        continue

                    if collector_name not in collector_classes:
                        self.log.error('Can not find collector %s',
                                       collector_name)
                        continue

                    # Since collector names can be defined with a space in order to instantiate multiple
                    # instances of the same collector, we want their files
                    # will not have that space and needs to have it replaced with an underscore
                    # instead
                    configfile = '/'.join([
                        self.config['collectorsConfigPath'], process_name]).replace(' ', '_') + '.conf'
                    configfile = load_config(configfile)
                    collector = initialize_collector(
                        collector_classes[collector_name],
                        name=process_name,
                        config=self.config,
                        configfile=configfile,
                        handlers=[])

                    if collector is None:
                        self.log.error('Failed to load collector %s',
                                       process_name)
                        continue

                    # Splay the loads
                    time.sleep(1)

                    process = multiprocessing.Process(
                        name=process_name,
                        target=collector_process,
                        args=(collector, self.log)
                        )
                    process.daemon = True
                    process.start()

                ##############################################################

                time.sleep(1)

            except SIGHUPException:
                self.log.info('Reloading state due to HUP')
                self.config = load_config(self.configfile)
                collectors = load_collectors(
                    self.config['diamondCollectorsPath'])


def main():
    parser = optparse.OptionParser()
    parser.add_option("-c",
                      "--config-file",
                      dest="config_file",
                      help="Fullerite configuration file",
                      metavar="FILE")
    parser.add_option("-l",
                      "--log_level",
                      default='INFO',
                      choices=['INFO', 'DEBUG', 'WARN', 'CRITICAL', 'NOTSET', 'ERROR'],
                      help="Set the log level to this level")
    parser.add_option("-f",
                      "--log_config",
                      help="Configure logging with the specified file")
    (options, args) = parser.parse_args()

    logging.basicConfig(level=logging.getLevelName(options.log_level or 'INFO'),
                        format=LOG_FORMAT)
    if options.log_config:
        logging.config.fileConfig(options.log_config)

    Server(options.config_file).run()

if __name__ == "__main__":
    main()
