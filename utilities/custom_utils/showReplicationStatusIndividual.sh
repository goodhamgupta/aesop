#!/bin/bash

starttime=$(date +"%s%N")

hostname=`hostname -f`

sourcefile=`mysql -h139.162.24.27 -ur42user -pr42passmysql  -e "show master status" | grep "mysql-bin.[0-9]" | cut -f 1 | cut -d "." -f 2 | sed 's/^0*//'`

sourcepos=`mysql -h139.162.24.27 -ur42user -pr42passmysql  -e "show master status" | grep "mysql-bin.[0-9]" | cut -f 2`

echo $sourcefile
let "sourceraised=$sourcefile<<32"

sourcescn=$((sourceraised + sourcepos))
echo "Source SCN is $sourcescn"

destinationScn=`sudo tail -n 1000  /srv/aesop_logs/sample-mysql-relay.log | grep -o "sequence.*"  | tail -1 | cut -d ":" -f 2 | cut -d "," -f 1`
if [ -z "$destinationScn" ] 
then
	destinationScn=`sudo tail -n 1000  /srv/aesop_logs/sample-mysql-relay.log | grep -o "windowScn.*"  | tail -1 | cut -d ":" -f 2 | cut -d "," -f 1 | sed 's/.$//'`
	echo "Varable is null. Pattern used to get latest SCN numer is = windowScn.*"
else
	echo "Variable is not null.Pattern used to get latest SCN number is = sequence.*"
fi

echo "Destinationscn is $destinationScn"
endtime=$(date +"%s%N")

email_body="File on Source = $sourcefile.\n"
email_body="$email_body Bin Log Position on Source = $sourcepos.\n"
email_body="$email_body Source SCN = $sourcescn.\n"
email_body="$email_body Destination SCN = $destinationScn.\n"
echo File on Source = $sourcefile
echo Bin Log Position on Source = $sourcepos
echo Source SCN = $sourcescn
echo Destination SCN = $destinationScn

diff=($((sourcescn-destinationScn)))

echo Destination Lag in Bytes = $diff
email_body="$email_body Destination Lag in Bytes = $diff.\n"
if test $diff -lt 0
then
diff=0
fi

diff_in_kb=$((diff/1024))

diff_in_mb=$((diff/1048576))

diff_in_gb=$((diff_in_mb/1024))

echo Destination Lag in KB = $diff_in_kb
echo Destination Lag in MB = $diff_in_mb
echo Destination Lag in GB = $diff_in_gb

email_body="$email_body Destination Lag in KB = $diff_in_kb.\n"
email_body="$email_body Destination Lag in MB = $diff_in_mb.\n"
email_body="$email_body Destination Lag in GB = $diff_in_gb.\n"
scripttime=$((endtime-starttime))

echo Script took time in milliseconds = $((scripttime/1000000))

echo Destination Lag in Seconds = $((diff_in_kb/100))

email_body="$email_body Script execution time = $((scripttime/1000000)) milliseconds.\n"

email_body="$email_body Destination Lag = $((diff_in_kb/100)) seconds.\n"

if [ $diff_in_kb -gt 500 ] 
then
alert_type=LEVEL-HIGH
elif [ $diff_in_kb -gt 100 ] 
then
alert_type=LEVEL-MED
elif [ $diff_in_kb -gt 50 ] 
then
alert_type=LEVEL-LOW
else 
alert_type=INFORMATION
fi

subject="$alert_type. Destination Lag = $diff_in_kb KB. Destination Lag = $((diff_in_kb/100)) seconds. $hostname"

echo -e "$email_body"| mutt -s "$subject" devops@zopper.com,shubham@zopper.com,ankit.bisht@zopper.com,ashish.baweja@zopper.com,bragadish@zopper.com
