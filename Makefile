javadoc:
	mvn javadoc:javadoc
	
exec:
	mvn package

notest:
	mvn package -Dmaven.test.skip=true

%:
	@:
    
clean:
	rm -rf ./target
	rm -rf doc/javadoc
	rm -rf tree.log*

