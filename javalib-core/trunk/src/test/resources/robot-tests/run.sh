#!/bin/bash

set_working_dir() {
  cd `dirname $0`
  export SCRIPT_DIR=`pwd`
  cd - >/dev/null
}

set_root_dir() {
  START_DIR=`pwd`
  cd $SCRIPT_DIR
  until [ -f ./pom.xml ]; do
    cd ..
  done
  ROOT=`pwd`
  cd $START_DIR
  export ROOT
}

build() {
  cd $ROOT
  if [ -z "`ls target/*.jar 2>/dev/null`" ]; then
    mvn package $1
  fi
  if [ ! -d target/dependency ]; then
    mvn dependency:copy-dependencies $1
  fi
  cd - >/dev/null
}

resolve_classpath() {
  PATHS="dependency/*.jar *.jar test-classes"

  CLASSPATH=""
  for i in $PATHS; do
    for resource in `echo $ROOT/target/$i`; do
      if [ -z "$CLASSPATH" ]; then
        CLASSPATH="$resource"
      else
        CLASSPATH="$CLASSPATH:$resource"
      fi
    done
  done
  export CLASSPATH
}

MAVEN_PROFILE=""
if [ "--legacy" = $1 ]; then
  MAVEN_PROFILE="-Pretro"
  shift
fi

set_working_dir
set_root_dir
build $MAVEN_PROFILE
resolve_classpath

jybot -d /tmp/ $@
