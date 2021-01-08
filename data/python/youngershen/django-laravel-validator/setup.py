from setuptools import setup, find_packages

version = '0.1'

setup(name='django-laravel-validator',
      version=version,
      description="a data validator for django inspired the validator of laravel framework",
      long_description="""\
      a data validator for django inspired the validator of laravel framework
      easy to use easy to validate every thing.
      """,
      classifiers=['Development Status :: 2 - Pre-Alpha',
                   'Environment :: Web Environment',
                   'Framework :: Django',
                   'Intended Audience :: Developers',
                   'License :: OSI Approved :: MIT License',
                   'Natural Language :: English',
                   'Programming Language :: Python :: 2.7',
                   ],
      keywords='form validator, django, laravel, django-laravel-validator',
      author='younger shen',
      author_email='younger.x.shen@gmail.com',
      url='https://github.com/youngershen/django-laravel-validator',
      license='MIT',
      packages=find_packages(exclude=['ez_setup', 'examples', 'tests']),
      include_package_data=True,
      zip_safe=True,
      install_requires=[
          # -*- Extra requirements: -*-
          'Django >= 1.6',
      ],
      entry_points="""
      # -*- Entry points: -*-
      """,
      )
