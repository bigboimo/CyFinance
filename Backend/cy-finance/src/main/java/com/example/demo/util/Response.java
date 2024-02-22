package com.example.demo.util;

import java.util.HashMap;
import java.util.Map;

public class Response<V> extends HashMap<String, V> {

    public Response(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public Response(int initialCapacity){
        super(initialCapacity);
    }

    public Response(){
        super();
    }

    public Response(Map<String, ? extends V> m) {
        super(m);
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append("{\"");
        for (Map.Entry<String, V> entry : this.entrySet()) {
            result.append(entry.getKey()).append("\":");//
            if (entry.getValue() instanceof String){
                result.append('\"').append(entry.getValue()).append("\", \"");
            } else {
                result.append(entry.getValue()).append(", \"");
            }
        }
        result.delete(result.length() - 3, result.length());
        result.append('}');
        return result.toString();
    }
}
