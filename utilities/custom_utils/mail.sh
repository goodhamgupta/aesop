#!/bin/bash

#This script is written assuming that it is run on a server configured to send mails. 

echo -e "AESOP Relay has encountered some Open replicator exception.The service might have restarted, but please look into it ASAP. " | mutt -s "MySQL Service Alert" shubham@zopper.com ankit.bisht@zopper.com devops@zopper.com bragadish@zopper.com

sudo python /srv/new_aesop/utilities/custom_utils/fetch.py



