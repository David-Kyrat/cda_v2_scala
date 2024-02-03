LT:=lsd  -hF --tree --group-dirs=first

all: run

run:
	mvn package exec:java

log:
	bat files/res/log/cda-err.log

show_pdf:
	@$(LT) files/res/md/ files/res/pdf
