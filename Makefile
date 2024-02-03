LT:=lsd  -hF --tree --group-dirs=first
JARNAME=cda

all: run

run:
	mvn package exec:java

log:
	bat files/res/log/cda-err.log

show_pdf:
	@$(LT) files/res/md/ files/res/pdf

clean:
	mvn clean

# call maven package and rename the <jarName>-with-dependencies.jar to <jarnName>.jar
# i.e. when creating the fatjar mvn keep the old jar (cda.jar) and calls the fatjar differently (cda-jar-with-dependencies.jar)
# this is a workaround to keep the jar name consistent and remove the old jar
package:
	mvn package
	rm target/$(JARNAME).jar
	mv target/$(JARNAME)-jar-with-dependencies.jar target/$(JARNAME).jar
