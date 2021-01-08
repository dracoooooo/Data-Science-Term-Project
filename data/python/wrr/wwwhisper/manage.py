#!/usr/bin/env python
#
# This script requires path to a directory with site-specific
# settings. It should be run like this:
# python manage.py test --pythonpath=../sites/SCHEME.DOMAIN.PORT/django \
#    wwwhisper_auth wwwhisper_admin
#
# For convenience during development, development version of
# site_settings.py can be put in wwwhisper_service directory.

import os
import sys

if __name__ == "__main__":
    os.environ.setdefault("DJANGO_SETTINGS_MODULE",
                          "wwwhisper_service.settings")

    from django.core.management import execute_from_command_line

    execute_from_command_line(sys.argv)
