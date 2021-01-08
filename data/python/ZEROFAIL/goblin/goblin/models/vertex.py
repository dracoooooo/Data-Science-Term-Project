from __future__ import unicode_literals
import inspect
import logging

from goblin import connection
from goblin.constants import VERTEX_TRAVERSAL
from goblin._compat import (
    array_types, string_types, add_metaclass, integer_types, float_types)
from goblin.exceptions import (
    GoblinException, ElementDefinitionException, GoblinQueryError)
from goblin.gremlin import GremlinMethod
from .element import Element, ElementMetaClass, vertex_types


logger = logging.getLogger(__name__)


class VertexMetaClass(ElementMetaClass):
    """Metaclass for vertices."""

    def __new__(mcs, name, bases, body):
        # short circuit element_type inheritance
        body['_label'] = body.pop('_label', None)

        klass = super(VertexMetaClass, mcs).__new__(mcs, name, bases, body)

        if not klass.__abstract__:
            label = klass.get_label()
            body['_label'] = label
            if (label in vertex_types and
                    str(vertex_types[label]) != str(klass)):
                logger.debug(ElementDefinitionException(
                    """%s is already registered as a vertex:
                       mcs: %s
                       name: %s
                       bases: %s
                       body: %s""" % (label, mcs, name, bases, body)))
            else:
                vertex_types[label] = klass

            # index requested indexed columns
            # klass._create_indices()

        return klass


class EnumVertexBaseMeta(VertexMetaClass):
    """
    This metaclass allows you to access MyVertexModel as if it were an enum.
    Ex. MyVertexModel.FOO

    The values are cached in a dictionary. This is useful if the number of
    MyVertexModels is small, however it it grows too large, you should be
    doing it a different way.

    This looks for a special (optional) function named `enum_generator` in your
    model and calls that to generate the ENUM for the model.

    There is an additional optional model attribute that can be set
    `__enum_id_only__` (defaults to True) which dictates whether or not just
    the Vertex ID is stored, or the whole Vertex in cache.
    """

    enums = None

    def __getattr__(cls, key):
        # property name to use for keying for the enum
        # method for handling name mangling, default to passthrough mode which
        # subs spaces for underscores and caps
        store_model = getattr(cls, '__enum_id_only__', True)

        def get_enum_keyword(enum):
            return getattr(
                enum, 'enum_generator', lambda: (
                    getattr(enum, 'name', '').replace(' ', '_').upper()))()

        if key.isupper():
            if cls.enums is None:
                cls.enums = dict(
                    [(get_enum_keyword(enum), enum._id if
                     store_model else enum) for enum in cls.all()]
                )
            id = cls.enums.get(key, None)
            if not id:
                # make one attempt to load any new models
                cls.enums = dict(
                    [(get_enum_keyword(enum), enum._id if
                     store_model else enum) for enum in cls.all()]
                )
                id = cls.enums.get(key, None)
                if not id:
                    raise AttributeError(key)
            return id
        else:
            return super(EnumVertexBaseMeta, cls).__getattr__(key)


@add_metaclass(VertexMetaClass)
class Vertex(Element):
    """ The Vertex model base class.

    The element type is auto-generated from the subclass name, but can
    optionally be set manually
    """
    # __metaclass__ = VertexMetaClass
    __abstract__ = True

    gremlin_path = 'vertex.groovy'

    _save_vertex = GremlinMethod()
    _delete_vertex = GremlinMethod()
    _traversal = GremlinMethod()
    _delete_related = GremlinMethod()
    _find_vertex_by_value = GremlinMethod(classmethod=True)

    _label = None

    FACTORY_CLASS = None

    def __repr__(self):
        return "{}(label={}, id={}, values={}, manual_values={})".format(
            self.__class__.__name__, self.get_label(),
            getattr(self, '_id', None), getattr(self, '_values', {}),
            getattr(self, '_manual_values', {}))

    def __getstate__(self):
        state = {'id': self.id, '_type': 'vertex'}
        properties = self.as_save_params()
        properties['label'] = self.get_label()
        state['properties'] = properties
        return state

    def __setstate__(self, state):
        self.__init__(**self.translate_db_fields(state))
        return self

    @classmethod
    def find_by_value(cls, field, value, as_dict=False):
        """
        Returns vertices that match the given field/value pair.
        ####REVIEW####
        :param field: The field to search
        :type field: str
        :param value: The value of the field
        :type value: str
        :param as_dict: Return results as a dictionary
        :type as_dict: boolean
        :rtype: [goblin.models.Vertex]
        """
        _field = cls.get_property_by_name(field)
        _label = cls.get_label()

        value_type = False
        if isinstance(value, integer_types + float_types):
            value_type = True
        results = cls._find_vertex_by_value(
            value_type=value_type,
            vlabel=_label,
            field=_field,
            val=value
        )

        if as_dict:  # pragma: no cover
            return {v._id: v for v in results}

        return results

    @classmethod
    def get_label(cls):
        """
        Returns the element type for this vertex.

        @returns: str

        """
        return cls._type_name(cls._label)

    def _reload_values(self, *args, **kwargs):
        """
        Method for reloading the current vertex by reading its current values
        from the database.

        """
        reloaded_values = {}
        future = connection.get_future(kwargs)
        future_result = connection.execute_query(
            'g.V(vid)', {'vid': self._id}, **kwargs)

        def on_read(f2):
            try:
                result = f2.result()
                result = result.data[0]
            except Exception as e:
                future.set_exception(e)
            else:
                # del result['type']  # don't think I need this
                reloaded_values['id'] = result['id']
                for name, value in result.get('properties', {}).items():
                    # This is a hack until decide how to deal with props
                    reloaded_values[name] = value[0]['value']
                future.set_result(reloaded_values)

        def on_reload_values(f):
            try:
                stream = f.result()
            except Exception as e:
                future.set_exception(e)
            else:
                future_read = stream.read()
                future_read.add_done_callback(on_read)
        future_result.add_done_callback(on_reload_values)
        return future

    def save(self, *args, **kwargs):
        """
        Save the current vertex using the configured save strategy, the default
        save strategy is to re-save all fields every time the object is saved.
        """
        super(Vertex, self).save()
        params = self.as_save_params()
        label = self.get_label()
        # params['element_type'] = self.get_element_type()  don't think we need
        # Here this is a future, have to set handler in callback
        future = connection.get_future(kwargs)
        future_result = self._save_vertex(label, params, **kwargs)
        deserialize = kwargs.pop('deserialize', True)
        def on_read(f2):
            try:
                result = f2.result()
            except Exception as e:
                future.set_exception(e)
            else:
                if deserialize:
                    result = result[0]
                    self._id = result._id
                    for k, v in self._values.items():
                        v.previous_value = result._values[k].previous_value
                else:
                    result = result.data
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

    def delete(self, **kwargs):
        """ Delete the current vertex from the graph. """
        if self.__abstract__:
            raise GoblinQueryError('Cant delete abstract elements')
        if self._id is None:  # pragma: no cover
            return self
        future = connection.get_future(kwargs)
        future_result = self._delete_vertex()

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

    @classmethod
    def all(cls, ids=None, as_dict=False, *args, **kwargs):
        return super(Vertex, cls).all(
            VERTEX_TRAVERSAL, ids=ids, as_dict=as_dict, *args, **kwargs)

    # This section of the API is under review
    def _simple_traversal(self,
                          operation,
                          labels,
                          limit=None,
                          offset=None,
                          types=None,
                          **kwargs):
        """
        Perform simple graph database traversals with ubiquitous pagination.

        :param operation: The operation to be performed
        :type operation: str
        :param labels: The edge labels to be used
        :type labels: list of Edges or strings
        :param start: The starting offset
        :type start: int
        :param max_results: The maximum number of results to return
        :type max_results: int
        :param types: The list of allowed result elements
        :type types: list

        """
        from goblin.models.edge import Edge
        label_strings = []
        for label in labels:
            if inspect.isclass(label) and issubclass(label, Edge):
                label_string = label.get_label()
            elif isinstance(label, Edge):
                label_string = label.get_label()
            elif isinstance(label, string_types):
                label_string = label
            else:
                raise GoblinException("traversal labels must be edge " +
                                      "classes, instances, or strings")
            label_strings.append(label_string)

        allowed_elts = None
        if types is not None:
            allowed_elts = []
            for e in types:
                if issubclass(e, Vertex):
                    allowed_elts += [e.get_label()]
                elif issubclass(e, Edge):
                    allowed_elts += [e.get_label()]

        if limit is not None and offset is not None:
            start = offset
            end = offset + limit
        else:
            start = end = None
        future = connection.get_future(kwargs)
        future_result = self._traversal(operation,
                                        label_strings,
                                        start,
                                        end,
                                        allowed_elts)

        def traversal_handler(data):
            if data is None:
                data = []
            return data

        def on_traversal(f):
            try:
                stream = f.result()
            except Exception as e:
                future.set_exception(e)
            else:
                stream.add_handler(traversal_handler)
                future.set_result(stream)

        future_result.add_done_callback(on_traversal)
        return future

    def _simple_deletion(self, operation, labels, **kwargs):
        """
        Perform simple bulk graph deletion operation.

        :param operation: The operation to be performed
        :type operation: str
        :param labels: The edge label to be used
        :type labels: str or Edge

        """
        from goblin.models.edge import Edge

        label_strings = []
        for label in labels:
            if inspect.isclass(label) and issubclass(label, Edge):
                label_string = label.get_label()
            elif isinstance(label, Edge):
                label_string = label.get_label()
            elif isinstance(label, string_types):
                label_string = label
            else:
                raise GoblinException("traversal labels must be edge " +
                                      "classes, instances, or strings")
            label_strings.append(label_string)

        future = connection.get_future(kwargs)
        future_result = self._delete_related(operation, label_strings)

        def on_read(f2):
            try:
                result = f2.result()
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

    def outV(self, *labels, **kwargs):
        """
        Return a list of vertices reached by traversing the outgoing edge with
        the given label.

        :param labels: pass in the labels to follow in as positional arguments
        :type labels: str or BaseEdge
        :param limit: The number of the page to start returning results at
        :type limit: int or None
        :param offset: The maximum number of results to return
        :type offset: int or None
        :param types: A list of allowed element types
        :type types: list

        """
        return self._simple_traversal('outV', labels, **kwargs)

    def inV(self, *labels, **kwargs):
        """
        Return a list of vertices reached by traversing the incoming edge with
        the given label.

        :param label: The edge label to be traversed
        :type label: str or BaseEdge
        :param limit: The number of the page to start returning results at
        :type limit: int or None
        :param offset: The maximum number of results to return
        :type offset: int or None
        :param types: A list of allowed element types
        :type types: list

        """
        return self._simple_traversal('inV', labels, **kwargs)

    def outE(self, *labels, **kwargs):
        """
        Return a list of edges with the given label going out of this vertex.

        :param label: The edge label to be traversed
        :type label: str or BaseEdge
        :param limit: The number of the page to start returning results at
        :type limit: int or None
        :param offset: The maximum number of results to return
        :type offset: int or None
        :param types: A list of allowed element types
        :type types: list

        """
        return self._simple_traversal('outE', labels, **kwargs)

    def inE(self, *labels, **kwargs):
        """
        Return a list of edges with the given label coming into this vertex.

        :param label: The edge label to be traversed
        :type label: str or BaseEdge
        :param limit: The number of the page to start returning results at
        :type limit: int or None
        :param offset: The maximum number of results to return
        :type offset: int or None
        :param types: A list of allowed element types
        :type types: list

        """
        return self._simple_traversal('inE', labels, **kwargs)

    def bothE(self, *labels, **kwargs):
        """
        Return a list of edges both incoming and outgoing from this vertex.

        :param label: The edge label to be traversed (optional)
        :type label: str or BaseEdge or None
        :param limit: The number of the page to start returning results at
        :type limit: int or None
        :param offset: The maximum number of results to return
        :type offset: int or None
        :param types: A list of allowed element types
        :type types: list

        """
        return self._simple_traversal('bothE', labels, **kwargs)

    def bothV(self, *labels, **kwargs):
        """
        Return a list of vertices both incoming and outgoing from this vertex.

        :param label: The edge label to be traversed (optional)
        :type label: str or BaseEdge or None
        :param limit: The number of the page to start returning results at
        :type limit: int or None
        :param offset: The maximum number of results to return
        :type offset: int or None
        :param types: A list of allowed element types
        :type types: list

        """
        return self._simple_traversal('bothV', labels, **kwargs)

    def delete_outE(self, *labels, **kwargs):
        """Delete all outgoing edges with the given label."""
        return self._simple_deletion('outE', labels, **kwargs)

    def delete_inE(self, *labels, **kwargs):
        """Delete all incoming edges with the given label."""
        return self._simple_deletion('inE', labels, **kwargs)

    def delete_outV(self, *labels, **kwargs):
        """
        Delete all outgoing vertices connected with edges with the given
        label.
        """
        return self._simple_deletion('outV', labels, **kwargs)

    def delete_inV(self, *labels, **kwargs):
        """Delete all incoming vertices connected with edges with the given label."""
        return self._simple_deletion('inV', labels, **kwargs)

    def query(self):
        from goblin.models.query import V
        return V(self)
