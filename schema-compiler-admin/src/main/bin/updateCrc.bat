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
FOR /R ./lib %%a in (*.jar) DO CALL :AddToPath %%a
java -cp %CLASSPATH% org.opentravel.schemacompiler.admin.LibraryCrcManager %*
GOTO :EOF

:AddToPath
SET CLASSPATH=%1;%CLASSPATH%
GOTO :EOF