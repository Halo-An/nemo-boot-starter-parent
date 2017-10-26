#!/bin/bash
SERVER=$(cd `dirname $0`;pwd)
DIR=$SERVER/build

mkdir -p $DIR/logs
mkdir -p $DIR/pid
mkdir -p $DIR/lib
echo "mkdir lib successfully"
rm -Rf $DIR/lib/*
echo "remove origin lib successfully"
rsync -vaP --exclude="*api*.jar" $SERVER/*/target/*.jar $DIR/lib
echo "copy new lib successfully"

#rm -Rf $DIR/bin
#echo "remove origin bin successfully"
#cp -R bin $DIR/bin
#echo "copy new bin successfully"
chmod +x $DIR/bin/*.sh