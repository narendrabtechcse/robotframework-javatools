#!/bin/bash

cd ..
mvn -o test-compile
mvn -o dependency:copy-dependencies
cd -

CLASSPATH=""
for i in ../target/dependency/*.jar; do
	if [ -z "$CLASSPATH" ]; then
		CLASSPATH="$i"
	else
		CLASSPATH="$CLASSPATH:$i"
	fi
done

#CLASSPATH="$HOME/workspace/jvmconnector-release-0.2/target/jvmconnector-0.2.jar:$CLASSPATH:../target/classes:../target/test-classes"
CLASSPATH="$CLASSPATH:../target/classes:../target/test-classes"
CLASSPATH=$CLASSPATH jybot -d /tmp/ $*
