#!/bin/bash 
target=`pwd`/target

set -o verbose
rm -rf target
mvn assembly:assembly
jar=`echo $target/jvmconnector-*-jar-with-dependencies.jar`
jarjar=${jar%.*}-jarjar.jar
java -jar ${target}/../release/jarjar-1.0.jar process ${target}/../release/jarjar_rules.txt $jar $jarjar
unzip -d $target $jar META-INF/MANIFEST.MF

tmpdir=$target/tmp
mkdir $tmpdir
unzip -d $tmpdir $jarjar
rmic -verbose -classpath $tmpdir -d $tmpdir org.robotframework.org.springframework.remoting.rmi.RmiInvocationWrapper
rmic -verbose -iiop -always -classpath $tmpdir -d $tmpdir org.robotframework.org.springframework.remoting.rmi.RmiInvocationWrapper
rm $jarjar
jar -cfm $jarjar $target/META-INF/MANIFEST.MF -C $tmpdir .
mv $jarjar $jar
rm -rf $tmpdir
rm -rf $target/META-INF
