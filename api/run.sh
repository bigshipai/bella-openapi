#!/bin/bash

set -e

JAVA_COMMENT="java"

# JVM 参数
JAVA_OPTS="$JAVA_OPTS -server -Xms2048m -Xmx2048m"
JAVA_OPTS="$JAVA_OPTS -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m"
JAVA_OPTS="$JAVA_OPTS -XX:MaxDirectMemorySize=1024m"
# 确保 System.gc() 可以被响应，用于堆外内存回收（不要添加 -XX:+DisableExplicitGC）
JAVA_OPTS="$JAVA_OPTS -Dlogging.path=${MATRIX_APPLOGS_DIR}"
JAVA_OPTS="$JAVA_OPTS -Dsun.net.inetaddr.ttl=1 -Dsun.net.inetaddr.negative.ttl=1"
JAVA_OPTS="$JAVA_OPTS -Xlog:gc*:file=${MATRIX_ACCESSLOGS_DIR}/gc-%t.log"

# GC 参数
GC_OPTS="$GC_OPTS -XX:+UseG1GC -XX:G1HeapRegionSize=2m -XX:MaxGCPauseMillis=500 -XX:InitiatingHeapOccupancyPercent=40 -XX:G1ReservePercent=10"
GC_OPTS="$GC_OPTS -XX:+ParallelRefProcEnabled -XX:ParallelGCThreads=4 -XX:ConcGCThreads=2 -XX:+ExplicitGCInvokesConcurrent"
GC_OPTS="$GC_OPTS -XX:+PrintPromotionFailure -XX:+PrintGCCause -XX:AutoBoxCacheMax=20000 -Djava.security.egd=file:/dev/./urandom"
GC_OPTS="$GC_OPTS -XX:+PrintCommandLineFlags -XX:-OmitStackTraceInFastThrow"
# Java 17 强封装：允许 Groovy 等框架的反射访问
GC_OPTS="$GC_OPTS --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED"

# 测试、预览开启DEBUG功能，生产开启JMX
if [ "$ENVTYPE" != "" ]; then
    JAVA_OPTS="$JAVA_OPTS -Djavax.net.debug=ssl -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:$DEBUGPORT"
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=$JMXPORT -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=$HOSTNAME"
else
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=$JMXPORT -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=$HOSTNAME"
fi

# 待启动jar
JARFILE=`find "$MATRIX_CODE_DIR/lib" -name *.jar`

# 启动应用
echo "$JAVA_COMMENT $JAVA_OPTS $GC_OPTS -jar $JARFILE ${USER_ARGS}"
exec $JAVA_COMMENT $JAVA_OPTS $GC_OPTS -jar $JARFILE ${USER_ARGS}
