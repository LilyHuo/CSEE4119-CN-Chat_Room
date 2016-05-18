JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	ValidUser.java \
	User.java \
	Server.java \
	Client.java
	

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class