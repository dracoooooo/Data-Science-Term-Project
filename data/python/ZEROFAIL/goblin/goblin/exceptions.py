from __future__ import unicode_literals


class GoblinException(Exception):
    """ Generic Base Exception for Goblin Library """
    pass


class GoblinConnectionError(GoblinException):
    """ Problem connecting with Titan """
    pass


class GoblinGraphMissingError(GoblinException):
    """ Graph with specified name does not exist """
    pass


class GoblinQueryError(GoblinException):
    """ Exception thrown when a query error occurs """
    pass


class ValidationError(GoblinException):
    """ Exception thrown when a property value validation error occurs """

    def __init__(self, *args, **kwargs):
        self.code = kwargs.pop('code', None)
        super(GoblinException, self).__init__(*args, **kwargs)


class ElementDefinitionException(GoblinException):
    """ Error in element definition """
    pass


class ModelException(GoblinException):
    """ Error in model """
    pass


class SaveStrategyException(GoblinException):
    """ Exception thrown when a Save Strategy error occurs """
    pass


class GoblinGremlinException(GoblinException):
    """ Exception thrown when a Gremlin error occurs """
    pass


class GoblinRelationshipException(GoblinException):
    """ Exception thrown when a Relationship error occurs """
    pass


class GoblinMetricsException(GoblinException):
    """ Exception thrown when a metric system error occurs """
    pass


class GoblinBlueprintsWrapperException(GoblinException):
    """ Exception thrown when a Blueprints wrapper error occurs """
    pass
