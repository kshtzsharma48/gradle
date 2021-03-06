## New and noteworthy

Here are the new features introduced in this Gradle release.

### Hooking into the dependency resolution

In order to make Gradle's dependency resolution even more robust
we added a new way of influencing the dependency resolution process.
Since Gradle 1.4 it is possible to specify *dependency resolve actions*.
An action is executed for each resolved dependency and offers ways
to manipulate the dependency metadata before the dependency is resolved.

The feature is incubating for now and not entire dependency metadata can be manipulated at this time.
We are very keen on your feedback and we will definitely grow this feature,
allowing more metadata to be manipulated, and more dependency resolution corner cases solved.
Even though dependency resolve actions are lower level hooks
in future we will use them to provide many high level features in Gradle's dependency engine.

For more information, including the code sample, please refer to this [user guide section](userguide/userguide_single.html#sec:dependency_resolve_actions).

### Easier to embed Gradle via [Tooling API](userguide/embedding.html)

We continuously look for ways to improve the experience of embedding Gradle.
The standard way to embed Gradle, [The Tooling API](userguide/embedding.html) used to ship in multiple jars, including some 3rd party libraries.
In Gradle 1.4 we refactored the publication and packaging of the Tooling API. The Tooling API is now shipped in a single jar.
All you need to work with the Tooling API is the tooling api jar and slf4j.
Furthermore, we repackaged the Tooling API's 3rd party transitive dependencies to avoid conflicts
with different versions you might already have on your classpath. Happy embedding!Now go and embed Gradle!

### Dependency resolution improvements

- GRADLE-2175 - Source, javadoc and classifier artifacts from Maven snapshots are correctly treated as changing.
- GRADLE-2364 - `--offline` works after resolving against a broken repository.
- GRADLE-2185 - Faster resolution of Maven snapshots.
- GRADLE-1919 - Added `m2Compatible` option.
- GRADLE-2546 - Faster searching for local candidates.

### Improvements to dependency resolution reports

Dependency resolution reports now show dependencies that couldn't be resolved. Here is an example for the `dependencies` task:

    compile - Classpath for compiling the sources.
    \--- foo:bar:1.0
         \--- foo:baz:2.0 FAILED

The `FAILED` marker indicates that `foo:baz:2.0`, which is depended upon by `foo:bar:1.0`, couldn't be resolved.

A similar improvement has been made to the `dependencyInsight` task:

    foo:baz:2.0 (forced) FAILED

    foo:baz:1.0 -> 2.0 FAILED
    \--- foo:bar:1.0
         \--- compile

In this example, `foo:baz` was forced to version `2.0`, but that version couldn't be resolved.

<!--
### Example new and noteworthy
-->

## Promoted features

Promoted features are features that were incubating in previous versions of Gradle but are now supported and subject to backwards compatibility.
See the User guide section on the “[Feature Lifecycle](userguide/feature_lifecycle.html)” for more information.

The following are the features that have been promoted in this Gradle release.

<!--
### Example promoted
-->

## Fixed issues

<!--
### Example promoted
-->

## Incubating features

Incubating features are intended to be used, but not yet guaranteed to be backwards compatible.
By giving early access to new features, real world feedback can be incorporated into their design.
See the User guide section on the “[Feature Lifecycle](userguide/feature_lifecycle.html)” for more information.

The following are the new incubating features or changes to existing incubating features in this Gradle release.

### Generate ivy.xml without publishing

The 'ivy-publish' plugin introduces a new GenerateIvyDescriptor task, which permits the generation of the ivy.xml metadata file without also publishing
your module to an ivy repository. The task name for the default ivy publication is 'generateIvyModuleDescriptor'.

The GenerateIvyDescriptor task also allows the location of the generated ivy descriptor file to changed from it's default location at `build/publications/ivy/ivy.xml`.
This is done by setting the `destination` property of the task:

    apply plugin: "ivy-publish"

    group = 'group'
    version = '1.0'

    // … declare dependencies and other config on how to build

    generateIvyModuleDescriptor {
        destination = 'generated-ivy.xml'
    }

Executing `gradle generateIvyModuleDescriptor` will result in the ivy module descriptor being written to the file specified. This task is automatically wired
into the respective PublishToIvyRepository tasks, so you do not need to explicitly call this task to publish your module.

## Deprecations

### Certain task configuration after execution of task has been started.

Changing certain task configuration does not make sense when the task is already being executed.
For example, imagine that at execution time, the task adds yet another doFirst {} action.
The task is already being executed so adding a *before* action is too late and it is probably a user mistake.
In order to provide quicker and higher quality feedback on user mistakes
we want to prevent configuring certain task properties when the task is already being executed.
For backwards compatibility reasons, certain task configuration is deprecated. This includes:

* Mutating Task.getActions()
* Calling Task.setActions()
* Calling Task.dependsOn()
* Calling Task.setDependsOn()
* Calling Task.onlyIf()
* Calling Task.setOnlyIf()
* Calling Task.doLast()
* Calling Task.doFirst()
* Calling Task.leftShift()
* Calling Task.setEnabled()
* Calling TaskInputs.files()
* Calling TaskInputs.file()
* Calling TaskInputs.dir()
* Calling TaskInputs.property()
* Calling TaskInputs.properties()
* Calling TaskInputs.source()
* Calling TaskInputs.sourceDir()
* Calling TaskOutputs.upToDateWhen()
* Calling TaskOutputs.files()
* Calling TaskOutputs.file()
* Calling TaskOutputs.dir()

## Potential breaking changes

### Incubating DependencyInsightReport throws better exception

For consistency, InvalidUserDataException is thrown instead of ReportException when user incorrectly uses the dependency insight report.

### Removed getSupportsAppleScript() in org.gradle.util.Jvm

In the deprecated internal class `org.gradle.util.Jvm` we removed the method `getSupportsAppleScript()` to check that AppleScriptEngine is available on the Jvm.
As a workaround you can dynamically check if the AppleScriptEngine is available:

    import javax.script.ScriptEngine
    import javax.script.ScriptEngineManager

    ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine engine = mgr.getEngineByName("AppleScript");
    boolean isAppleScriptAvailable = engine != null;

### IvyPublication (incubating) no longer has `descriptorFile` property

In v1.3 it was possible to set the `descriptorFile` property on an IvyPublication object. This property has been removed with the introduction of the new
GenerateIvyDescriptor task. To specify where the ivy.xml file should be generated, set the `destination` property of the GenerateIvyDescriptor task.

## External contributions

We would like to thank the following community members for making contributions to this release of Gradle.

James Bengeyfield - `showViolations` flag for `Checkstyle` task (GRADLE-1656)

Dalibor Novak - `m2compatible` flag on `PatternRepositoryLayout` (GRADLE-1919)

Brian Roberts, Tom Denley - Support multi-line JUnit test names (for better ScalaTest compatibility) (GRADLE-2572)

<!--
* Some Person - fixed some issue (GRADLE-1234)
-->

We love getting contributions from the Gradle community. For information on contributing, please see [gradle.org/contribute](http://gradle.org/contribute).