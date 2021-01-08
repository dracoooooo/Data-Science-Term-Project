'''This software, written by Zack Mullaly <redwire@riseup.net>, is
designed to help developers to move away from their reliance on NPM
and instead directly reference their dependencies' repositories
in their Node.js projects.
'''

import argparse
import urllib2
import json
import sys
import os


def compare_version_numbers(v1, v2):
    '''Determine whether the first version number, such as 1.5.12,
is greater than, equal to, or less than the second.'''
    parts1 = v1.split('.')
    parts2 = v2.split('.')
    for i in range(len(parts1)):
        num1 = int(''.join([c for c in parts1 if c in '0123456789']))
        num2 = int(''.join([c for c in parts2 if c in '0123456789']))
        compared_value = cmp(num1, num2)
        if compared_value != 0:
            return compared_value
    return 0


def project_info(package_name):
    '''Get the JSON information about a package.'''
    registry_address = 'https://registry.npmjs.org/' + package_name
    try:
        body = urllib2.urlopen(registry_address).read()
        return json.loads(body)
    except urllib2.HTTPError:
        return None


def dependencies(package_json, version_number=None):
    '''Get the list of package names of dependencies for a package.'''
    versions = package_json['versions']
    if version_number is None:
        # Get the maximum version number if the caller didn't provide one explicity.
        version_number = sorted(versions, cmp=compare_version_numbers, reverse=True)[0]
    try:
        return package_json['versions'][version_number]['dependencies'].keys()
    except KeyError:
        return None


def repository_url(package_json):
    '''Get the URL of a package's repository'''
    try:
        return package_json['repository']['url']
    except KeyError:
        return None


def replace_dependencies(package_json, kind, memo={}):
    '''Replace a top-level package's dependencies with their repository URLs.
kind is a type of dependency, such as "dependencies" or "devDependencies" etc.'''
    deps = package_json.get(kind, None)
    if deps is None:
        return package_json, memo
    for dependency in deps.keys():
        if dependency not in memo:
            package = project_info(dependency)
            if package is None:
                continue
            repo = repository_url(package)
            if repo is None:
                continue
            if repo.startswith('http'):
                repo = 'git+' + repo
            package_json[kind][dependency] = repo
            memo[dependency] = repo
        else:
            package_json[kind][dependency] = memo[dependency]
    return package_json, memo


def recursively_replace_dependencies(node_modules_path, kind, memo={}):
    '''Recursively traverse into node_modules/ and then each dependency's node_modules/
directory, replacing dependencies as we go.'''
    package_json_filename = os.sep.join([node_modules_path, 'package.json'])
    if os.path.isfile(package_json_filename):
        try:
            in_file = open(package_json_filename)
            package_json = json.loads(in_file.read())
            in_file.close()
            new_pkg_json, new_memos = replace_dependencies(package_json, kind, memo)
            memo.update(new_memos)
            out_file = open(package_json_filename, 'w')
            out_file.write(json.dumps(new_pkg_json, indent=4))
            out_file.close()
        except IOError:
            sys.stderr.write('Could not read/write {0}'.format(package_json_filename))
    for name in os.listdir(node_modules_path):
        path = os.sep.join([node_modules_path, name])
        if os.path.isdir(path):
            recursively_replace_dependencies(path, kind, memo)
    return memo


def main():
    parser = argparse.ArgumentParser(
            description='Replace NPM dependencies with their repository URLs')
    parser.add_argument('-d', '--directory', type=str, default=os.curdir,
            help='The directory to begin executing dependency name replacement in')
    parser.add_argument('-k', '--kind', default='dependencies',
            choices=['dependencies', 'devDependencies', 'peerDependencies',
                     'bundledDependencies', 'optionalDependencies'],
            help='The kind of dependency to replace')
    parser.add_argument('-r', '--recursive', action='store_true', default=False,
            help='A flag to enable recursively replacing dependency names in subdirectories')
    parser.add_argument('-m', '--memo', type=str, default='',
            help='A path to a file containing the output of a previous n2g execution')
    arguments = parser.parse_args()

    # Make sure we have been provided with a valid directory from which we can begin
    # finding package.json files and node_modules/ directories.
    if not os.path.isdir(arguments.directory):
        sys.stderr.write('{0} is not a directory.\n'.format(arguments.directory))
        return

    # The program outputs the memo dictionary mapping dependency names to their
    # respective repository URLs, and the -m and --memo option allows the user
    # to specify a file from which such output can be read, so that we can save
    # on making requests for information found previously.
    if arguments.memo != '' and not os.path.isfile(arguments.memo):
        sys.stderr.write('Can not read {0} to get previously discovered repository URLs'.format(
            arguments.memo))
        return
    memoized_values = {}
    if arguments.memo != '':
        in_file = open(arguments.memo)
        memoized_values = json.loads(in_file.read())
        in_file.close()

    # If the recursive flag is provided, we will replace every single dependency
    # name with its repository URL both in the top-level package.json file and
    # the package.json file under each installed dependency in node_modules/ and
    # their corresponding node_modules/ directories.
    # If the flag is not provided, we will only make replacements in package.json.
    if arguments.recursive:
        memo = recursively_replace_dependencies(
                arguments.directory, arguments.kind, memo=memoized_values)
        sys.stdout.write('{0}\n'.format(json.dumps(memo, indent=4)))
    else:
        pkg_json_file = os.sep.join([arguments.directory, 'package.json'])
        if not os.path.isfile(pkg_json_file):
            sys.stderr.write('Could not open {0} for reading.\n'.format(pkg_json_file))
            return
        in_file = open(pkg_json_file)
        package_json = json.loads(in_file.read())
        in_file.close()
        _, memo = replace_dependencies(package_json, arguments.kind, memo=memoized_values)
        sys.stdout.write('{0}\n'.format(json.dumps(memo, indent=4)))

if __name__ == '__main__':
    main()
