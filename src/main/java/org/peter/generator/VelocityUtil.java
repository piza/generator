package org.peter.generator;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by Peter on 16/1/25.
 */
public class VelocityUtil {

    private static ObjectMapper objectMapper=new ObjectMapper();

    public static <T> String objToJson(Class<T> objClass){
        try {
            T t= objClass.newInstance();
           return objectMapper.writeValueAsString(t);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
    }
}
