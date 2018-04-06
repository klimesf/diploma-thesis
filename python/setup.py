#!/usr/bin/env python
# -*- coding: utf-8 -*-
import io
import re
from setuptools import setup

with io.open('README.md', 'rt', encoding='utf8') as f:
    readme = f.read()

with io.open('business_context/__init__.py', 'rt', encoding='utf8') as f:
    version = re.search(r'__version__ = \'(.*?)\'', f.read()).group(1)

setup(
    name='business_context',
    version='1.0.0',
    license='BSD',
    author='Filip Klimeš',
    author_email='klimefi1@fel.cvut.cz',
    packages=['business_context', 'business_context_grpc'],
    include_package_data=True,
    zip_safe=False,
    platforms='any',
    install_requires=[
    ],
    extras_require={
        'dev': [
            'pytest>=3',
        ],
    },
    classifiers=[
        'Intended Audience :: Developers',
        'License :: OSI Approved :: BSD License',
        'Operating System :: OS Independent',
        'Programming Language :: Python :: 3.6',
    ],
    entry_points={},
)
