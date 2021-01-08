# -*- coding:utf-8 -*-
# PROJECT_NAME : django-laravel-validator
# FILE_NAME    : 
# AUTHOR       : younger shen

from django_laravel_validator.exceptions import InvalidMinValidatorParameterError
from django_laravel_validator.utils import check_errors
from django_laravel_validator.validator import Validator
from .messages import *


class TestValidator1(Validator):
    username = 'required'
    password = 'required'

    def check(self):
        username = self.data.get('username')
        password = self.data.get('password')

        if username != 'youngershen':
            self.error_list.get('username').update(dict(wrong_user=u'you got a wrong user'))

        if password != '123456':
            self.error_list.get('password').update(dict(wrong_pass=u'you got a wrong pass'))


class TestValidator2(Validator):
    username = 'required|min:8|numeric'
    password = 'required|min:8|numeric'

    def check(self):
        print ' i am checking '


class TestValidator3(Validator):

    username = 'required|min:sdf|numeric|sss'
    password = 'required|min:sss|numeric'


class TestValidator4(Validator):
    username = 'required|min:8|numeric'
    password = 'required|min:2|numeric'


class TestValidator5(Validator):
    username = 'required|min:8|max:16|alpha'
    password = 'required|range:2,5|alpha'


class TestValidator6(Validator):
    username = 'required|length:5|alpha'
    password = 'required|range:2,5'
    remember_me = 'accepted'
    email = 'required|email'
    ip = 'ip'
    is_superuser = 'bool'
    phone = 'required|regex:^[0-9]{5,10}$'


class MatchValidatorTestValidator(Validator):
    password = 'required'
    password_confirm = 'required|match:password'


class UniqueValidatorTestValidator(Validator):
    email = 'required|unique:account.User,email'


def test_match_validator():
    data = dict(password='123', password_confirm='123')
    validator = MatchValidatorTestValidator(data)
    ret = validator.fails()
    assert ret


def test_validator():
    data = dict(username='youngershen', password='123456')
    validator = TestValidator1(data)
    ret = validator.fails()
    error_list = validator.errors()

    assert ret is True
    assert check_errors(error_list, None)

    data = dict(username='123', password='123')

    validator = TestValidator1(data)
    ret = validator.fails()
    error_list = validator.errors()

    assert ret is False
    assert check_errors(error_list, None) is False


def test_validator2():
    data = dict(username='123456789', password='123456789')
    validator = TestValidator2(data)
    ret = validator.fails()
    error_list = validator.errors()

    assert ret is True
    assert check_errors(error_list, None) is True

    data = dict(username='', password='wwwsf')
    validator = TestValidator2(data)
    ret = validator.fails()
    error_list = validator.errors()

    assert ret is False
    assert error_list.get('username').get('required') == REQUIRED_MESSAGE
    assert error_list.get('username').get('min') == MIN_MESSAGE.format(min=8)
    assert error_list.get('username').get('numeric') == NUMERIC_MESSAGE
    assert error_list.get('password').get('min') == MIN_MESSAGE.format(min=8)
    assert error_list.get('password').get('numeric') == NUMERIC_MESSAGE


def test_validator3():
    data = dict(username='tinker', password='i am tinker')
    validator = TestValidator3(data)
    try:
        ret = validator.fails()
    except InvalidMinValidatorParameterError as e:
        pass


def test_validator4():
    data = dict(username='', password='')
    message = {'username.required': 'haha', 'password.required': 'heihei', 'username.min': 'min'}

    validator = TestValidator4(data, message=message)

    ret = validator.fails()
    error_list = validator.errors()
    assert ret is False
    assert error_list.get('username').get('required') == 'haha'
    assert error_list.get('password').get('required') == 'heihei'
    assert error_list.get('username').get('min') == 'min'
    assert error_list.get('username').get('numeric') == NUMERIC_MESSAGE


def test_validator5():
    data = dict(username='', password='')
    validator = TestValidator5(data)
    ret = validator.fails()
    error_list = validator.errors()

    assert ret is False

    assert error_list.get('username').get('required') == REQUIRED_MESSAGE
    assert error_list.get('username').get('min') == MIN_MESSAGE.format(min=8)
    assert error_list.get('username').get('max') == MAX_MESSAGE.format(max=16)
    assert error_list.get('username').get('alpha') == ALPHA_MESSAGE

    assert error_list.get('password').get('required') == REQUIRED_MESSAGE
    assert error_list.get('password').get('range') == RANGE_MESSAGE.format(min=2, max=5)
    assert error_list.get('password').get('alpha') == ALPHA_MESSAGE


def test_validator6():
    data = dict(username='abcef', password='abc', remember_me='1', email='younger.x.shen@gmail.com', ip='192.168.1.1', is_superuser='1', phone='123456')
    validator = TestValidator6(data)
    ret = validator.fails()
    error_list = validator.errors()

    assert not error_list.get('username')
    assert not error_list.get('password')
    assert not error_list.get('remember_me')
    assert not error_list.get('email')
    assert not error_list.get('ip')
    assert not error_list.get('is_superuser')


def test_validator7():
    data = dict(username='youngershen', email='root@root.com')
    validator = UniqueValidatorTestValidator(data)
    ret = validator.fails()
    assert ret is True
