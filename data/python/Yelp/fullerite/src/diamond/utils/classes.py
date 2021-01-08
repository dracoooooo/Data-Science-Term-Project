# coding=utf-8

import os
import sys
import logging
import inspect
import traceback

from diamond.util import load_class_from_name
from diamond.collector import Collector


def load_include_path(paths):
    """
    Scan for and add paths to the include path
    """
    for path in paths:
        # Verify the path is valid
        if not os.path.isdir(path):
            continue
        # Add path to the system path, to avoid name clashes
        # with mysql-connector for example ...
        if path not in sys.path:
            sys.path.insert(1, path)
        # Load all the files in path
        for f in os.listdir(path):
            # Are we a directory? If so process down the tree
            fpath = os.path.join(path, f)
            if os.path.isdir(fpath):
                load_include_path([fpath])


def load_dynamic_class(fqn, subclass):
    """
    Dynamically load fqn class and verify it's a subclass of subclass
    """
    if not isinstance(fqn, basestring):
        return fqn

    cls = load_class_from_name(fqn)

    if cls == subclass or not issubclass(cls, subclass):
        raise TypeError("%s is not a valid %s" % (fqn, subclass.__name__))

    return cls


def load_collectors(paths=None, filter=None):
    """
    Scan for collectors to load from path
    """
    # Initialize return value
    collectors = {}
    log = logging.getLogger('diamond')

    if paths is None:
        return

    if isinstance(paths, basestring):
        paths = map(str, paths.split(','))
        print paths
        paths = map(str.strip, paths)

    load_include_path(paths)

    for path in paths:
        # Get a list of files in the directory, if the directory exists
        if not os.path.exists(path):
            raise OSError("Directory does not exist: %s" % path)

        if path.endswith('tests') or path.endswith('fixtures'):
            return collectors

        # Load all the files in path
        for f in os.listdir(path):

            # Are we a directory? If so process down the tree
            fpath = os.path.join(path, f)
            if os.path.isdir(fpath):
                subcollectors = load_collectors([fpath])
                for key in subcollectors:
                    collectors[key] = subcollectors[key]

            # Ignore anything that isn't a .py file
            elif (os.path.isfile(fpath)
                  and len(f) > 3
                  and f[-3:] == '.py'
                  and f[0:4] != 'test'
                  and f[0] != '.'):

                # Check filter
                if filter and os.path.join(path, f) != filter:
                    continue

                modname = f[:-3]

                try:
                    # Import the module
                    mod = __import__(modname, globals(), locals(), ['*'])
                except (KeyboardInterrupt, SystemExit), err:
                    log.error(
                        "System or keyboard interrupt "
                        "while loading module %s"
                        % modname)
                    if isinstance(err, SystemExit):
                        sys.exit(err.code)
                    raise KeyboardInterrupt
                except:
                    # Log error
                    log.error("Failed to import module: %s. %s",
                              modname,
                              traceback.format_exc())
                    continue

                # Find all classes defined in the module
                for attrname in dir(mod):
                    attr = getattr(mod, attrname)
                    # Only attempt to load classes that are infact classes
                    # are Collectors but are not the base Collector class
                    if (inspect.isclass(attr)
                            and issubclass(attr, Collector)
                            and attr != Collector):
                        if attrname.startswith('parent_'):
                            continue
                        # Get class name
                        fqcn = '.'.join([modname, attrname])
                        try:
                            # Load Collector class
                            cls = load_dynamic_class(fqcn, Collector)
                            # Add Collector class
                            collectors[cls.__name__] = cls
                        except Exception:
                            # Log error
                            log.error(
                                "Failed to load Collector: %s. %s",
                                fqcn, traceback.format_exc())
                            continue

    # Return Collector classes
    return collectors


def initialize_collector(cls, name=None, config=None, handlers=[], configfile=None):
    """
    Initialize collector
    """
    log = logging.getLogger('diamond')
    collector = None

    try:
        # Initialize Collector
        collector = cls(name=name, config=config, handlers=handlers, configfile=configfile)
    except Exception:
        # Log error
        log.error("Failed to initialize Collector: %s. %s",
                  cls.__name__, traceback.format_exc())

    # Return collector
    return collector
