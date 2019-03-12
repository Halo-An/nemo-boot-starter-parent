#!/bin/bash
SERVER=$(cd `dirname $0`;pwd)
DIR=$SERVER/build

FLAVOR=$1

for filename in $( ls $DIR/bin |grep "sh"); 
do 
  sh "$DIR/bin/"$filename restart $FLAVOR; 
  RESULT=$?
  echo $RESULT
  if [[ $RESULT != "0" ]]; then
      echo "application start failed"
      exit 1
  fi
  
done
echo "restart successfully"