package models;

import com.owlike.genson.Genson;

public class Serializer<T> {
    private Genson genson;

    public Serializer() {
        genson = new Genson();
    }

    public String classToString(T object) {
        return genson.serialize(object);
    }

    public T classFromString(String str, Class<T> clazz) {
        return genson.deserialize(str, clazz);
    }
}
