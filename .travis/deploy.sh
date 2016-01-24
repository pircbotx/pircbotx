#!/bin/bash
#
# Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
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

set +x
set +e
mvn source:jar #deploy
mvn -Pcomplete-build site #site-deploy

#everything is built however build may of changed repo
git checkout master

#Seriously git?! git checkout, git reset, git pull, all with gh-pages, origin/gh-pages, remotes/origin, gh-pages?
#Nope, obviously you do
git fetch origin -f gh-pages:gh-pages
git checkout gh-pages
#...
rm -rf snapshot/
mv target/site/* snapshot/
git add snapshot
git config user.name "Leon Blakey"
git config user.email "leon.m.blakey@gmail.com"
git commit -m "Autobuild of maven site by Travis CI"
#git push https://$DEPLOY_PAGES_USERNAME:$DEPLOY_PAGES_PASSWORD@github.com/TheLQ/pircbotx.git
