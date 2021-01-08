from __future__ import unicode_literals
from nose.plugins.attrib import attr

from tornado.testing import gen_test
from .base_tests import GraphPropertyBaseClassTestCase, create_key
from decimal import getcontext
from decimal import Decimal as _D
from goblin import connection
from goblin.properties.properties import Decimal
from goblin.models import Vertex
from goblin._compat import print_


@attr('unit', 'property', 'property_decimal')
class DecimalPropertyTestCase(GraphPropertyBaseClassTestCase):
    klass = Decimal
    good_cases = (1.101, 1.11, 0.001, _D((0, (1, 0, 0, 0), -3)),
                  _D((0, (1, 0, 0, ), -2)))
    bad_cases = (0, 1.2345, 'val', ['val'], {'val': 1}, '', '1.234',
                 _D((0, (1, 0, 0, 0, 1), -4)))


class DecimalTestVertex(Vertex):
    element_type = 'test_decimal_vertex'
    test_val1 = Decimal()


@attr('unit', 'property', 'property_decimal')
class DecimalVertexTestCase(GraphPropertyBaseClassTestCase):

    @gen_test
    def test_decimal_io(self):
        key = DecimalTestVertex.get_property_by_name('test_val1')
        yield create_key(key, 'Float')
        dt1 = yield DecimalTestVertex.create(test_val1=_D((0, (5, 0, 0, 0), -3)))
        dt3 = yield DecimalTestVertex.create(test_val1=-1.001)
        try:
            dt2 = yield DecimalTestVertex.get(dt1._id)
            self.assertEqual(dt1.test_val1, dt2.test_val1)
            dt4 = yield DecimalTestVertex.get(dt3._id)
            self.assertEqual(dt4.test_val1, _D((1, (1, 0, 0, 1), -3)))
        finally:
            yield dt1.delete()
            yield dt3.delete()
