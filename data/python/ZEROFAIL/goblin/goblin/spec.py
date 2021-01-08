from __future__ import unicode_literals
from six import print_
import json

from goblin import connection


def get_existing_indices():
    """ Find all Vertex and Edge types available in the database """
    vertex_indices = connection.execute_query('mgmt = graph.openManagement(); mgmt.getVertexLabels().collect{it.name()}')
    edge_indices = connection.execute_query('mgmt = graph.openManagement(); mgmt.getRelationTypes(EdgeLabel).collect{it.name()}')
    return vertex_indices, edge_indices


def make_property_key(name, data_type, cardinality, graph_name=None, **kwargs):
    graph_name = graph_name or connection._graph_name or "graph"
    script = """
        try {
            mgmt = graph.openManagement()
            name = mgmt.makePropertyKey('%s').dataType(%s.class)
            name.cardinality(Cardinality.%s).make()
            mgmt.commit()
        } catch (err) {
            graph.tx().rollback()
            throw(err)
        }""" % (name, data_type, cardinality)
    return _property_handler(script, graph_name, **kwargs)


def get_property_key(name, graph_name=None, **kwargs):
    graph_name = graph_name or connection._graph_name or "graph"
    script = """
        try {
            mgmt = graph.openManagement()
            prop = mgmt.getPropertyKey('%s')
            return prop
        } catch (err) {
            graph.tx().rollback()
            throw(error)
        } """ % (name)
    # This returns a vertex...?
    return _property_handler(script, graph_name, **kwargs)


def change_property_key_name(old_name, new_name, graph_name=None, **kwargs):
    script = """
        try {
            mgmt = graph.openManagement()
            prop = mgmt.getPropertyKey('%s')
            mgmt.changeName(prop, '%s')
            mgmt.commit()
        } catch (err) {
            graph.tx().rollback()
            throw(err)
        }""" % (old_name, new_name)
    return _property_handler(script, graph_name, **kwargs)


def _property_handler(script, graph_name, **kwargs):
    future = connection.get_future(kwargs)
    future_response = connection.execute_query(script, graph_name=graph_name)

    def on_read(f2):
        try:
            result = f2.result()
        except Exception as e:
            future.set_exception(e)
        else:
            future.set_result(result)

    def on_key(f):
        try:
            stream = f.result()
        except Exception as e:
            future.set_exception(e)
        else:
            future_read = stream.read()
            future_read.add_done_callback(on_read)

    future_response.add_done_callback(on_key)
    return future


def write_diff_indices_to_file(filename, spec=None):  # pragma: no cover
    """ Preview of index diff specification to write to file

    :param filename: The file to write to
    :type filename: basestring
    """
    if not spec:
        print_("Generating Specification...")
        spec = connection.generate_spec()
    print_("Writing Compiled Diff Indices to File %s ..." % filename)
    vertex_indices, edge_indices = get_existing_indices()
    with open(filename, 'wb') as f:
        for s in spec:
            for pn, pv in s['properties'].items():
                if s['element_type'] == 'Edge' and pn not in edge_indices:
                    f.writelines([pv['compiled'], ])
                elif s['element_type'] == 'Vertex' and pn not in vertex_indices:
                    f.writeliness([json.dumps(pv['compiled']), ])


def write_compiled_indices_to_file(filename, spec=None):  # pragma: no cover
    """ Write the compile index specification to file

    :param filename: The file to write to
    :type filename: basestring
    """
    if not spec:
        print_("Generating Specification...")
        spec = connection.generate_spec()
    print_("Writing Compiled Indices to File %s ..." % filename)
    with open(filename, 'wb') as f:
        for s in spec:
            for pn, pv in s['properties'].items():
                f.writelines([json.dumps(pv['compiled']), ])


def write_specs_to_file(filename):  # pragma: no cover
    """ Generate and write a specification to file

    :param filename: The file to write to
    :type filename: basestring
    """
    print_("Generating Specification...")
    spec = connection.generate_spec()
    print_("Writing Specification to File %s ..." % filename)
    with open(filename, 'wb') as f:
        json.dump(spec, f)
    write_compiled_indices_to_file(filename+'.idx', spec=spec)
    write_compiled_indices_to_file(filename+'.idxdiff', spec=spec)
