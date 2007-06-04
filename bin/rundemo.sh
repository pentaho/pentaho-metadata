#!/bin/sh

# **************************************************
# ** Set these to the location of your mozilla
# ** installation directory.  Use a Mozilla with
# ** Gtk2 and Fte enabled.
# **************************************************

export MOZILLA_FIVE_HOME=/usr/local/mozilla
export LD_LIBRARY_PATH=/usr/local/mozilla

# **************************************************
# ** Libraries used:                              **
# **************************************************

CLASSPATH=.

# This will get the versioned pentaho-meta.jar
for i in `ls ./lib/*.jar`
do
  CLASSPATH=${CLASSPATH}:${i}
done

CLASSPATH=$CLASSPATH:libswt/jface.jar
CLASSPATH=$CLASSPATH:libswt/runtime.jar

# **************************************************
# ** JDBC & other libraries used:                 **
# **************************************************

for f in `find libext`
do
  CLASSPATH=$CLASSPATH:$f
done

# **************************************************
# ** Platform specific libraries ...              **
# **************************************************

JAVA_BIN=java
LIBPATH="NONE"

case `uname -s` in 
	AIX)
		LIBPATH=libswt/aix/
		;;

	SunOS) 
		LIBPATH=libswt/solaris/
		;;

	Darwin)
		LIBPATH=libswt/osx/
		JAVA_BIN=libswt/osx/java_swt
		chmod +x $JAVA_BIN
		;;

	Linux)
	    ARCH=`uname -m`
		case $ARCH in
			x86_64)
				LIBPATH=libswt/linux/x86_64/
				;;

			i[3-6]86)
				LIBPATH=libswt/linux/x86/
				;;

			ppc)
				LIBPATH=libswt/linux/ppc/
				;;

			*)	
				echo "I'm sorry, this Linux platform [$ARCH] is not yet supported!"
				exit
				;;
		esac
		;;

	FreeBSD)
	    ARCH=`uname -m`
		case $ARCH in
			x86_64)
				LIBPATH=libswt/freebsd/x86_64/
				echo "I'm sorry, this Linux platform [$ARCH] is not yet supported!"
				exit
				;;

			i[3-6]86)
				LIBPATH=libswt/freebsd/x86/
				;;

			ppc)
				LIBPATH=libswt/freebsd/ppc/
				echo "I'm sorry, this Linux platform [$ARCH] is not yet supported!"
				exit
				;;

			*)	
				echo "I'm sorry, this Linux platform [$ARCH] is not yet supported!"
				exit
				;;
		esac
		;;

	HP-UX) 
		LIBPATH=libswt/hpux/
		;;
	CYGWIN*)
		./RunDemo.bat
		# exit
		;;

	*) 
		echo Running the demo is not supported on this hosttype : `uname -s`
		exit
		;;
esac 

export LIBPATH

if [ "$LIBPATH" != "NONE" ]
then
  for f in `find $LIBPATH -name '*.jar'`
  do
    CLASSPATH=$CLASSPATH:$f
  done
fi


# ******************************************************************
# ** Set java runtime options                                     **
# ** Change 256m to higher values in case you run out of memory.  **
# ******************************************************************

OPT="-Xmx256m -cp $CLASSPATH -Djava.library.path=$LIBPATH"

# ***************
# ** Run...    **
# ***************

$JAVA_BIN $OPT org.pentaho.pms.demo.MakeSelectionDemo "$1" "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9"

