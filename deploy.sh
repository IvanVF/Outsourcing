#!/bin/bash

./gradlew bootJar
cp ./build/libs/antiKriptonit.jar ./../antiKriptonit.jar

if [ -f ./../application.properties ]; then 
	echo 'application.properties уже задеплоен'
else
	cp ./internal_properties/application.properties ./../application.properties
fi

cd ..

java -jar ./antiKriptonit.jar