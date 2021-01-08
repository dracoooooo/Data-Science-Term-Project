import os

__goblin_version_path__ = os.path.realpath(__file__ + '/../VERSION')
__version__ = open(__goblin_version_path__, 'r').readline().strip()
