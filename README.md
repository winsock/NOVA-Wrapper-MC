[![Build Status](https://img.shields.io/travis/NOVAAPI/NovaWrapper-MC.svg?style=flat-square)](https://travis-ci.org/NOVAAPI/NovaWrapper-MC)
[![Coverage](https://img.shields.io/codecov/c/github/NOVAAPI/NovaWrapper-MC.svg?style=flat-square)](https://codecov.io/github/NOVAAPI/NovaWrapper-MC)
[![Tests](https://img.shields.io/jenkins/t/http/jenkins.magik6k.net/NovaWrapper-MC1.7.10.svg?style=flat-square)](http://jenkins.magik6k.net/job/NovaWrapper-MC1.7.10/lastCompletedBuild/testReport/)

Nova API
========
NOVA is a voxel game modding framework designed to allow mods to be run across different voxel games.

NOVA Minecraft Wrapper is licensed under the LGPL v3 License.
http://opensource.org/licenses/lgpl-3.0.html

### Set Up
To set up NOVA Wrapper for Minecraft, use the standard procedure for setting up a Forge mod.

1. Go into the directory of NOVA Wrapper
2. Open command line and type "gradlew setupDecompWorkspace eclipse" or "gradlew setupDecompWorkspace idea"
3. Let Gradle run dependencies. If you are using IntelliJ, a .ipr file should be generated. Open the ipr file and IntelliJ should open with the correct setup.
4. If you are trying to contribute to NOVA Core, you must also clone NOVA Core. In IntelliJ, add NOVA Core as a module dependency (and remove NOVA Core as a jar dependency) so you can edit the source code.

### Dependencies
* Guice
* JUnit
* AssertJ

Using the IDEA formatter
------------------------
To use the formatter you find [here](https://github.com/NOVAAPI/NovaCore/tree/master/guidelines),
start IntelliJ IDEA, go to `Files->Import Settings...`,
select `guidelines/intelliJ-formatter.jar` and click `OK`.
