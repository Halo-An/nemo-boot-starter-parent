#!/bin/bash
SERVER=$(cd `dirname $0`;cd ..;pwd)
APP=$(basename $0 .sh)

cd $SERVER

JAVA_OPTS="-Xms64m -Xmx192m -Xss1024K -XX:PermSize=64m -XX:MaxPermSize=128m"
case "$FLAVOR" in
  prod)
    echo "FLAVOR is $FLAVOR"
    JAVA_OPTS="-server -Xms1024m -Xmx1024m -Xss1024k -XX:PermSize=128m -XX:MaxPermSize=128m -XX:+UseParallelOldGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$SERVER/logs/jvm -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:$SERVER/logs/jvm-heap-trace.log -XX:NewSize=384m -XX:MaxNewSize=384m"
    ;;
  test)
    echo "FLAVOR is $FLAVOR"
    ;;
  sandbox)
    echo "FLAVOR is $FLAVOR"
    ;;
  default)
    echo "FLAVOR is $FLAVOR"
    ;;
  *)
    echo -e "\e[1;31m"
    echo "environment variable can not be empty: 'FLAVOR', i will exit, please Usage: {default|test|sandbox|prod}"
    echo -e "\e[0m"
    exit 1 
    ;;
esac

APP_OPTS="--spring.profiles.active=$FLAVOR"
if [ $ZONE ];then
  APP_OPTS="--spring.profiles.active=$FLAVOR --zone=$ZONE"
  echo "ZONE is $ZONE"
else
  echo -e "\e[1;31m"
  echo  -e "environment variable can not be empty: 'ZONE', i will exit, please Usage: {zone-migu-slave-one|zone-migu-slave-two|zone-migu-slave-three}"
  echo -e "\e[0m"
  exit 1
fi

PID_FILE=$SERVER/pid/$APP-server.pid
case "$1" in
  start)
    nohup java $JAVA_OPTS -jar $SERVER/lib/$APP*.jar $APP_OPTS > $SERVER/logs/$APP-stdout.log 2>&1 &
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
    
    nohup java $JAVA_OPTS -jar $SERVER/lib/$APP*.jar $APP_OPTS > $SERVER/logs/$APP-stdout.log 2>&1 &
    echo $! > $PID_FILE
    echo "start $APP successfully, pid:$!"
    ;;
  *)
    echo  -e "Usage: {start|stop|restart}" 
    ;;
esac
exit 0