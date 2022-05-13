# sokobanAuto

## Compilation Sokoban : 
mvn compile

## Execution Sokoban : 
java --add-opens java.base/java.lang=ALL-UNNAMED       -server -Xms2048m -Xmx2048m       -cp "$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q):target/test-classes/:target/classes"       sokoban.SokobanMain
