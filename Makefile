# cda (root package name) stands for Course-Description-Automation
.PHONY: package all clean __clean__ log cat_log

LIB2 = /home/noahl/.sdkman/candidates/scala/current/lib/scala3-library_3-3.3.1.jar:/home/noahl/.sdkman/candidates/scala/current/lib/scala-library-2.13.10.jar:/home/noahl/tests/scalafx/fli_scala/lib/core-12.62.7.jar:/home/noahl/tests/scalafx/fli_scala/lib/jvm-driver-9.4.3.jar:/home/noahl/tests/scalafx/fli_scala/lib/jfoenix-19.0.1.jar:/home/noahl/.m2/repository/com/google/code/gson/gson/2.8.5/gson-2.8.5.jar:/home/noahl/.m2/repository/org/scala-lang/modules/scala-parallel-collections_3/1.0.4/scala-parallel-collections_3-1.0.4.jar

LIB = lib/scala3-library_3-3.3.1.jar:lib/scala3-compiler_3-3.3.1.jar:lib/gson-2.10.1.jar:lib/scala-parallel-collections_3-1.0.4.jar:lib/scala-library-2.13.10.jar:lib/scala3-interfaces-3.3.1.jar:lib/tasty-core_3-3.3.1.jar:lib/scala-asm-9.5.0-scala-1.jar:lib/compiler-interface-1.3.5.jar:lib/jline-reader-3.19.0.jar:lib/jline-terminal-3.19.0.jar:lib/jline-terminal-jna-3.19.0.jar:lib/protobuf-java-3.7.0.jar:lib/util-interface-1.3.0.jar:lib/jna-5.3.1.jar:lib/scala3-library_3-3.3.1.jar:lib/scala-library-2.13.10.jar:lib/core-12.62.7.jar:lib/jvm-driver-9.4.3.jar:lib/jfoenix-19.0.1.jar:lib/gson-2.8.5.jar:lib/scala-parallel-collections_3-1.0.4.jar

# scala_build dir
SB_DIR = fli_scala_72500b76e2-f0c4a5fde9
OUT = .scala_build/$(SB_DIR)/classes/main/

CP = "$(LIB)"
RES = --resource-dir "./files"

GUI_MAIN = cda.view.jfxuserform.Main
MODEL_MAIN = cda.model.Main
MAIN = cda.App
CLI_ARGS = files/res/abbrev.tsv
CP_FILE = classpath.txt
# HINT: To see the different main classes that can be run use the --interactive switch (target `int`)
# 		=> will prompt user to select a main class to run

all: build app

run: app
	
# Main App
app:
	@scala-cli . -cp $(CP) $(RES) --main-class $(MAIN) -- $(N)

# JavaFx User Form
gui:
	@scala-cli . -cp $(CP) $(RES) --main-class $(GUI_MAIN) -- $(CLI_ARGS)

# Interactive
int: 
	@scala-cli . -cp $(CP) $(RES) --interactive


# HK: No need to pass "--jar $(LIB)" if "project.scala" is correctly configured at root of project (i.e. same place as build.sbt)
build:
	scala-cli compile . -deprecation --with-compiler  $(RES) -d out

# Build and use switch to tell scala-cli to print the whole classpath, so that we can run the
# program with java by just specifying "-cp $(cat classpath.txt)"
build_extract_classpath:
	@scala-cli compile . --with-compiler --jar $(LIB) $(RES) -d out  -p > $(CP_FILE)
	

# Doesnt work but ideally we should cat classpath.txt file generated par build_extract_classpath
##build_extract_classpath

java: build
	java -cp $(LIB):out $(MAIN)

#java -cp $(shell cat classpath.txt) $(MAIN)
#java -cp $(shell cat $(CLASSPATH_FILE)) $(MAIN)
#test:
#	java -cp ".scala-build/$(SB_DIR)/classes/main/:$(LIB)" cda.App verbose

# package: build
# 	cd out && jar cfvm ../cda.jar ../META-INF/MANIFEST.MF cda/* jfxuserform/* ../lib/*  ../files/* && cd ..
	# cd out && jar cfvm ../cda.jar ../META-INF/MANIFEST.MF cda/* jfxuserform/* && cd ..
	# mkdir -p jar && cp files jar -rf && cp lib jar -rf && mv cda.jar jar

# Project.scala handles dependencies
package2: build
	scala-cli package . --with-compiler --main-class cda.App -f --assembly

fat_jar: build
	./package_fat_jar

package: fat_jar

# scala-cli package . --dep "com.google.code.gson:gson:2.10.1","org.scala-lang.modules:scala-parallel-collections_3:1.0.4" --jar "/home/noahl/.sdkman/candidates/scala/current/lib/scala3-library_3-3.3.1.jar:/home/noahl/.sdkman/candidates/scala/current/lib/scala-library-2.13.10.jar:/home/noahl/tests/scalafx/fli_scala/lib/core-12.62.7.jar:/home/noahl/tests/scalafx/fli_scala/lib/jvm-driver-9.4.3.jar:/home/noahl/tests/scalafx/fli_scala/lib/jfoenix-19.0.1.jar" --main-class cda.App -f 

show_pdf_dir:
	ls "files/res/pdf"

cat_log:
	cat "files/res/log/cda-err.log"

log: cat_log

# jar cant access libraries from inside it
#cd out && jar cfvm ../Output.jar ../META-INF/MANIFEST.MF cda/* jfxuserform/* ../lib/*  ../files/* && cd ..

# scala-cli --power package . -cp $(LIB2) $(RES) --main-class $(MAIN) -f -o fli_scala.jar

clean:
	-@ make __clean__ -s

__clean__:
	-@rm -f files/res/pdf/*.pdf files/res/md/*.md files/res/log/*.log &> /dev/null
#jar cvfm MyFatJar.jar  META-INF/MANIFEST.MF -C out/ . -C lib/ .
