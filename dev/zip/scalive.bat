# You may need to customize memory config below to optimize for your environment
set JAVA_OPTS=-Djava.awt.headless=true

set ROOT_DIR=%~dp0
cd "%$ROOT_DIR%"

set CLASS_PATH="%ROOT_DIR%\2.10.3\*;%ROOT_DIR%\*"

java %JAVA_OPTS% -cp %CLASS_PATH% scalive.AgentLoader %$ROOT_DIR% %*
