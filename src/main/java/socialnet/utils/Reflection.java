package socialnet.utils;


import lombok.var;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

@Component
public class Reflection {
    public Map<String, Object> getFieldsNameAndValue(Object object) {
        Map<String, Object> result = new HashMap<>();
        var cls = object.getClass();
        for (Field field : cls.getDeclaredFields()) {
            var value = methodInvoke(object, getMethodName("get", field.getName()));
            result.put(field.getName(), value);
        }
        return result;
    }

    public <T> void getAllFields(T cls) {
        for (Field field : cls.getClass().getDeclaredFields()) {
            //System.out.println(getMethodName("get", field.getName()));
            System.out.println(field.getName());
        }
    }

    private String getMethodName(String prefix, String fieldName) {
        return prefix +
                String.valueOf(fieldName.charAt(0)).toUpperCase().concat(fieldName.substring(1));
    }

    public <T> Object methodInvoke(T cls, String methodName) {
        try {
            Method method = cls.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(cls);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFieldNames(Object object) {
        String result = "";
        String delemitter = "";
        for (Field field : object.getClass().getDeclaredFields()) {
            result = result.concat(delemitter).concat(field.getName()).concat(" = ? ");
            delemitter = ", ";
        }
        return result;
    }

    public String getSqlFieldNames(Object object) {
        String result = "";
        String delemitter = "";
        for (Field field : object.getClass().getDeclaredFields()) {
            result = result.concat(delemitter)
                    .concat(getSqlName(field.getName()))
                    .concat(" = ? ");
            delemitter = ", ";
        }

        return result;
    }

    public String getSqlName(String fieldName) {
        String result = "";
        String capsName = fieldName.toUpperCase();
        for (int i = 0; i < fieldName.length(); i++) {
            if (fieldName.charAt(i) == capsName.charAt(i)) {
                result = fieldName.substring(0, i).concat("_").concat(fieldName.substring(i));
                break;
            }
        }
        if (result.isEmpty()) result = fieldName;
        String r = result;
        return r;
    }

    public Object[] getObjectsArray(Object object) {
        var declaredFields = object.getClass().getDeclaredFields();
        Object[] objects = new Object[declaredFields.length];
        int i = 0;
        for (Field field : declaredFields) {
            var value = methodInvoke(object, getMethodName("get", field.getName()));
            objects[i] = value;
            i++;
        }
        return objects;
    }

    public int[] getTypesArray(Object object) {
        var declaredFields = object.getClass().getDeclaredFields();
        int[] result = new int[declaredFields.length];
        int i = 0;
        for (Field field : declaredFields) {
            var type = field.getType();
            // TODO: 31.03.2023 добавить недостающие классы для преобразования
            if (type == String.class)
                result[i] = Types.VARCHAR;
            else if (type == Long.class)
                result[i] = Types.BIGINT; //-5;
            else if (type == Timestamp.class)
                result[i] = Types.TIMESTAMP;
            i++;
        }
        return result;
    }

    public void objectsAdapt(Object[] objects, int[] types) {
        int i = 0;
        for (int type : types) {
            if ((type == 12) && (objects[i] != null)) {
                objects[i] = "'".concat((String) objects[i]).concat("'");
            }
            i++;
        }
    }

}
