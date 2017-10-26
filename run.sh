#!/bin/bash
SERVER=$(cd `dirname $0`;pwd)
DIR=$SERVER/build

for filename in $( ls $DIR/bin |grep "sh"); do sh "$DIR/bin/"$filename restart $1; done
echo "restart successfully"