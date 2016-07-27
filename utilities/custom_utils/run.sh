#Script to manage zookeeper
#Copy this script to the location of the zoo.conf file. Ansible location is: /home/user_name/zookeeper_cluster/install/zookeeper-3.4.6/conf
command=$1

if [ $command == ""]
then
	echo "Usage: bash "$0 "{run|cli|stop|status|restart}"
else
	case $command in
			run)
			sudo bash /usr/share/zookeeper/bin/zkServer.sh start ./zoo.conf
			;;
			cli)
			sudo bash /usr/share/zookeeper/bin/zkCli.sh
			;;
			stop)
			sudo bash /usr/share/zookeeper/bin/zkServer.sh stop ./zoo.conf
			;;
			status)
			sudo bash /usr/share/zookeeper/bin/zkServer.sh stop ./zoo.conf
			;;
			restart)
			sudo bash /usr/share/zookeeper/bin/zkServer.sh stop ./zoo.conf
			sudo bash /usr/share/zookeeper/bin/zkServer.sh start ./zoo.conf
			;;
			*)
	esac
fi

