# WorkInProgress
These are the two projects( Flipkart's Aesop and Flipkart's Open Replicator) that me and my colleague were working on and we will opensource the changes once done.

# Changes already done:
# FK's Aesop:
1. Editing Kafka data layer to insert data in JSON instead of pre-programmed AVRO schema.
2. Getting Old and new mysql row values on upsert( update or insert).

# FK's Open replicator:
1. Made Open replicator fault tolerant for MySQL restart events.
2. Making sure it continues from last read MySQL bin-logs on retrying after crash instead of retrying from beginning.

The above changes were added by my colleague.
#Deployment:
- Ansible has been used to automate the deployment of the AESOP ecosystem to any remote servers(We've used AWS T2 Medium Instances)
- It sets up the AESOP relay, a cluster of AESOP consumers automatically. It also configures Zookeeper and Kafka servers, both of which are cluster based to ensure high availablity and fault-tolerance. Adding servers to the cluster is as simple as adding another machine's IP address to the Ansible variables.
- The monitor the AESOP ecosystem, we have used CheckMK to get the status of the Zookeeper and Kafka servers via JMX. It is also used to monitor the system performance such as Disk space remaining, Memory Used,etc. It has been configured to send out email alerts increase the servers pass some threshold values. Cronjobs are also setup automatically via ansible.
- The AESOP Relay and AESOP Consumer as monitored via jmxtrans. There are custom BASH scripts to monitor the performance of the system.
- To monitor the lag between the AESOP Relay and the AESOP Consumer, a custom python script has been written which fetches the last consumed SCN number from the Zookeeper partitions(as they are more reliable than the consumer logs) and sends out an email every hour which is configurable via crontab.

#TODO:
- Add Spring file config code to allow Aesop relay server to read from multiple MySQL DBs and allow aesop client servers to read from multiple relay servers. Current configuration allows single db read for relay and single relay read for client.
- Take Flipkart's Aesop and Flipkart's Open Replicator fork merge own changes and raise request to pull the changes.
- Add detailed steps to execute AESOP components and Ansible. Also merge the tutorial videos into this repo.
