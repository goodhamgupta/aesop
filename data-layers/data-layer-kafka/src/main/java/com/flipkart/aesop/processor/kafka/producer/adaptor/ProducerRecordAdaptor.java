package com.flipkart.aesop.processor.kafka.producer.adaptor;

import com.flipkart.aesop.event.AbstractEvent;
import com.flipkart.aesop.processor.kafka.processor.KafkaEventProcessor;
import com.flipkart.aesop.processor.kafka.producer.codec.ifaces.Codec;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.trpr.platform.core.impl.logging.LogFactory;
import org.trpr.platform.core.spi.logging.Logger;

import java.io.IOException;
import java.nio.charset.Charset;

public abstract class ProducerRecordAdaptor<K,V> {

    private Codec<K> keyCodec;

    private Codec<V> valueCodec;

    /**
     * Created the producer records from the abstract event
     * @param event abstract event
     * @return Producer Record
     * @throws IOException
     */
    public ProducerRecord createProducerRecord(AbstractEvent event) throws IOException {
    	Logger LOGGER = LogFactory.getLogger(ProducerRecordAdaptor.class);
    	LOGGER.info("######################################");
    	LOGGER.info("working2");
    	LOGGER.info("######################################");
        return new ProducerRecord(getTopicName(event),
                keyCodec.encode(getPrimaryKey(event)),  valueCodec.encode(getPayload(event
        )));
        /*return new ProducerRecord(getTopicName(event),
                "ankit".getBytes(Charset.forName("UTF-8")),  "{\"balance\": 1000.21, \"num\":100, \"is_vip\":true, \"name\":\"foo\"}".getBytes(Charset.forName("UTF-8")));
    */
    }

    /**
     * Returns the topic name from the abstract event
     * @param event
     * @return
     */
    public abstract String getTopicName(AbstractEvent event);

    /**
     * Returns the primary key from the abstract event
     * @param event
     * @return
     */
    public abstract K getPrimaryKey(AbstractEvent event);

    /**
     * Returns the payload from the abstract event
     * @param event
     * @return
     */
    public abstract V getPayload(AbstractEvent event);

    /* Getters and Setters start */
    public void setKeyCodec(Codec<K> keyCodec) {
        this.keyCodec = keyCodec;
    }

    public Codec<K> getKeyCodec() {
        return this.keyCodec;
    }

    public void setValueCodec(Codec<V> valueCodec) {
        this.valueCodec = valueCodec;
    }

    public Codec<V> getValueCodec() {
        return this.valueCodec;
    }
    /* Getters and Setters end */
}