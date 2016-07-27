#!/bin/bash

starttime=$(date +"%s%N")

sourcefile=`mysql -hrelay_1 -uaesop_user -paesop  -e "show master status" | grep "relay_1[0-9]-bin" | cut -f 1 | cut -d "." -f 2 | tr -d '0'`

sourcepos=`mysql -hrelay_1 -uaesop_user -paesop  -e "show master status" | grep "relay_2[0-9]-bin" | cut -f 2`

let "sourceraised=$sourcefile << 32"

sourcescn=$((sourceraised + sourcepos))

client_scn_command='tail -n 100  /path_to_log_dir/mysql-client-cluster/mysql-client-cluster.log | grep -o "sequence.*"  | tail -1 | cut -d ":" -f 2 | cut -d "," -f 1'

client2Scn=`ssh aesop_user@stg_client_server_1 $client_scn_command`

client3Scn=`ssh aesop_user@stg_client_server_2 $client_scn_command`

endtime=$(date +"%s%N")

if test $client3Scn -gt $client2Scn
then
destinationScn=$client3Scn
else
destinationScn=$client2Scn
fi

echo File on Source = $sourcefile
echo Bin Log Position on Source = $sourcepos
echo Source SCN = $sourcescn
echo client2Scn SCN = $client2Scn
echo client3Scn SCN = $client3Scn
echo destinationScn SCN = $destinationScn

diff=$((sourcescn-destinationScn))
diff2=$((sourcescn-client3Scn))
diff3=$((sourcescn-client2Scn))

echo Client 2 Lag in Bytes = $diff2
echo Client 3 Lag in Bytes = $diff3
echo Destination Lag in Bytes = $diff

if test $diff -lt 0
then
diff=0
fi

diff_in_kb=$((diff/1024))

diff_in_mb=$((diff/1048576))

echo Destination Lag in KB = $diff_in_kb
echo Destination Lag in MB = $diff_in_mb


scripttime=$((endtime-starttime))

echo Script took time in milliseconds = $((scripttime/1000000))

echo Destination Lag in Seconds = $((diff_in_kb/100))
                                                                        
