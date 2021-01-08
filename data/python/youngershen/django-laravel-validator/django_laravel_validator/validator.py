# -*- coding:utf-8 -*-
# PROJECT_NAME : django-laravel-validator
# FILE_NAME    : 
# AUTHOR       : younger shen
import copy
from importlib import import_module
from django.core.exceptions import ValidationError
from .exceptions import InvalidRuleNameError
from .exceptions import InvalidValidateDataError
from .utils import format_args_split, check_errors, error_message_generate
from .rules import WITH_PARAMETERS_VALIDATOR


class BaseValidator(type):
    """
        Metaclass for all validators
    """

    def __init__(cls, name, bases, dct):
        new_attrs = dict()
        error_list = dict()
        error_list_ext = dict()
        if name is not 'Validator':
            for k in dct:
                if k != '__module__' and k != '__main__':
                    pattern = dct.get(k, None)
                    if pattern and not callable(pattern):
                        new_attrs.update(**{k: pattern})
                        error_list.update(**{k: {}})

            setattr(cls, 'validate_data', new_attrs)
            setattr(cls, 'error_list', error_list)
            setattr(cls, 'error_list_ext', error_list_ext)

        super(BaseValidator, cls).__init__(name, bases, dct)

    def __new__(mcs, name, bases, dct):
        return type.__new__(mcs, name, bases, dct)


class Validator(object):
    """
        base class for all Validator rules
    """
    __metaclass__ = BaseValidator

    def __init__(self, data, message=None):
        if not data:
            raise InvalidValidateDataError()

        self.data = data
        self.message = message
        self.validate_flag = True
        self.error_list = copy.deepcopy(self.error_list)
        self.error_list_ext = copy.deepcopy(self.error_list_ext)

    def add_error(self, error):
        self.error_list_ext.update(error)

    def get(self, item):
        return self.data.get(item, None)

    def get_extra(self, item):
        if self.extra_data:
            return self.extra_data.get(item)
        else:
            return None

    def set_extra(self, key, value):
        if not getattr(self, 'extra_data', None):
            setattr(self, 'extra_data', dict())
        update = {key: value}
        self.extra_data.update(update)

    def fails(self):
        validate_data = getattr(self, 'validate_data')
        for k in validate_data:
            rules = validate_data.get(k, None)
            if not rules:
                raise InvalidValidateDataError()
            else:
                rules_list = rules.split('|')
                for rule in rules_list:
                    rule_origin = rule
                    rule = rule.split(':')[0]
                    if rule is None or rule == '':
                        raise InvalidRuleNameError(rule=rule)

                    rule_validator = import_module('.rules', package='django_laravel_validator')

                    try:
                        regex = getattr(rule_validator, rule.upper())
                    except AttributeError:
                        raise InvalidRuleNameError(rule=rule)

                    if rule.upper() in WITH_PARAMETERS_VALIDATOR:
                        rule_args = format_args_split(rule_origin)
                        regex = regex(rule_args)
                    else:
                        regex = regex()
                    try:
                        setattr(regex, 'validator_instance', self)
                        regex(self.data.get(k, None))
                    except ValidationError as e:
                        error_message = error_message_generate(k, rule, self.message, e)
                        error_dict = self.error_list.get(k)
                        error_dict.update({rule: error_message})
                        self.error_list.get(k).update(error_dict)
                        self.validate_flag = False

        check = getattr(self, 'check', None)
        if check and callable(check) and self.validate_flag:
            check()

        return check_errors(self.error_list, self.error_list_ext)

    def errors(self):
        self.error_list.update(extra=self.error_list_ext)
        return self.error_list
