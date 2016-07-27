/*
 * Copyright 2012-2015, the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flipkart.aesop.runtime.producer.eventprocessor.impl;

import org.apache.avro.generic.GenericRecord;
import org.trpr.platform.core.impl.logging.LogFactory;
import org.trpr.platform.core.spi.logging.Logger;

import com.flipkart.aesop.runtime.producer.eventlistener.OpenReplicationListener;
import com.flipkart.aesop.runtime.producer.eventprocessor.BinLogEventProcessor;
import com.flipkart.aesop.runtime.producer.txnprocessor.MysqlTransactionManager;
import com.flipkart.aesop.runtime.producer.txnprocessor.impl.MysqlTransactionManagerImpl;
import com.google.code.or.binlog.BinlogEventV4;
import com.google.code.or.binlog.impl.event.StopEvent;

/**
 * The <code>QueryEventProcessor</code> processes StopEvent from source. Rotate Event is called whenever the Mysql server stops.
 * ASSUMPTION : is that when a stop event is received and mysql is started after that the next file sequence will be one more than current
 * file.
 * TODO: send email to concerned users if stop event is received i.e. mysql has stopped.
 * @author Ankit Bisht
 * @version 1.0, 04 Mar 2016
 */
public class StopEventProcessor implements BinLogEventProcessor{

	/** Logger for this class*/
	private static final Logger LOGGER = LogFactory.getLogger(StopEventProcessor.class);

	/**
	 * @see com.flipkart.aesop.runtime.producer.eventprocessor.BinLogEventProcessor#process(com.google.code.or.binlog.BinlogEventV4, com.flipkart.aesop.runtime.producer.eventlistener.OpenReplicationListener)
	 */
	@Override
	public void process(BinlogEventV4 event, OpenReplicationListener listener) throws Exception {
		MysqlTransactionManager manager = listener.getMysqlTransactionManager();
		
		//StopEvent stopEvent = (StopEvent)event;
		int currFileNum = manager.getCurrFileNum();
		int newFileNum = currFileNum + 1;
		LOGGER.info("AESOP INFO >> MySQL stopped, Increasing file sequence number by 1. Current bin log file number: " + String.valueOf(currFileNum));
		manager.setCurrFileNum(newFileNum);
	}
}
