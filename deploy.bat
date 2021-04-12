@echo off
chcp 1251
call gradlew.bat bootJar
copy /Y .\build\libs\antiKriptonit.jar .\..\antiKriptonit.jar

if exist .\..\application.properties (echo 'application.properties уже задеплоен') else (copy /Y .\internal_properties\application.properties .\..\application.properties)

cd ..

java -jar .\antiKriptonit.jar