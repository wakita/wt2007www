CLASSPATH=jar/wt2007www.jar:jar/java-getopt-1.0.13.jar:jar/csv.jar
DATA=karate
ALG=HN
JAVA=java -classpath $(CLASSPATH) jp.ac.titech.is.socialnet.clustering.Main

clean:
	rm -f *.idx *.db

build:
	rm -rf jp wt2007www.jar
	mkdir -p jp/ac/titech/is
	ln -s $(HOME)/work/eclipse/wakitalab/bin/jp/ac/titech/is/socialnet jp/ac/titech/is/socialnet
	jar cf jar/wt2007www.jar jp
	rm -rf jp

pack:
	./pack.sh

help:
	$(JAVA) --help

am2sdb:
	$(JAVA) --command am2sdb --input karate

csv2sdb:
	$(JAVA) --command csv2sdb --input karate

em2sdb:
	$(JAVA) --command em2sdb --input karate

sym2sdb:
	$(JAVA) --command sym2sdb --input karate

clusters:
	$(JAVA) --command analyze --input karate

clusters-HE:
	$(JAVA) --command analyze --input karate --algorithm HE

view-graph:
	$(JAVA) --command view-graph --input karate

view-history:
	$(JAVA) --command view-history --input karate

view-clusters:
	$(JAVA) --command view-clusters --input karate

view-clusters-symbolic:
	$(JAVA) --command view-clusters --input karate --symbolic
