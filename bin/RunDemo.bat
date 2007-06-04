@echo off

REM **************************************************
REM ** Make sure we use the correct J2SE version!   **
REM ** Uncomment the PATH line in case of trouble   **
REM **************************************************

REM set PATH=C:\j2sdk1.4.2_11\bin;.;%PATH%

set CLASSPATH=.

REM ******************
REM   Core Library
REM ******************
REM This will get the versioned pentaho-meta.jar
FOR %%F IN (lib\*.jar) DO call :addcp %%F
REM set CLASSPATH=%CLASSPATH%;lib\pentaho-meta.jar

REM **********************
REM   External Libraries
REM **********************

REM Loop the libext directory and add the classpath.
REM The following command would only add the last jar: FOR %%F IN (libext\*.jar) DO call set CLASSPATH=%CLASSPATH%;%%F
REM So the circumvention with a subroutine solves this ;-)

FOR %%F IN (libext\*.jar) DO call :addcp %%F
FOR %%F IN (libext\commons\*.jar) DO call :addcp %%F
FOR %%F IN (libext\JDBC\*.jar) DO call :addcp %%F
FOR %%F IN (libext\meta\*.jar) DO call :addcp %%F
FOR %%F IN (libext\misc\*.jar) DO call :addcp %%F
FOR %%F IN (libext\pentaho\*.jar) DO call :addcp %%F

goto extlibe

:addcp
set CLASSPATH=%CLASSPATH%;%1
goto :eof

:extlibe

REM *****************
REM   SWT Libraries
REM *****************

set CLASSPATH=%CLASSPATH%;libswt\runtime.jar
set CLASSPATH=%CLASSPATH%;libswt\jface.jar
set CLASSPATH=%CLASSPATH%;libswt\common.jar
set CLASSPATH=%CLASSPATH%;libswt\commands.jar
set CLASSPATH=%CLASSPATH%;libswt\win32\swt.jar

REM ******************************************************************
REM ** Set java runtime options                                     **
REM ** Change 128m to higher values in case you run out of memory.  **
REM ******************************************************************

set OPT=-Xmx256m -cp %CLASSPATH% -Djava.library.path=libswt\win32\

REM ***************
REM ** Run...    **
REM ***************

start javaw %OPT% org.pentaho.pms.demo.MakeSelectionDemo %1 %2 %3 %4 %5 %6 %7 %8 %9


