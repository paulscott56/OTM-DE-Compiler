@REM
@REM Copyright (C) 2014 OpenTravel Alliance (info@opentravel.org)
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM         http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

@echo off

set "SCRIPTDIR=%~dp0"

set "REPOSITORY_CONFIG=%SCRIPTDIR%conf\ota2-repository-config.xml"
set "LOG4J_CONFIG=%SCRIPTDIR%conf\log4j-manager.properties"

set "JAVA_OPTS=-Dota2.repository.config=%REPOSITORY_CONFIG% -Dlog4j.configuration=file:/%LOG4J_CONFIG%"

java.exe %JAVA_OPTS% -cp ./lib/* org.opentravel.schemacompiler.index.ShutdownIndexingService %*