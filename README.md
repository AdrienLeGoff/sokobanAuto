# sokobanAuto

## Compilation Sokoban : 
mvn compile

## Execution Sokoban : 
java --add-opens java.base/java.lang=ALL-UNNAMED       -server -Xms2048m -Xmx2048m       -cp "$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q):target/test-classes/:target/classes"       sokoban.SokobanMain

### Remarque
Une erreur arrive parfois à l'intérieur du Parser parfois et on n'a pas réussi à trouver une solution.
L'erreur est la suivante :
java.lang.NoClassDefFoundError : Could not initialize class org.apache.logging.log4j.util.PropertiesUtil
et en remontant les appels on peut voir que celà sort du parser pddl.
