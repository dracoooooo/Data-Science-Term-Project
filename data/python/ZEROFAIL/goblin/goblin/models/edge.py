import logging

from goblin import connection
from goblin.constants import EDGE_TRAVERSAL
from goblin._compat import (
    array_types, integer_types, float_types, string_types, add_metaclass)
from goblin.exceptions import (
    ElementDefinitionException, GoblinQueryError, ValidationError)
from goblin.gremlin import GremlinMethod
from .element import Element, ElementMetaClass, edge_types
from .query import V


logger = logging.getLogger(__name__)


class EdgeMetaClass(ElementMetaClass):
    """Metaclass for edges."""

    def __new__(mcs, name, bases, body):
        # short circuit element_type inheritance
        body['_label'] = body.pop('_label', None)

        klass = super(EdgeMetaClass, mcs).__new__(mcs, name, bases, body)

        if not klass.__abstract__:
            label = klass.get_label()
            if label in edge_types and str(edge_types[label]) != str(klass):
                # Catch imports from other modules, don't reload module into
                # vertex types, only load once
                logger.debug(
                    ElementDefinitionException(
                        """%s is already registered as a edge:
                           mcs: %s
                           name: %s
                           bases: %s
                           body: %s""" % (label, mcs, name, bases, body)))
            else:
                edge_types[label] = klass
        return klass


@add_metaclass(EdgeMetaClass)
class Edge(Element):
    """Base class for all edges."""

    # __metaclass__ = EdgeMetaClass
    __abstract__ = True

    # if set to True, no more than one edge will
    # be created between two vertices
    __exclusive__ = False

    _label = None

    gremlin_path = 'edge.groovy'

    _save_edge = GremlinMethod()
    _delete_edge = GremlinMethod()
    _get_edges_between = GremlinMethod(classmethod=True)
    _find_edge_by_value = GremlinMethod(classmethod=True)

    FACTORY_CLASS = None
    # edge id
    # edge_id = columns.UUID(save_strategy=columns.SAVE_ONCE)

    def __init__(self, outV, inV, **values):
        """
        Initialize this edge with the outgoing and incoming vertices as well as
        edge properties.

        :param outV: The vertex this edge is coming out of
        :type outV: Vertex
        :param inV: The vertex this edge is going into
        :type inV: Vertex
        :param values: The properties for this edge
        :type values: dict

        """
        self._outV = outV
        self._inV = inV
        super(Edge, self).__init__(**values)

    def __repr__(self):
        return "{}(label={}, id={}, values={}, manual_values={})".format(
            self.__class__.__name__, self.__class__.get_label(),
            getattr(self, '_id', None), getattr(self, '_values', {}),
            getattr(self, '_manual_values', {}))

    def __getstate__(self):
        state = {u'_id': self.id,
                 u'_type': u'edge',
                 u'_outV': str(self.outV().id),
                 u'_inV': str(self.inV().id),
                 u'_label': self.get_label(),
                 u'_properties': self.as_save_params()}
        return state

    def __setstate__(self, state):
        data = self.translate_db_fields(state)
        self.__init__(state['_outV'], state['_inV'], **data)
        return self

    @classmethod
    def find_by_value(cls, field, value, as_dict=False, **kwargs):
        """
        Returns edges that match the given field/value pair.

        :param field: The field to search
        :type field: str
        :param value: The value of the field
        :type value: str
        :param as_dict: Return results as a dictionary
        :type as_dict: boolean
        :rtype: [goblin.models.Edge]
        """
        _field = cls.get_property_by_name(field)
        _label = cls.get_label()

        value_type = False
        if isinstance(value, integer_types + float_types):
            value_type = True

        future = connection.get_future(kwargs)
        future_results = cls._find_edge_by_value(
            value_type=value_type,
            elabel=_label,
            field=_field,
            val=value
        )

        def by_value_handler(data):
            if data is None:
                data = []
            if as_dict:  # pragma: no cover
                data = {v._id: v for v in data}
            return data

        def on_find_by_value(f):
            try:
                stream = f.result()
            except Exception as e:
                future.set_exception(e)
            else:
                stream.add_handler(by_value_handler)
                future.set_result(stream)

        future_results.add_done_callback(on_find_by_value)

        return future

    @classmethod
    def all(cls, ids=None, as_dict=False, *args, **kwargs):
        return super(Edge, cls).all(
            EDGE_TRAVERSAL, ids=ids, as_dict=as_dict, *args, **kwargs)

    @classmethod
    def get_label(cls):
        """
        Returns the label for this edge.

        :rtype: str

        """
        return cls._type_name(cls._label)

    @classmethod
    def get_between(cls, outV, inV, page_num=None, per_page=None):
        """
        Return all the edges with a given label between two vertices.

        :param outV: The vertex the edge comes out of.
        :type outV: Vertex
        :param inV: The vertex the edge goes into.
        :type inV: Vertex
        :param page_num: The page number of the results
        :type page_num: int
        :param per_page: The number of results per page
        :type per_page: int
        :rtype: list

        """
        return cls._get_edges_between(out_v=outV,
                                      in_v=inV,
                                      elabel=cls.get_label(),
                                      page_num=page_num,
                                      per_page=per_page)

    def validate(self):
        """
        Perform validation of this edge raising a ValidationError if any
        problems are encountered.
        """
        if self._id is None:
            if self._inV is None:
                raise ValidationError(
                    'in vertex must be set before saving new edges')
            if self._outV is None:
                raise ValidationError(
                    'out vertex must be set before saving new edges')
        super(Edge, self).validate()

    def save(self, *args, **kwargs):
        """
        Save this edge to the graph database.
        """
        super(Edge, self).save()
        future = connection.get_future(kwargs)
        future_result = self._save_edge(self._outV,
                                        self._inV,
                                        self.get_label(),
                                        self.as_save_params(),
                                        exclusive=self.__exclusive__,
                                        **kwargs)

        def on_read(f2):
            try:
                result = f2.result()[0]
            except Exception as e:
                future.set_exception(e)
            else:
                future.set_result(result)

        def on_save(f):
            try:
                stream = f.result()
            except Exception as e:
                future.set_exception(e)
            else:
                future_read = stream.read()
                future_read.add_done_callback(on_read)

        future_result.add_done_callback(on_save)

        return future

    def _reload_values(self, *args, **kwargs):
        """ Re-read the values for this edge from the graph database. """
        reloaded_values = {}
        future = connection.get_future(kwargs)
        future_result = connection.execute_query(
            'g.E(eid)', {'eid': self._id}, **kwargs)

        def on_read(f2):
            try:
                result = f2.result()
                result = result.data[0]
            except Exception as e:
                future.set_exception(e)
            else:
                if result:
                    # del result['type']
                    reloaded_values['id'] = result['id']
                    for name, value in result.get('properties', {}).items():
                        reloaded_values[name] = value
                    if result['id']:
                        setattr(self, 'id', result['id'])
                    future.set_result(reloaded_values)
                else:
                    future.set_result({})

        def on_reload(f):
            try:
                stream = f.result()
            except Exception as e:
                future.set_exception(e)
            else:
                future_read = stream.read()
                future_read.add_done_callback(on_read)

        future_result.add_done_callback(on_reload)

        return future

    @classmethod
    def create(cls, outV, inV, label=None, *args, **kwargs):
        """
        Create a new edge of the current type coming out of vertex outV and
        going into vertex inV with the given properties.

        :param outV: The vertex the edge is coming out of
        :type outV: Vertex
        :param inV: The vertex the edge is going into
        :type inV: Vertex

        """
        edge = super(Edge, cls).create(outV, inV, *args, **kwargs)
        return edge

    def delete(self, **kwargs):
        """
        Delete the current edge from the graph.
        """
        if self.__abstract__:  # pragma: no cover
            raise GoblinQueryError('cant delete abstract elements')
        if self._id is None:
            return self

        future = connection.get_future(kwargs)
        future_result = self._delete_edge()

        def on_read(f2):
            try:
                result = f2.result()
            except Exception as e:
                future.set_exception(e)
            else:
                future.set_result(result)

        def on_delete(f):
            try:
                stream = f.result()
            except Exception as e:
                future.set_exception(e)
            else:
                future_read = stream.read()
                future_read.add_done_callback(on_read)

        future_result.add_done_callback(on_delete)

        return future

    def _simple_traversal(self, operation, *args, **kwargs):
        """
        Perform a simple traversal starting from the current edge returning a
        list of results.

        :param operation: The operation to be performed
        :type operation: str
        :rtype: list

        """
        deserialize = kwargs.pop('deserialize', True)

        def edge_traversal_handler(data):
            if deserialize:
                data = [Element.deserialize(d) for d in data]
            return data

        future_results = connection.execute_query(
            'g.E(id).%s()' % operation, {'id': self.id},
            handler=edge_traversal_handler, **kwargs)

        return future_results

    def inV(self, *args, **kwargs):
        """
        Return the vertex that this edge goes into.

        :rtype: Vertex

        """
        from goblin.models.vertex import Vertex
        future = connection.get_future(kwargs)
        if self._inV is None:
            future_results = self._simple_traversal('inV', **kwargs)

            def on_traversal(f):
                try:
                    result = f.result()
                except Exception as e:
                    future.set_exception(e)
                else:
                    self._inV = result[0]
                    if isinstance(self._inV, string_types + integer_types):
                        future_results = V(self._inV).get(**kwargs)

                        def on_get(f2):
                            try:
                                result = f2.result()
                            except Exception as e:
                                future.set_exception(e)
                            else:
                                self._inV = result

                        future_results.add_done_callback(on_get)
                    future.set_result(self._inV)
                future_results.add_done_callback(on_traversal)

        elif isinstance(self._inV, string_types + integer_types):
            future_results = V(self._inV).get(**kwargs)

            def on_get(f2):
                try:
                    result = f2.result()
                except Exception as e:
                    future.set_exception(e)
                else:
                    self._inV = result
                    future.set_result(self._inV)

            future_results.add_done_callback(on_get)
        else:
            future.set_result(self._inV)
        return future

    def outV(self, *args, **kwargs):
        """
        Return the vertex that this edge goes into.

        :rtype: Vertex

        """
        from goblin.models.vertex import Vertex
        future = connection.get_future(kwargs)
        if self._inV is None:
            future_results = self._simple_traversal('outV', **kwargs)

            def on_traversal(f):
                try:
                    result = f.result()
                except Exception as e:
                    future.set_exception(e)
                else:
                    self._outV = result[0]
                    if isinstance(self._outV, string_types + integer_types):
                        future_results = V(self._outV).get(**kwargs)

                        def on_get(f2):
                            try:
                                result = f2.result()
                            except Exception as e:
                                future.set_exception(e)
                            else:
                                self._outV = result

                        future_results.add_done_callback(on_get)
                    future.set_result(self._outV)
                future_results.add_done_callback(on_traversal)

        elif isinstance(self._outV, string_types + integer_types):
            future_results = V(self._outV).get(**kwargs)

            def on_get(f2):
                try:
                    result = f2.result()
                except Exception as e:
                    future.set_exception(e)
                else:
                    self._outV = result
                    future.set_result(self._outV)

            future_results.add_done_callback(on_get)
        else:
            future.set_result(self._outV)
        return future
