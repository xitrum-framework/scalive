@echo off

set JAVA_OPTS=-Djava.awt.headless=true

set ROOT_DIR=%~dp0
cd "%ROOT_DIR%"

set CLASS_PATH="%ROOT_DIR%\*;."

java %JAVA_OPTS% -cp %CLASS_PATH% scalive.client.AgentLoader %ROOT_DIR% %*
