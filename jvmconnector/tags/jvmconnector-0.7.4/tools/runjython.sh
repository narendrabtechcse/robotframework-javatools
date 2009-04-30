#!/bin/bash

if [ -f pom.xml ]; then
  if [ ! -f 'dependencies.txt' ]; then
    mvn dependency:list -DoutputAbsoluteArtifactFilename=true|grep jar|sed 's/.*://'> 'dependencies.txt'
  fi

  if [ ! -d 'target/classes' ] || [ ! -d 'target/test-classes' ]; then
    mvn test-compile
  fi

  export CLASSPATH=`cat dependencies.txt|xargs echo|sed 's/ /:/g'`:target/classes:target/test-classes
fi

jython -Dpython.path=/usr/lib/python2.5/site-packages/:src/main/python:src/test/python $*
