from __future__ import unicode_literals
from goblin._compat import integer_types, float_types, string_types, iteritems
from goblin.tools import LazyImportClass
from goblin.exceptions import GoblinException

# avoid circular import
element = LazyImportClass('goblin.models.element.Element')
numeric_types = (integer_types + float_types)


class Row(object):
    """ Row

    A row represent a table row, from which it's columns can be accessed like
    a tuple or dict. Rows are read-only and accept elements or dicts with as
    initializers as a result of a GremlinTable query. Also the . getattr
    notation can be used to access elements

    Example:
    row = Row({'person': Friend.create(....), 'myval': 3})
    print "{}:{} - {}".format(row.friend_edge.nickname, row.person.name,
    row.myval)
    """

    __ready = False
    __okay_setattr = '_Row__'

    def __init__(self, data):
        # this class is cached, so no performance hit here.
        if isinstance(data, element.klass):
            data = data.as_dict()
        elif not isinstance(data, dict):
            raise GoblinException("Result data is not tabular!")
        self.__data = data
        for k, v in data.items():
            if isinstance(k, string_types):
                setattr(self, k, v)
        self.__position = 0
        self.__ready = True

    def __getslice__(self, i, j):
        return list(self.__data.values())[i:j]

    def __setslice__(self, i, j, sequence):
        raise GoblinException("Row is not editable")

    def __delslice__(self, i, j):
        raise GoblinException("Row is not editable")

    def __getitem__(self, item):
        if isinstance(item, numeric_types) or isinstance(item, slice):
            return list(self.__data.values())[item]
        return self.__data[item]

    def __setitem__(self, key, value):
        raise GoblinException("Row is not editable")

    def __delitem__(self, key):
        raise GoblinException("Row is not editable")

    def __setattr__(self, key, value):
        # only allow 'private' fields to be set
        if not self.__ready or key.startswith(self.__okay_setattr):
            super(Row, self).__setattr__(key, value)
        else:
            raise GoblinException("Row is not editable")

    def __delattr__(self, item):
        raise GoblinException("Row is not editable")

    def __iter__(self):
        return self

    def __next__(self):
        return self.next()

    def keys(self):
        return self.__data.keys()

    def values(self):
        return self.__data.values()

    def items(self):
        return self.__data.items()

    def iteritems(self):
        for k, v in self.__data.items():
            yield k, v

    def next(self):
        if self.__position == len(self.__data):
            self.__position = 0
            raise StopIteration()
        tmp = list(self.__data.values())[self.__position]
        self.__position += 1
        return tmp

    def __len__(self):
        return len(self.__data)

    def __repr__(self):
        result = "{}(".format(self.__class__.__name__)
        for k, v in iteritems(self.__data):
            result += "{}={}, ".format(k, v)
        result = result.rstrip(", ")
        result += ")"
        return result

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        else:
            return self.__data == other._Row__data


class Table(object):
    """ Table

    A table accepts the results of a GremlinTable in it's constructor.
    It can be iterated over like a normal list, but within the rows
    the dictionaries are accessible via .notation

    For example:

    # returns a table of people & my friend edge to them
    # the edge contains my nickname for that person
    friends = goblin.gremlin.GremlinTable()

    def get_friends_and_my_nickname(self):
        result = self.friends()
        for i in result:
            print "{}:{}".format(i.friend_edge.nickname, i.person.name)
    """

    def __init__(self, gremlin_result):
        if gremlin_result == [[]]:
            gremlin_result = []

        self.__gremlin_result = gremlin_result
        self.__position = 0

    def __getitem__(self, item):
        """
        Returns an enhanced dictionary
        """
        return Row(self.__gremlin_result[item])

    def __setitem__(self, key, value):
        raise GoblinException("Cannot edit Table result")

    def __delitem__(self, key):
        raise GoblinException("Cannot edit Table result")

    def __getslice__(self, i, j):
        return [Row(r) for r in self.__gremlin_result[i:j]]

    def __setslice__(self, i, j, sequence):
        raise GoblinException("Cannot edit Table result")

    def __delslice__(self, i, j):
        raise GoblinException("Cannot edit Table result")

    def __iter__(self):
        return self

    def __next__(self):
        return self.next()

    def next(self):
        if self.__position == len(self.__gremlin_result):
            self.__position = 0
            raise StopIteration()
        tmp = self.__gremlin_result[self.__position]
        self.__position += 1
        return Row(tmp)

    def __len__(self):
        return len(self.__gremlin_result)

    def __repr__(self):
        return '{}(rows={})'.format(
            self.__class__.__name__, len(self.__gremlin_result))


__all__ = ['Table', 'Row']
