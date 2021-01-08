from __future__ import unicode_literals
import unittest
from tornado import concurrent
from gremlinclient import tornado_client
from goblin import connection
from goblin import constants


class TestSetup(unittest.TestCase):

    def test_default_setup_teardown(self):
        connection.setup("ws://localhost:8182/")
        self.assertEqual(connection._future, concurrent.Future)
        self.assertIsInstance(connection._connection_pool, tornado_client.Pool)
        self.assertEqual(connection._graph_name, "graph")
        self.assertEqual(connection._traversal_source, "g")
        self.assertEqual(connection._scheme, "ws")
        self.assertEqual(connection._netloc, "localhost:8182")
        self.assertEqual(connection._client_module,
                         constants.TORNADO_CLIENT_MODULE)
        connection.tear_down()
