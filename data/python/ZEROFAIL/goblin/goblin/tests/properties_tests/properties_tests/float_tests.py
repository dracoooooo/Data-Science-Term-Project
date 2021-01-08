from __future__ import unicode_literals
from nose.plugins.attrib import attr

from tornado.testing import gen_test

from .base_tests import GraphPropertyBaseClassTestCase, create_key
from goblin import connection
from goblin.properties.properties import Float, Double
from goblin.models import Vertex
from goblin._compat import print_


@attr('unit', 'property', 'property_float')
class FloatPropertyTestCase(GraphPropertyBaseClassTestCase):
    klass = Float
    good_cases = (1.1, 0.0, None)
    bad_cases = ('', 'a', 1, [], [1], {}, {'a': 1})

    @gen_test
    def test_manual_schema(self):
        key = FloatTestVertex.get_property_by_name('test_val')
        label = FloatTestVertex.get_label()
        yield create_key(key, 'Float')
        stream = yield connection.execute_query(
            "graph.addVertex(label, l0, k0, v0)",
            bindings={'l0': label, 'k0': key, 'v0': 1.23})
        resp = yield stream.read()
        self.assertEqual(
            resp.data[0]['properties'][key][0]['value'], 1.23)

    @gen_test
    def test_violate_manual_schema(self):
        key = FloatTestVertex.get_property_by_name('test_val')
        label = FloatTestVertex.get_label()
        yield create_key(key, 'Float')
        with self.assertRaises(RuntimeError):
            stream = yield connection.execute_query(
                    "graph.addVertex(label, l0, k0, v0)",
                    bindings={'l0': label, 'k0': key, 'v0': 'decimal'})
            resp = yield stream.read()
            print(resp)

    @gen_test
    def test_manual_schema_double(self):
        key = DoubleTestVertex.get_property_by_name('test_val')
        label = DoubleTestVertex.get_label()
        yield create_key(key, 'Double')
        stream = yield connection.execute_query(
            "graph.addVertex(label, l0, k0, v0)",
            bindings={'l0': label, 'k0': key, 'v0': 1.23})
        resp = yield stream.read()
        self.assertEqual(
            resp.data[0]['properties'][key][0]['value'], 1.23)

    @gen_test
    def test_violate_manual_schema_double(self):
        key = DoubleTestVertex.get_property_by_name('test_val')
        label = DoubleTestVertex.get_label()
        yield create_key(key, 'Double')
        with self.assertRaises(RuntimeError):
            stream = yield connection.execute_query(
                    "graph.addVertex(label, l0, k0, v0)",
                    bindings={'l0': label, 'k0': key, 'v0': 'somestring'})
            resp = yield stream.read()
            print(resp)


class FloatTestVertex(Vertex):
    element_type = 'test_float_vertex'

    test_val = Float()


@attr('unit', 'property', 'property_float')
class FloatVertexTestCase(GraphPropertyBaseClassTestCase):

    @gen_test
    def test_float_io(self):
        print_("creating vertex")
        key = FloatTestVertex.get_property_by_name('test_val')
        yield create_key(key, 'Float')
        dt = yield FloatTestVertex.create(test_val=1.1)
        print_("getting vertex from vertex: %s" % dt)
        dt2 = yield FloatTestVertex.get(dt._id)
        print_("got vertex: %s\n" % dt2)
        self.assertEqual(dt2.test_val, dt.test_val)
        print_("deleting vertex")
        yield dt2.delete()

        dt = yield FloatTestVertex.create(test_val=2.2)
        print_("\ncreated vertex: %s" % dt)
        dt2 = yield FloatTestVertex.get(dt._id)
        print_("Got vertex: %s" % dt2)
        self.assertEqual(dt2.test_val, 2.2)
        print_("deleting vertex")
        yield dt2.delete()


@attr('unit', 'property', 'property_double')
class DoublePropertyTestCase(FloatPropertyTestCase):
    klass = Double


class DoubleTestVertex(Vertex):
    element_type = 'test_double_vertex'

    test_val = Double()


@attr('unit', 'property', 'property_double')
class DoubleVertexTestCase(GraphPropertyBaseClassTestCase):

    @gen_test
    def test_double_io(self):
        print_("creating vertex")
        key = DoubleTestVertex.get_property_by_name('test_val')
        yield create_key(key, 'Double')
        dt = yield DoubleTestVertex.create(test_val=1.1)
        print_("getting vertex from vertex: %s" % dt)
        dt2 = yield DoubleTestVertex.get(dt._id)
        print_("got vertex: %s\n" % dt2)
        self.assertEqual(dt2.test_val, dt.test_val)
        print_("deleting vertex")
        yield dt2.delete()

        dt = yield DoubleTestVertex.create(test_val=2.2)
        print_("\ncreated vertex: %s" % dt)
        dt2 = yield DoubleTestVertex.get(dt._id)
        print_("Got vertex: %s" % dt2)
        self.assertEqual(dt2.test_val, 2.2)
        print_("deleting vertex")
        yield dt2.delete()
