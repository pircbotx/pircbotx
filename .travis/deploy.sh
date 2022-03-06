#!/bin/bash
#
# Copyright (C) 2010-2022 The PircBotX Project Authors
#
# This file is part of PircBotX.
#
# PircBotX is free software: you can redistribute it and/or modify it under the
# terms of the GNU General Public License as published by the Free Software
# Foundation, either version 3 of the License, or (at your option) any later
# version.
#
# PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
# WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
# A PARTICULAR PURPOSE. See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along with
# PircBotX. If not, see <http://www.gnu.org/licenses/>.
#

set -e
set -x

if [[ $TRAVIS_PULL_REQUEST != "false" || !($TRAVIS_BRANCH = "master" || $TRAVIS_BRANCH = "stable") ]]; then
	echo exiting due to invalid state
	exit 0
fi

mvn source:jar deploy
mvn -Pcomplete-build site #site-deploy

#everything is built however build may of changed repo
git checkout -- .

# switch to gh-pages where site is stored, was not downloaded by travis though
git fetch origin gh-pages:gh-pages
git checkout gh-pages

if [ "$TRAVIS_BRANCH"  == "stable" ]; then
	SNAP_FOLDER=snapshot-stable
else
	# on master
	SNAP_FOLDER=snapshot
fi

rm -rf $SNAP_FOLDER
mkdir $SNAP_FOLDER
mv target/site/* $SNAP_FOLDER/
git add $SNAP_FOLDER
git config user.name "Leon Blakey"
git config user.email "leon.m.blakey@gmail.com"
git commit -m "Autobuild of $SNAP_FOLDER for $TRAVIS_BRANCH"
git push https://$DEPLOY_PAGES_USERNAME:$DEPLOY_PAGES_PASSWORD@github.com/TheLQ/pircbotx.git
