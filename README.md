<img align="right" src=".github/logo/256.png" height="256" width="256">

# Tickompiler

Tickompiler is a compiler/decompiler for Tickflow, a language based on the bytecode format used by the game Rhythm Heaven Megamix to describe its rhythm games.

[![Downloads](https://img.shields.io/github/downloads/SneakySpook/Tickompiler/total.svg)](https://github.com/SneakySpook/Tickompiler/releases)
[![License](https://img.shields.io/github/license/SneakySpook/Tickompiler.svg)](https://github.com/SneakySpook/Tickompiler/blob/master/LICENSE.txt)

In-depth documentation for Tickflow can be found [here](https://tickompiler.readthedocs.io/en/latest/).

Game files extracted and decompiled using this tool can be used in conjunction with [this patch](https://github.com/SneakySpook/RHMPatch).

# Running the program
Requires Java 8 or newer and for `java` to be in the path.

Open a terminal in the same directory as `tickompiler.jar` and run:

Java 15 and older:
```
java -jar tickompiler.jar --help
```

Java 16 and newer (due to issue #8):
```
java --add-opens java.base/java.lang=ALL-UNNAMED -jar tickompiler.jar --help
```
