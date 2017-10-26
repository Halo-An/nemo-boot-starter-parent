#!/bin/bash
FLAVOR=$2
SERVER=$(cd `dirname $0`;cd ..;pwd)
APP=$(basename $0 .sh)
MASTER=$(ifconfig | grep 10.28.148.140 | wc -L)

if [ "$FLAVOR" != "prod" ] || [ "$MASTER" != 0 ]
then
  echo "this is master，timer is running"
else
  echo "this is not master, timer is not running, i will exit"
  exit 0
fi

cd $SERVER
#
PID_FILE=$SERVER/pid/$APP-server.pid
JAVA_OPTS="-Xms64m -Xmx192m -Xss1024K -XX:PermSize=64m -XX:MaxPermSize=128m"
case "$FLAVOR" in
  prod)
    JAVA_OPTS="-server -Xms1024m -Xmx1024m -Xss1024k -XX:PermSize=128m -XX:MaxPermSize=128m -XX:+UseParallelOldGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$SERVER/logs/jvm -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:$SERVER/logs/jvm-heap-trace.log -XX:NewSize=384m -XX:MaxNewSize=384m"
    ;;
  test)
    ;;
  sandbox)
    ;;
  default)
    ;;
  *)
    echo "Usage: {prod|test|sandbox|default}" 
    ;;
esac

case "$1" in
  start)
    nohup java $JAVA_OPTS -jar $SERVER/lib/$APP*.jar --spring.profiles.active=$FLAVOR > $SERVER/logs/$APP-stdout.log 2>&1 &
    echo $! > $PID_FILE
    echo "start $APP successfully, pid:$!"
    ;;
  stop)
    kill `cat $PID_FILE`
    rm -rf $PID_FILE
    echo "stop $APP successfully" 
    ;;
  restart)
    kill `cat $PID_FILE`
    rm -rf $PID_FILE
    echo "stop $APP successfully" 
    
    nohup java $JAVA_OPTS -jar $SERVER/lib/$APP*.jar --spring.profiles.active=$FLAVOR > $SERVER/logs/$APP-stdout.log 2>&1 &
    echo $! > $PID_FILE
    echo "start $APP successfully, pid:$!"
    ;;
  *)
    echo "Usage: {start|stop|restart}" 
    ;;
esac
exit 0