#!/usr/bin/env bash

usage() {
    cat <<EOF
Usage: coin_service.sh (start|stop|restart) <args...>

where argument is one of:
    -force (accepted only with stop command): Decides whether to stop the Coin Service forcefully if not stopped by normal shutdown
EOF
}

if [ $# -lt 1 ]
then
    echo "Error: no enough arguments provided."
    usage
    exit 1
fi

BINDIR=`dirname "$0"`
COIN_HOME=`cd $BINDIR/..;pwd`
COIN_STOP_TIMEOUT=${COIN_STOP_TIMEOUT:-30}
COIN_PID_DIR=${COIN_PID_DIR:-$COIN_HOME/bin}
COIN_PID_FILE=$COIN_PID_DIR/coin_service.pid
COIN_LOG_DIR=${COIN_LOG_DIR:-"$COIN_HOME/logs"}
COIN_LOG=$COIN_LOG_DIR/coin_service.log
COIN_LOG_OUT=$COIN_LOG_DIR/coin_service.out
COIN_CONF=${COIN_CONF:-$COIN_HOME/conf/coin_service.conf}
COIN_LOG_CONF=${COIN_LOG_CONF:-$COIN_HOME/conf/log4j.properties}
COIN_LOG_LEVEL=${COIN_LOG_LEVEL:-INFO}
COIN_LOG_APPENDER=${COIN_LOG_APPENDER:-ROLLINGFILE}

# exclude tests jar
RELEASE_JAR=`ls $COIN_HOME/coin-*.jar 2> /dev/null | grep -v tests | tail -1`
if [ $? == 0 ]; then
    COIN_JAR=$RELEASE_JAR
fi

# exclude tests jar
BUILT_JAR=`ls $COIN_HOME/target/coin-*.jar 2> /dev/null | grep -v tests | tail -1`
if [ $? != 0 ] && [ ! -e "$COIN_JAR" ]; then
    echo "\nCouldn't find coin service jar.";
    echo "Make sure you've run 'mvn package'\n";
    exit 1;
elif [ -e "$BUILT_JAR" ]; then
    COIN_JAR=$BUILT_JAR
fi

mkdir -p "$COIN_LOG_DIR"

add_maven_deps_to_classpath() {
    MVN="mvn"
    if [ "$MAVEN_HOME" != "" ]; then
        MVN=${MAVEN_HOME}/bin/mvn
    fi

    # Need to generate classpath from maven pom. This is costly so generate it
    # and cache it. Save the file into our target dir so a mvn clean will get
    # clean it up and force us create a new one.
    f="${COIN_HOME}/target/cached_classpath.txt"
    if [ ! -f "${f}" ]
    then
        ${MVN} -f "${COIN_HOME}/pom.xml" dependency:build-classpath -Dmdep.outputFile="${f}" &> /dev/null
    fi
    COIN_CLASSPATH=${CLASSPATH}:`cat "${f}"`
}

rotate_out_log ()
{
    log=$1;
    num=5;
    if [ -n "$2" ]; then
       num=$2
    fi
    if [ -f "$log" ]; then # rotate logs
        while [ $num -gt 1 ]; do
            prev=`expr $num - 1`
            [ -f "$log.$prev" ] && mv "$log.$prev" "$log.$num"
            num=$prev
        done
        mv "$log" "$log.$num";
    fi
}

if [ -d "$COIN_HOME/lib" ]; then
    for i in $COIN_HOME/lib/*.jar; do
        COIN_CLASSPATH=$COIN_CLASSPATH:$i
    done
else
    add_maven_deps_to_classpath
fi

JMXREMOTE_PORT=${JMXREMOTE_PORT:-8007}
JPDA_PORT=${JPDA_PORT:-1099}
JPDA_SUSPEND=${JPDA_SUSPEND:-n}

JMX_ARGS="-Dcom.sun.management.jmxremote.port=${JMXREMOTE_PORT} -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"

COIN_CLASSPATH="$COIN_JAR:$COIN_CLASSPATH"
COIN_CLASSPATH="`dirname $COIN_CONF`:$COIN_CLASSPATH"
LOG_SETTING="-Dlog4j.configuration=log4j.properties -Dcoin.log.dir=$COIN_LOG_DIR -Dcoin.log.file=coin_service.log -Dcoin.log.level=$COIN_LOG_LEVEL -Dcoin.log.appender=$COIN_LOG_APPENDER"
JPDA_SETTING="-Xdebug -Xrunjdwp:transport=dt_socket,address=${JPDA_PORT},server=y,suspend=${JPDA_SUSPEND}"
OPTS="$OPTS $COIN_EXTRA_OPTS $LOG_SETTING $JPDA_SETTING $JMX_ARGS"

command=$1
shift

case $command in
    start)
        if [ -f $COIN_PID_FILE ]; then
            if kill -0 `cat $COIN_PID_FILE` > /dev/null 2>&1; then
                echo "Coin service already running as process `cat $COIN_PID_FILE`."
                exit 1
            fi
        fi
        rotate_out_log $COIN_LOG_OUT
        echo "Starting coin service, logging to $COIN_LOG"
        cd "$COIN_HOME"
        nohup java -cp $COIN_CLASSPATH $OPTS coin.Application $COIN_CONF > $COIN_LOG_OUT 2>&1 < /dev/null &
        if [ $? -eq 0 ]
        then
            if echo -n $! > "$COIN_PID_FILE"
            then
                sleep 1
                echo "Successfully started coin service"
            else
                echo "Failed to write coin service pid"
                exit 1
            fi
        else
            echo "Failed to start coin service"
            exit 1
        fi
        ;;
    stop)
        if [ -f $COIN_PID_FILE ]; then
            TARGET_PID=`cat $COIN_PID_FILE`
            if kill -0 $TARGET_PID > /dev/null 2>&1; then
                echo "Shutting down coin service..."
                kill $TARGET_PID
                count=0
                while ps -p $TARGET_PID > /dev/null;
                do
                    echo "Shutdown is in progress... Please wait..."
                    sleep 1
                    count=`expr $count + 1`

                    if [ "$count" = "$COIN_STOP_TIMEOUT" ]; then
                        break
                    fi
                 done

                if [ "$count" != "$COIN_STOP_TIMEOUT" ]; then
                    echo "Shutdown completed."
                    exit 0
                fi

                if kill -0 $TARGET_PID > /dev/null 2>&1; then
                    $JAVA_HOME/bin/jstack $TARGET_PID > $COIN_LOG_OUT
                    echo "Thread dumps are taken for analysis at $COIN_LOG_OUT"
                    if [ "$1" == "-force" ]
                    then
                        echo "Forcefully stopping coin service"
                        kill -9 $TARGET_PID >/dev/null 2>&1
                        echo "Successfully stopped the process"
                    else
                        echo "WARNNING: Coin service is not stopped completely."
                        exit 1
                    fi
                fi
            else
                echo "No coin service to stop"
            fi
            rm $COIN_PID_FILE
        else
            echo "No coin service to stop"
        fi
        ;;
    restart)
        shift
        "$0" stop ${@}
        sleep 3
        "$0" start ${@}
        ;;
    *)
        usage
        exit 1
        ;;
esac
