from __future__ import unicode_literals
import logging

from goblin._compat import float_types, print_, integer_types, string_types
from goblin import connection
from goblin.exceptions import GoblinQueryError
from .element import Element
from goblin.constants import (EQUAL, NOT_EQUAL, GREATER_THAN,
                              GREATER_THAN_EQUAL, LESS_THAN,
                              LESS_THAN_EQUAL, WITHIN, INSIDE,
                              OUTSIDE, BETWEEN)
import copy
from goblin.properties.base import GraphProperty

logger = logging.getLogger(__name__)


class V(object):
    """
    All query operations return a new query object, which currently deviates
    from blueprints. The blueprints query object modifies and returns the same
    object This method seems more flexible, and consistent w/ the rest of
    Gremlin.
    """
    _limit = None

    def __init__(self, vertex):
        self._vertex = vertex
        self._steps = []
        self._bindings = {}

    def count(self, *args, **kwargs):
        """
        :returns: number of matching vertices
        :rtype: int
        """
        pass

    def has(self, key, value, compare=EQUAL):
        """
        :param key: key to lookup
        :type key: str | goblin.properties.GraphProperty
        :param value: value to compare
        :type value: str, float, int
        :param compare: comparison keyword
        :type compare: str
        :rtype: Query
        """
        q = copy.copy(self)
        if issubclass(type(key), property):
            msg = "Use %s.get_property_by_name" % (self.__class__.__name__)
            logger.error(msg)
            raise GoblinQueryError(msg)
        binding = self._get_binding(value)
        if compare in [INSIDE, OUTSIDE, BETWEEN, WITHIN]:
            step = "has('{}', {}(*{}))".format(key, compare, binding)
        else:
            step = "has('{}', {}({}))".format(key, compare, binding)
        q._steps.append(step)
        return q

    def has_label(self, *labels):
        labels = self._get_labels(labels)
        return self._unpack_step("hasLabel", labels)

    def has_id(self, *ids):
        return self._unpack_step("hasId", ids)

    # def has_key(self, *keys):
    #     return self._unpack_step("hasKey", keys)

    # def has_value(self, *values):
    #     return self._unpack_step("hasValue", values)

    def out_step(self, *labels):
        labels = self._get_labels(labels)
        return self._unpack_step("out", labels)

    def in_step(self, *labels):
        labels = self._get_labels(labels)
        return self._unpack_step("in", labels)

    def both(self, *labels):
        labels = self._get_labels(labels)
        return self._unpack_step("both", labels)

    def out_e(self, *labels):
        labels = self._get_labels(labels)
        return self._unpack_step("outE", labels)

    def in_e(self, *labels):
        labels = self._get_labels(labels)
        return self._unpack_step("inE", labels)

    def both_e(self, *labels):
        labels = self._get_labels(labels)
        return self._unpack_step("bothE", labels)

    def out_v(self):
        return self._simple_step("outV")

    def in_v(self):
        return self._simple_step("inV")

    def both_v(self):
        return self._simple_step("bothV")

    def other_v(self):
        return self._simple_step("otherV")

    def _get_labels(self, labels):
        new_labels = []
        for label in labels:
            try:
                label = label.get_label()
                new_labels.append(label)
            except:
                new_labels.append(label)
        return new_labels

    def _simple_step(self, func):
        q = copy.copy(self)
        step = '{}()'.format(func)
        q._steps.append(step)
        return q

    def _unpack_step(self, func, vals):
        q = copy.copy(self)
        binding = self._get_binding(vals)
        step = '{}(*{})'.format(func, binding)
        q._steps.append(step)
        return q

    def _get_binding(self, val):
        binding = 'b{}'.format(len(self._bindings))
        self._bindings[binding] = val
        return binding

    def limit(self, limit):
        pass

    def get(self, deserialize=True, *args, **kwargs):

        if isinstance(self._vertex, string_types + integer_types):
            vid = self._vertex
        else:
            vid = self._vertex._id
        self._bindings.update({"vid": vid})

        steps = self._get()
        script = "g.V(vid){}".format(steps)
        if steps:
            future_results = self._get_stream(script, deserialize)
        else:
            future_results = self._get_simple(deserialize)

        return future_results

    def _get_stream(self, script, deserialize, **kwargs):

        def process_results(results):
            if not results:
                results = []
            if deserialize:
                results = [Element.deserialize(r) for r in results]
            return results

        future_results = connection.execute_query(
            script, bindings=self._bindings, handler=process_results,
            **kwargs)
        return future_results

    def _get_simple(self, deserialize, **kwargs):
        script = "g.V(vid)"
        future_results = self._get_stream(script, deserialize, **kwargs)
        future = connection.get_future(kwargs)

        def on_read(f):
            try:
                result = f.result()
            except Exception as e:
                future.set_exception(e)
            else:
                if not result:
                    future.set_exception(GoblinQueryError("Does not exist"))
                future.set_result(result[0])

        def on_stream(f2):
            try:
                stream = f2.result()
            except Exception as e:
                future.set_exception(e)
            else:
                future_read = stream.read()
                future_read.add_done_callback(on_read)

        future_results.add_done_callback(on_stream)
        return future

    def _get(self):
        output = ''
        if self._steps:
            output = '.{}'.format('.'.join(self._steps))
        return output
