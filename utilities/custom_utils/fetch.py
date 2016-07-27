from kazoo.client import KazooClient

import json
import ast
import logging
import operator
import MySQLdb
import socket
import time
from tabulate import tabulate
import smtplib
from email.MIMEMultipart import MIMEMultipart
from email.MIMEText import MIMEText


from itertools import izip

logging.basicConfig()


MYSQL_SHOW_MASTER_STATUS    = 'SHOW MASTER STATUS'



def get_mysql_master_replication_status():
	try:
		db  = MySQLdb.connect("10.0.3.132","aesopuser","A3s0pUs3rClu3t3t","merchant")
		cursor = db.cursor()
		cursor.execute("SHOW MASTER STATUS")
		row = cursor.fetchone()
		col_names = [desc[0] for desc in cursor.description]
		return dict(izip(col_names, row))
	except Exception as e:
		print ("Exceptio occured! ",str(e))

def validate_master():
    errors = {}
    try:
        res = get_mysql_master_replication_status()
    except Exception, e:
        errors = {'MASTER_CONNECTION_ERROR': str(e)}
        return None, errors
    print res
    position            = res['Position']
    binlog_do_db        = res['Binlog_Do_DB']
    binlog_file         = res['File']
    binlog_ignore_db    = res['Binlog_Ignore_DB']
    return res, errors


def get_scn_from_zookeeper():
	try:
		print("Starting the process")
		zk = KazooClient(hosts='10.0.57.146:2181,10.0.57.145:2181,10.0.77.195:2181')
		print("Connection established")
		zk.start()
		sum_of_scn_num = 0
		if zk.exists("/fk_kafka_cluster1"):
			children_list = zk.get_children("/fk_kafka_cluster1/PROPERTYSTORE/")
			sorted_list = sorted(children_list)
			partion_num_scn_num_dict = {}
			for children in sorted_list:
				recv_data,stat = zk.get("/fk_kafka_cluster1/PROPERTYSTORE/"+str(children))
				data_dict = ast.literal_eval(recv_data)
				partition_num =  data_dict["id"]
				fields = ast.literal_eval(data_dict['simpleFields']['c'])
				scn_num = fields["windowScn"]
				sum_of_scn_num += scn_num
				partion_num_scn_num_dict[partition_num] = scn_num
			print "Data fetching from Zookeeper complete"
			avg_scn_num = (sum_of_scn_num/len(children_list))
			sorted_dict = sorted(partion_num_scn_num_dict.items(),key=operator.itemgetter(1))
			return avg_scn_num,sorted_dict 
		else:
			print("Node does not exist")
	except Exception as e:
		print("Exception occured!",str(e))


def send_mail(email_subject,email_body):
	print "Inside send mail"
	try:
		from_addr = "aesop.alerts@gmail.com"
		to_addr = ["shubham@zopper.com", "ankit.bisht@zopper.com","bragadish@zopper.com" ,"devops@zopper.com","pavankumar@zopper.com"]
		msg = MIMEMultipart()
		msg['From'] = from_addr
		msg['To'] = ", ".join(to_addr)
		msg['Subject'] = email_subject
		msg.attach(MIMEText(email_body,_charset="UTF-8"))
		server = smtplib.SMTP('smtp.gmail.com', 587)
		server.starttls()
		server.login(from_addr,"x7lsvtrl")
		text = msg.as_string()
		server.sendmail(from_addr,to_addr,text)
		server.quit()
	except Exception as e:
		print str(e)

if __name__ == '__main__':

	try:
		start_time = time.time()
		res,errors = validate_master()
		offset = int(res['Position'])
		file_number = int(res['File'][10:])
		mysql_scn_number = (file_number << 32 | offset)
		zoo_scn_number,sorted_dict = get_scn_from_zookeeper()
		dest_lag_in_bytes = mysql_scn_number - zoo_scn_number
		dest_lag_in_kb = float(dest_lag_in_bytes/1024)
		dest_lag_in_mb = float(dest_lag_in_kb/1024)
		dest_lag_in_gb = int(dest_lag_in_mb/1024)
		if dest_lag_in_kb > 500:
			alert_type="LEVEL-HIGH"
		elif dest_lag_in_kb > 100:
			alert_type="LEVEL-MED"
		else:
			alert_type="LEVEL-INFO"
		hostname = socket.gethostname()
		email_subject = str(alert_type +  " Destination Lag = "+ str(dest_lag_in_kb) + " KB. Destination Lag = "+ str(dest_lag_in_kb/100) + " seconds. " + str(hostname))
		headers = ["Partition Number","SCN Number"]
		table = tabulate(sorted_dict,headers=headers,tablefmt="simple").encode('ascii','ignore')
		email_body = ""
		email_body += "File Number on Database = " + str(file_number) + "\n"
		email_body += "Bin Log Position on Source = " + str(offset) + "\n"
		email_body += "Database SCN Number = " +str(mysql_scn_number) + "\n"	
		email_body += "Destination SCN Number = " + str(zoo_scn_number) + "\n"
		email_body += "Lag in Bytes = " + str(dest_lag_in_bytes) + "\n"
		email_body += "Lag in KB = " + str(dest_lag_in_kb) + "\n"
		email_body += "Lag in MB = " + str(dest_lag_in_mb) + "\n"
		email_body += "Lag in GB = " + str(dest_lag_in_gb) + "\n"
		finish_time = (time.time()-start_time)
		email_body += "\nAlert generation time = " + str(finish_time) + " seconds\n"
		email_body += "\n The partions of Zookeeper and their SCN numbers are as follows: \n\n"
		email_body += table
		send_mail(email_subject,email_body)
	except Exception as e:
		print ("Exception Occured!",str(e))		
