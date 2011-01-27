#!/bin/sh

export CLASSPATH=jar/wt2007www.jar:jar/java-getopt-1.0.13.jar:jar/csv.jar
MAIN=jp.ac.titech.is.socialnet.clustering.Main
JAVA="java -Xmx1G -classpath $CLASSPATH $MAIN"

$JAVA $*
