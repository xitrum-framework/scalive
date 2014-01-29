# You may need to customize memory config below to optimize for your environment
set JAVA_OPTS=-Xmx1024m -Xms256m -XX:MaxPermSize=128m -XX:+HeapDumpOnOutOfMemoryError -XX:+AggressiveOpts -XX:+OptimizeStringConcat -XX:+UseFastAccessorMethods -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSClassUnloadingEnabled -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=1 -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -Djava.awt.headless=true -server -Dxitrum.mode=production

set ROOT_DIR=%~dp0..
cd "%$ROOT_DIR%"

# Include ROOT_DIR to find this pid easier later, when
# starting multiple processes from different directories
set CLASS_PATH="%ROOT_DIR%\lib\*;config"

java %JAVA_OPTS% -cp %CLASS_PATH% %*
