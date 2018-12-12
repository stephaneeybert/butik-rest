# butik-rest
A job test assignment project REST layer

To only build the project
```
mvn clean install
```

To build and run some integration tests
```
mvn clean install -Denv="test" -Ddb="h2"
mvn clean install -Denv="test" -Ddb="mysql"
mvn clean install -Denv="test" -Ddb="postgresql"
```

The data layer is compatible with MySQL and Oracle
```
-Denv="prod" (an empty env string is considered as prod)
-Denv="test"
-Ddb="h2"
-Ddb="mysql"
-Ddb="postgresql"
-Ddb="oracle"
```

Running with the debugger
```
mvn clean install -Denv="test" -Ddb="h2" -Dmaven.surefire.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005 -Xnoagent -Djava.compiler=NONE"
```

Swagger 

The API (is) was exposed at http://localhost:8080/api/swagger-ui.html
BREAKING ! Swagger breaks Spring REST. Issue opened at https://github.com/springfox/springfox/issues/2623
The Swagger configuration is removed from the project.

Some example API requests
```
curl -i -H "Accept:application/json" -H "Content-Type: application/json" "http://localhost:8080/api/products/" -X GET
curl -i -H "Accept:application/json" -H "Content-Type: application/json" "http://localhost:8080/api/orders/" -X GET
```
