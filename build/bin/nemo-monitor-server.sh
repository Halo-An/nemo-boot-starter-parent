#!/bin/bash
SERVER=$(cd `dirname $0`;cd ..;pwd)
APP=$(basename $0 .sh)
MAX_CHECK_NUM=120
JAVA_OPTS="-Xms64m -Xmx192m -Xss1024K -XX:PermSize=64m -XX:MaxPermSize=128m"
APP_OPTS="--spring.profiles.active=$FLAVOR"
DATE=$(date "+%Y-%m-%d")
PID_FILE=$SERVER/pid/$APP-server.pid

check(){
  for((i=0;i<$MAX_CHECK_NUM;i++))
  do
    echo -e ".\c"
    PORT=$(netstat -tunlp | grep $PID/java | grep $FP | awk '{printf $4}' | cut -d: -f2)
    if [[ $PORT != "" ]]; then
      RESULT=$(curl -s http://localhost:$PORT)
##      echo "$PORT,$RESULT"
      if [[ $RESULT != "" ]]; then
        echo "" 
        echo "$APP started，pid:$PID"
        exit 0
      fi
    fi
    sleep 1
  done

  echo "" 
  echo "$APP start failed"
  exit 1
}

cd $SERVER
case "$FLAVOR" in
  prod) 
    echo "FLAVOR is $FLAVOR"
    FP=20
    JAVA_OPTS="-server -Xms1024m -Xmx1024m -Xss1024k -XX:PermSize=128m -XX:MaxPermSize=128m -XX:+UseParallelOldGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$SERVER/logs/jvm -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:$SERVER/logs/jvm-heap-trace.log -XX:NewSize=384m -XX:MaxNewSize=384m"
    ;;
  test)
    echo "FLAVOR is $FLAVOR"
    FP=22
    ;;
  sandbox)
    echo "FLAVOR is $FLAVOR"
    FP=23
    ;;
  default)
    echo "FLAVOR is $FLAVOR"
	FP=21
    ;;
  *)
    echo -e "\e[1;31m"
    echo "environment variable can not be empty: 'FLAVOR', i will exit, please Usage: {default|test|sandbox|prod}"
    echo -e "\e[0m"
    exit 1 
    ;;
esac

if [ $ZONE ];then
  APP_OPTS="$APP_OPTS --zone=$ZONE"
  echo "ZONE is $ZONE"
else
  echo -e "\e[1;31m"
  echo  -e "environment variable can not be empty: 'ZONE', i will exit, please Usage: {zone-migu-slave-one|zone-migu-slave-two|zone-migu-slave-three}"
  echo -e "\e[0m"
  exit 1
fi

case "$1" in
  start)
    nohup java $JAVA_OPTS -jar $SERVER/lib/$APP*.jar $APP_OPTS >> $SERVER/logs/$APP-stdout-$DATE.log 2>&1 &
    PID=$!
    echo $PID > $PID_FILE
    echo -e "$APP starting \c"
    check
    ;;
  stop)
    kill -9 `cat $PID_FILE`
    rm -rf $PID_FILE
    echo "$APP stoped" 
    exit 0
    ;;
  restart)
    kill -9 `cat $PID_FILE`
    rm -rf $PID_FILE
    echo "$APP stoped" 
    sleep 5
    nohup java $JAVA_OPTS -jar $SERVER/lib/$APP*.jar $APP_OPTS >> $SERVER/logs/$APP-stdout-$DATE.log 2>&1 &
    PID=$!
    echo $PID > $PID_FILE
    echo -e "$APP starting \c"
    check
    ;;
  *)
    echo  -e "Usage: {start|stop|restart}" 
    exit 1
    ;;
esac