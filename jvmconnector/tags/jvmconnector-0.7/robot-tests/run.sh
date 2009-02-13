#!/bin/bash

dir=`dirname $0`
cd "$dir/.."
if [ ! -d 'target/test-classes' ]; then
  mvn -o test-compile
fi
if [ ! -d 'target/dependency' ]; then
  mvn -o dependency:copy-dependencies
fi
cd -

CLASSPATH=""
for i in $dir/../target/dependency/*.jar; do
	if [ -z "$CLASSPATH" ]; then
		CLASSPATH="$i"
	else
		CLASSPATH="$CLASSPATH:$i"
	fi
done

CLASSPATH="$dir/../target/classes:$dir/../target/test-classes:$CLASSPATH"
CLASSPATH=$CLASSPATH jybot -L TRACE -d /tmp/ $*
