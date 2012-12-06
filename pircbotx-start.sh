#
# Copyright (C) 2010-2011 Leon Blakey <lord.quackstar at gmail.com>
#
# This file is part of PircBotX.
#
# PircBotX is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# PircBotX is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with PircBotX. If not, see <http://www.gnu.org/licenses/>.
#

# This is the script that Jenkins uses to restart the TheLQ-PircBotX bot. This 
# works better than start-stop-daemon which doesn't like that there are 2 services
# running with the Java command: Jenkins and TheLQ-PircBotX. 
# 
# Jenkins calls the script like this (dos2unix is used as this script might be edited on Windows)
# #!/bin/bash
# BUILD_ID=dontKillMe
# cp /var/lib/jenkins/jobs/TheLQ-PircBotX/workspace/target/pircbotx-standalone.jar /var/lib/jenkins/thelq-pircbotx/
# dos2unix pircbotx-start.sh
# ./pircbotx-start.sh
#
# This should not be considered the only or the best way to "daemonize" a bot. 
# There are probably better alternatives, but this was the only script that was 
# found to work 

ROOT=/var/lib/jenkins/thelq-pircbotx
PIDFILE=$ROOT/pircbotx.pid

# Kill it if its already up
if [ -f $PIDFILE ]; then
    PID=$(cat $PIDFILE);
    echo "Stopping TheLQ-PircBotx at PID $PID ..."
    kill $PID;
    echo "TheLQ-PircBotx stopped ..."
    rm $PIDFILE
fi

# Start
nohup java -jar $ROOT/pircbotx-standalone.jar $ROOT/ > $ROOT/pircbotx.log 2>&1 &
NEWPID=$!
echo $NEWPID > $PIDFILE
echo "TheLQ-PircBotX started at $NEWPID ..."