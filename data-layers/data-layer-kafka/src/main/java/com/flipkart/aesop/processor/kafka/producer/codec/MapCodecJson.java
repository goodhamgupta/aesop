package com.flipkart.aesop.processor.kafka.producer.codec;

import com.flipkart.aesop.processor.kafka.producer.codec.ifaces.Codec;

import javax.validation.constraints.NotNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;


public class MapCodecJson implements Codec<Map<String,Object>> {

    public byte[] encode(@NotNull Map<String,Object> object) throws IOException {
        if (object == null) {
            return null;
        }
        JSONObject obj = new JSONObject();
        try 
        {
        	for (Map.Entry<String, Object> entry : object.entrySet()) {
        	    String key = entry.getKey();
        	    Object value = entry.getValue();
        	    obj.put(key, value);
        	}
        }
        catch (JSONException ex) {
            throw new IllegalArgumentException("######Unsupported Encoding Failed to serialize object of type: " + object.getClass(), ex);
        }
        return obj.toString().getBytes(Charset.forName("UTF-8"));
    }

    @SuppressWarnings(value = "unchecked")
    public Map<String,Object> decode(byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        }
        try {
            //ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            //return (Map<String, Object>) ois.readObject();
        	return null;
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Failed to deserialize object", ex);
        }
    }
}
