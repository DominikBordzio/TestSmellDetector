# Test Smell Detector

## Introduction

Unit test code, just like any other source code, is subject to bad programming practices, known also as anti-patterns, defects and smells. Smells, being symptoms of bad design or implementation decisions, has been proven to be responsible for decreasing the quality of software systems from various aspects, such as making it harder to understand, more complex to maintain, more prone to errors and bugs.

Test smells are defined as bad programming practices in unit test code (such as how test cases are organized, implemented and interact with each other) that indicate potential design problems in the test source code.



## Project Overview

The purpose of this project is twofold:

1. Contribute to the list of existing test smells, by proposing new test smells that developers need to be aware of.
2. Provide developers with a tool to automatically detect test smell in their unit test code. 


## More Information

Visit the project website: https://testsmells.github.io/

# PBR Development notes

## JavaParser guide
https://leanpub.com/javaparservisited

## Example class - DeadFields
DeadFields implements a ClassVisitor, which allows the following steps:
1. visit(ClassOrInterfaceDeclaration declaration) - a class is parsed, all declared methods and class fields (testFields array) are extracted
2. visit(MethodDeclaration n) - for every declared method, some action is performed. In this example, every method is recursively
searched to find all Nodes of NameExpr type (which in this case are the the fields used inside of that method).

## Generating output
To generate output files from TsDetect the user has to provide an input csv file with the following data:
1. Project name
2. Path to java test file
3. Path to java produciton file
e.g. PBR;C:\apps\pbr\src\test\RibTest.java;C:\apps\pbr\src\Rib.java

To execute detection in TsDetect jar directory:
java -jar .\TestSmellDetector.jar pathToInputFile.csv



