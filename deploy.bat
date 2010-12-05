@REM
@REM Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
@REM
@REM This file is part of pircbotx.
@REM
@REM pircbotx is free software: you can redistribute it and/or modify
@REM it under the terms of the GNU General Public License as published by
@REM the Free Software Foundation, either version 3 of the License, or
@REM (at your option) any later version.
@REM
@REM pircbotx is distributed in the hope that it will be useful,
@REM but WITHOUT ANY WARRANTY; without even the implied warranty of
@REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
@REM GNU General Public License for more details.
@REM
@REM You should have received a copy of the GNU General Public License
@REM along with pircbotx.  If not, see <http://www.gnu.org/licenses/>.
@REM

@echo off
echo Lazy auto deply since I can't remember commands
echo.
echo Please select option
echo  1) Deploy SNAPSHOT
echo  2) Deploy RELEASE
echo .
set /p answer=Run number: 

if %answer%==1 (
	mvn clean install javadoc:jar source:jar deploy

)
if %answer%==2 (
	mvn release:clean release:prepare release:perform
)

pause

