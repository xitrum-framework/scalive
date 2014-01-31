# You may need to customize memory config below to optimize for your environment
set JAVA_OPTS=-Djava.awt.headless=true

set ROOT_DIR=%~dp0
cd "%$ROOT_DIR%"

java %JAVA_OPTS% -cp %ROOT_DIR% scalive.AgentLoader %$ROOT_DIR% %*
