from kafka import KafkaConsumer
import json
# To consume latest messages and auto-commit offsets
#Change topic name accordingly. General format: "database_name.topic". GroupID does not matter.
consumer = KafkaConsumer('merchant.topic',group_id='1',bootstrap_servers=['172.17.28.217:9092','172.17.28.218:9092','172.17.28.219:9092',])
for message in consumer:
	data =json.loads(message.value)
	print data
