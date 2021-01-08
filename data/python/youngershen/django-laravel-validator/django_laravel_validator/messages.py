# -*- coding:utf-8 -*-
# PROJECT_NAME : django-laravel-validator
# FILE_NAME    : 
# AUTHOR       : younger shen

REQUIRED_MESSAGE = u'The field under validation must be present in the input data.'
NUMERIC_MESSAGE = u'The field under validation must have a numeric value.'
MIN_MESSAGE = u'The field under validation must have a minimum value : {min}.'
MAX_MESSAGE = u'The field under validation must be less than or equal to a maximum value : {max}'
LENGTH_MESSAGE = u'The field under validation must have a size of {length}'
RANGE_MESSAGE = u'The field under validation must have a size between the given min and max of {min} to {max}'
ACCEPTED_MESSAGE = u'The field under validation must be yes, on, or 1.'
ALPHA_MESSAGE = u'The field under validation must be entirely alphabetic characters'
EMAIL_MESSAGE = u'The field under validation must be formatted as an e-mail address.'
IP_ADDRESS_MESSAGE = u'The field under validation must be formatted as an IP address.'
BOOLEAN_MESSAGE = u'The field under validation must be able to be cast as a boolean. Accepted input are true, false, 1, 0, "1" and "0".'
REGEX_MESSAGE = u'The field under validation must match the given regular expression : {regex} .'
MATCH_MESSAGE = u'the field must match : {match}. '
UNIQUE_MESSAGE = u'the {field} field must be unique of table {model}.'
JSON_MESSAGE = u'the {field} field must be a json string'
# error messages

UNIQUE_UNKNOW_APP_NAME = u'unknow app name : {appname}'
UNIQUE_UNKNOW_MODEL_NAME = u'unknow model name : {appname}.models.{modelname}'
UNIQUE_UNKNOW_MODEL_FIELD = u'unknow model field name : {fieldname}'