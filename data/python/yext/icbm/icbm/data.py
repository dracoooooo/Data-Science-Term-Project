#!/usr/bin/python

import glob
import functools
import os
import sys

import engine

def cache(f):
    """A decorator to cache results for a given function call.

    Note: The caching is only done on the first argument, usually "self".
    """
    ret = {}
    def _Wrapper(*args, **kwargs):
        self = args[0]
        if self not in ret:
            ret[self] = f(*args, **kwargs)
        return ret[self]
    return _Wrapper

VERBOSE = True
JAVA_BINARY_FLAGS_DEFAULT = False

printed = set()
def pdep(a, b):
    """Interceptor for printing dependency links.

    Args:
      a: Source dependency
      b: Destination dependency
    """
    if (a, b) in printed:
        return
    if a == b:
        return
    if VERBOSE:
        print "\"%s\" -> \"%s\"" % (a, b)
    printed.add((a, b))

TOPLEVEL = "_top"

def abs_target(target, default_module=None):
    """Determines the canonical representation of a target, while
    allowing syntactic sugar for the user.

    Params:
      target: e.g. "src=com/yext/subscriptions:app", src/com/yext/subscriptions:app"
      default_module: optional module that can be used to resolve a module-less path.

    Returns:
      The fully qualified target name.

    Raises:
      Error if no module can be found and none is provided.
    """
    # If the target is fully-specified, just return it.
    if "=" in target:
        return target

    # If the target doesn't have a : in it, assume that the "file" in
    # the directory is actually the target name.
    if ":" not in target and "/" in target:
        d, t = target.rsplit("/", 1)
        target = "%s:%s" % (d, t)

    # If the target ends with .java, then likely the user just
    # autocompleted and wants the target without .java, so strip it
    # out.
    if target.endswith(".java"):
        target = target[:-5]

    # Try splitting on 'src' directory
    if "src/" in target:
        module, rt = target.split("src/")
        return "%ssrc=%s" % (module, rt)

    # The test directory doesn't have a src dir, so add a special
    # case syntactic sugar to add "=" after test.
    elif target.startswith("test/"):
        return "test=%s" % (target[5:])

    # Ran out of guesses, use default module
    elif default_module is not None:
        return "%s=%s" % (default_module, target)

    else:
        raise Exception("No module could be determined for target: " + target)


class DataHolder(object):

    """
    Class that is the holder of all objects specified in spec files.
    Keeps track of where they are specified, what they output, and
    intermediates their interactions with the Engine.
    """

    # path:name -> fake-o data holders, which know how to insert things
    # into the engine.
    _registered = {}

    # Set of dependency FullName's whose files have already been loaded.
    _processed = set()

    def __init__(self, module, path, name):
        """Constructor.

        Args:
          module: The module for this target (e.g. Core)
          path: The path for this target
          name: The target name within the path
        """
        self.module = module
        self.path = path
        self.name = name

    def FullName(self):
        """Returns the fully-qualified target name."""
        return "%s=%s:%s" % (self.module, self.path, self.name)

    def Apply(self, e):
        """Inserts any necessary rules into the engine.

        Args:
          e: An engine.Engine instance to insert rules into.

        Returns: A file name which will be generated by the last
        engine rule that was inserted.
        """
        raise NotImplementedError

    def TopApply(self, e):
        """See Apply.

        This function is called instead of Apply when a rule is being
        built explicitly at the command line.
        """
        raise NotImplementedError

    def LoadSpecs(self):
        """Loads spec files needed to resolve this target's dependencies."""
        raise NotImplementedError

    def _LoadSpecs(self, deps):
        """Loads the spec files that define the passed in dependencies.

        Args:
          deps: List of target names that this rule depends on.
        """
        deps = list(self.Canonicalize(deps))
        if VERBOSE:
            print "loading: %s" % str(deps)
        for dep in deps:
            pdep(self.FullName(), dep)
        while len(deps) > 0:
            depname = deps.pop()
            try:
                LoadTargetSpec(self.module, depname)
            except:
                print "%s: error loading %s=%s" % (self.FullName(), self.module, depname)
                raise
            dep = DataHolder.Get(self.module, depname)
            assert dep, "%s not found by %s:%s" % (depname, self.path, self.name)
            if dep.FullName() in self._processed:
                continue
            self._processed.add(dep.FullName())
            if isinstance(dep, JavaLibrary) and dep.deps:
                ds = list(dep.Canonicalize(dep.deps))
                deps.extend(ds)
                for d in ds:
                    pdep(dep.FullName(), d)
            else:
                dep.LoadSpecs()

    def Canonicalize(self, deps):
        """Fully-qualifies any non-fully-qualified dependencies in the list.

        Uses the current module when one isn't provided in the dependecy string.

        Args:
          deps: List of strings of dependency specifiers

        Returns: A list of fully-qualified dependency strings.
        """
        for dep in deps:
            yield abs_target(dep, self.module)

    @classmethod
    def Register(cls, module, path, name, obj):
        """Registers a given target in the global registry."""
        fname = "%s=%s:%s" % (module, path, name)
        assert fname not in cls._registered, fname
        assert isinstance(obj, DataHolder)
        cls._registered[fname] = obj

    @classmethod
    def Get(cls, module, fname):
        """Retrieves a target from the global registry."""
        fname = abs_target(fname, default_module=module)
        return cls._registered.get(fname)

    @classmethod
    def Go(cls, targets):
        """Builds everything starting with the given targets as the top-level.

        Args:
          targets: List of string specifiers of targets resolved in top-level
                   scope

        Returns: True if all the targets built successfully, False otherwise
        """
        done = set()
        e = engine.Engine()
        target_names = []
        for target in targets:
            holder = cls.Get(TOPLEVEL, target)
            if not holder:
                print >>sys.stderr, "Unknown target", target
                continue
            ret = holder.TopApply(e)
            if ret:
                target_names.append(ret)
        # Make sure to apply all of the Generate targets. Otherwise
        # their out's will never get considered.
        for obj in cls._registered.itervalues():
            if isinstance(obj, Generate):
                obj.Apply(e)
        e.ComputeDependencies()
        for target in target_names:
            e.BuildTarget(e.GetTarget(target))
        return e.Go()

class JavaBinary(DataHolder):

    """Class that holds a java_binary target."""

    def __init__(self, module, path, name, main, deps, flags=None, premain=None):
        DataHolder.__init__(self, module, path, name)
        self.main = main
        self.deps = deps
        self.flags = flags if flags is not None else JAVA_BINARY_FLAGS_DEFAULT
        self.premain = premain

    @cache
    def Apply(self, e):
        # Build up a list of source files, jars, and data files that
        # we need to get.
        sources = set()
        jars = self.jars = set()
        datas = set()

        deps = list(self.deps)
        processed = set()
        while len(deps) > 0:
            depname = deps.pop()
            dep = DataHolder.Get(self.module, depname)
            assert dep, "%s not found" % depname
            if dep.FullName() in processed:
                continue
            assert isinstance(dep, JavaLibrary), '%s is not a library' % depname

            dep.Apply(e)

            if dep.files:
                sources.update(dep.files)
            if dep.jars:
                jars.update(dep.jars)
            if dep.data:
                datas.update(dep.data)
            if dep.deps:
                deps.extend(dep.Canonicalize(dep.deps))
            processed.add(dep.FullName())

        if self.flags:
            dep = DataHolder.Get(
                self.module, "Core/src=com/alphaco/util/flags:flag_processor")
            dep.Apply(e)

        c = engine.JavaCompile(self.path, self.name, sources, jars,
                               datas, self.main, self.flags)
        e.AddTarget(c)
        return c.Name()

    TopApply = Apply

    def LoadSpecs(self):
        self._LoadSpecs(self.deps)
        if self.flags:
            self._LoadSpecs(
                ["Core/src=com/alphaco/util/flags:flag_processor"])


class JavaJar(DataHolder):

    """Class that holds a java_deploy target."""

    def __init__(self, module, path, name, binary, premain=None):
        DataHolder.__init__(self, module, path, name)
        self.binary = binary
        self.premain = premain

    @cache
    def Apply(self, e):
        dep = DataHolder.Get(self.module, self.binary)
        assert dep, "%s not found" % self.binary
        assert isinstance(dep, JavaBinary)
        dep.Apply(e)
        #name = dep.Apply(e)
        #target = e.GetTarget(name)
        j = engine.JarBuild(self.path, self.name + ".jar", dep.name,
                            dep.jars, dep.main, self.premain or dep.premain)
        e.AddTarget(j)
        return j.Name()

    TopApply = Apply

    def LoadSpecs(self):
        self._LoadSpecs([self.binary])


class JavaLibrary(DataHolder):

    """Class that holds a java_library target."""

    def __init__(self, module, path, name, files, jars, deps, data):
        DataHolder.__init__(self, module, path, name)
        self.jars = jars
        self.deps = deps
        self.data = data
        self.files = files

    @cache
    def TopApply(self, e):
        sources = set(self.files)
        jars = self.jars = set(self.jars)
        datas = set(self.data)

        deps = list(self.deps)
        processed = set()
        while len(deps) > 0:
            depname = deps.pop()
            dep = DataHolder.Get(self.module, depname)
            assert dep, "%s not found" % depname
            if dep.FullName() in processed:
                continue
            assert isinstance(dep, JavaLibrary)

            dep.Apply(e)

            if dep.files:
                sources.update(dep.files)
            if dep.jars:
                jars.update(dep.jars)
            if dep.data:
                datas.update(dep.data)
            if dep.deps:
                deps.extend(dep.Canonicalize(dep.deps))
            processed.add(dep.FullName())

        c = engine.JavaCompile(self.path, os.path.join(self.path, self.name),
                               sources, jars,
                               datas, "", False)
        e.AddTarget(c)
        return c.Name()

    def Apply(self, e):
        pass

    def LoadSpecs(self):
        if self.deps:
            self._LoadSpecs(self.deps)

class JavaWar(DataHolder):

    """Class that holds a java_war target."""

    def __init__(self, module, path, name, data, binary):
        DataHolder.__init__(self, module, path, name)
        self.data = data
        self.binary = binary

    @cache
    def Apply(self, e):
        dep = DataHolder.Get(self.module, self.binary)
        assert dep, "%s not found" % self.binary
        assert isinstance(dep, JavaBinary)
        dep.Apply(e)
        w = engine.WarBuild(self.path, self.name + ".war", self.data, dep.name, dep.jars)
        e.AddTarget(w)
        return w.Name()

    TopApply = Apply

    def LoadSpecs(self):
        if self.binary:
            self._LoadSpecs([self.binary])

class PlayApp(DataHolder):

    """Class that holds a play_app target."""

    def __init__(self, module, path, name, modules, deps, data, play_home):
        DataHolder.__init__(self, module, path, name)
        self.modules = modules
        self.deps = deps
        self.data = data
        self.play_home = play_home

    @cache
    def Apply(self, e):
        deps = []
        datas = set(self.data)
        for depname in self.deps:
            dep = DataHolder.Get(self.module, depname)
            deps.append(dep.Apply(e))

        for module in self.modules:
            assert os.path.exists(module), "play module not found: %s" % module

        c = engine.PlayCompile(self.path, self.name + ".zip", self.modules, deps, datas, self.play_home)
        e.AddTarget(c)
        return c.Name()

    TopApply = Apply

    def LoadSpecs(self):
        self._LoadSpecs(self.deps)

class Generate(DataHolder):

    """Class that holds a generate target."""

    def __init__(self, module, path, name, compiler, args, ins, outs):
        DataHolder.__init__(self, module, path, name)
        self.compiler = compiler
        self.args = args
        self.ins = ins
        self.outs = outs
        self.deps = []

    @cache
    def Apply(self, e):
        # TODO(rich): Make generate take java binary / library as ins
        # instead of using deps.
        deps = []
        for depname in self.deps:
            dep = DataHolder.Get(self.module, depname)
            deps.append(dep.Apply(e))

        target = engine.Generate(self.path, self.name, self.compiler, self.args,
                                 self.ins, self.outs, deps)
        e.AddTarget(target)
        return target.Name()

    def LoadSpecs(self):
        pass

class Alias(DataHolder):

    """Class that holds an alias target."""

    def __init__(self, module, path, name, deps):
        DataHolder.__init__(self, module, path, name)
        self.deps = deps

    def Apply(self, e):
        deps = []
        for depname in self.deps:
            dep = DataHolder.Get(self.module, depname)
            deps.append(dep.Apply(e))
        target = engine.Alias(self.path, "__alias_%s" % self.name, deps)
        e.AddTarget(target)
        return target.Name()

    TopApply = Apply

    def LoadSpecs(self):
        self._LoadSpecs(self.deps)

def FixPath(module, path, lst):
    """Computes real/fake paths used by the engine.

    Args:
      module: The current module
      path: The path relative to which the files in lst are given
      lst: A list of files relative to the module/path

    Yields: (fake, real) path tuples for each of the given files. The
    fake path is the path that the file should appear at in the
    output, and the real path is either an absolute path to where the
    file really resides, or it is the fake path in case the file does
    not actually exist.
    """
    if not lst:
        return
    for l in lst:
        fake_path = os.path.join(path, l)
        if module != TOPLEVEL:
            base = "."
            real_path = os.path.join(module, base, fake_path)
        else:
            base = "."
            real_path = os.path.join(base, fake_path)
        if os.path.exists(real_path):
            yield fake_path, os.path.abspath(real_path)
        else:
            yield fake_path, fake_path

def java_library(module, dpath, name, path=None,
                 files=None, jars=None, deps=None, data=None,
                 jars_override=False, deps_override=False):
    if path:
        dpath = path
    obj = DataHolder.Get(module, "%s:%s" % (dpath, name))
    if not obj:
        obj = JavaLibrary(module, dpath, name, [], [], [], [])
        DataHolder.Register(module, dpath, name, obj)

    if files:
        obj.files.extend(FixPath(module, dpath, files))
    if jars:
        if jars_override:
            obj.jars = list(FixPath(module, dpath, jars))
        else:
            obj.jars.extend(FixPath(module, dpath, jars))
    if deps:
        if deps_override:
            obj.deps = deps
        else:
            obj.deps.extend(deps)
    if data:
        obj.data.extend(FixPath(module, dpath, data))


def java_binary(module, dpath, name, main="", deps=None,
                flags=None, path=None, premain=None):
    if path:
        dpath = path
    obj = DataHolder.Get(module, "%s:%s" % (dpath, name))
    if not obj:
        # This name was not previously autogenerated. Create a new JavaBinary.
        obj = JavaBinary(module, dpath, name, main, deps, flags, premain)
        DataHolder.Register(module, dpath, name, obj)
        jar = JavaJar(module, dpath, name + "_deploy", obj.FullName())
        DataHolder.Register(module, dpath, name + "_deploy", jar)
    else:
        # Extend/selectively override the previously autogenerated JavaBinary.
        if main:
            obj.main = main
        if deps:
            obj.deps.extend(deps)
        if flags is not None:
            obj.flags = flags
        if premain:
            obj.premain = premain


def java_deploy(module, dpath, name, binary, path=None, premain=None):
    if path:
        dpath = path
    obj = JavaJar(module, dpath, name, binary, premain)
    DataHolder.Register(module, dpath, name, obj)

def java_war(module, dpath, name, data, deps=None, path=None):
    if path:
        dpath = path
    if deps:
        binary = JavaBinary(module, dpath, name + "_deps",
                            None, deps, flags=False)
        DataHolder.Register(module, dpath, name + "_deps", binary)
    else:
        binary = None

    war = JavaWar(module, dpath, name,
                  list(FixPath(module, dpath, data)),
                  binary.FullName())
    DataHolder.Register(module, dpath, name, war)

def play_app(module, dpath, name, modules, deps=None, path=None, data=None, play_home="thirdparty/play"):
    if path:
        dpath = path
    obj = PlayApp(module, dpath, name, modules, [], [], play_home)
    DataHolder.Register(module, dpath, name, obj)
    if deps:
        obj.deps.extend(deps)
    if data:
        obj.data.extend(FixPath(module, dpath, data))

def generate(module, dpath, name, compiler=None, args=None, ins=None, outs=None, path=None, deps=None):
    if path:
        dpath = path
    obj = Generate(module, dpath, name, compiler, args,
                   list(FixPath(module, dpath, ins)),
                   map(lambda x: x[0], FixPath(module, dpath, outs)))
    DataHolder.Register(module, dpath, name, obj)
    if deps:
        obj.deps.extend(deps)

def alias(module, path, name, deps):
    obj = Alias(module, path, name, deps)
    DataHolder.Register(module, path, name, obj)

loaded = set()

def LoadTargetSpec(module, target):
    """Loads the spec file that should contain the target in question.

    Args:
      module: The module relative to which the target should be evaluated
      target: The target whose spec file we should load
    """
    # TODO: cache eval results, perhaps reorganize things so that they
    # are cacheable, to avoid reparsing all the files every time.
    target = abs_target(target, module)
    if "=" in target:
        module, target = target.split("=", 1)
    assert module, "module unknown for target %s" % target
    assert ":" in target, target
    dirname, tgt = target.split(":")
    if module == TOPLEVEL:
        base = "."
        fn = os.path.join(base, dirname, "build.spec")
    else:
        base = "."
        fn = os.path.join(module, base, dirname, "build.spec")
    if fn in loaded:
        return
    elif not os.path.exists(fn):
        return
    #print "loading", fn
    loaded.add(fn)
    builtins = dict(globals()["__builtins__"])
    del builtins["__import__"]
    d = os.path.dirname(fn)
    def relglob(pattern, excludes=[]):
        """Special glob function that returns a list of paths relative
        to the directory of the spec file.
        """
        return [os.path.relpath(fn, d)
                for fn in glob.glob(os.path.join(d, pattern))
                if os.path.relpath(fn, d) not in excludes]
    scope = {
        "__builtins__": builtins,
        "java_library": functools.partial(java_library, module, dirname),
        "java_binary": functools.partial(java_binary, module, dirname),
        "java_deploy": functools.partial(java_deploy, module, dirname),
        "java_war": functools.partial(java_war, module, dirname),
        "play_app": functools.partial(play_app, module, dirname),
        "generate": functools.partial(generate, module, dirname),
        "alias": functools.partial(alias, module, dirname),
        "glob": relglob,
        }
    execfile(fn, scope)

SRCDIR = "src"