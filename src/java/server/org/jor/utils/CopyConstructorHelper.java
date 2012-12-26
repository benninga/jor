package org.jor.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.metadata.ClassMetadata;

import org.jor.model.ModelBean;
import org.jor.model.ModelBeanDefaultClass;
import org.jor.model.ModelBeanFactory;
import org.jor.model.ModelCollectionType;
import org.jor.model.StringLength;
import org.jor.model.user.User;
import org.jor.server.services.db.DataService;

public class CopyConstructorHelper
{
    private static final String SET_ID_SETTER = "setId";
    private static final Set<Class<?>> primitiveTypes;
    private static final Set<Class<?>> collectionTypes;
    private static final Map<Class<?>, ValueManager> valueManagers;
    private static final Map<String, Class<?>> knownModelBeans;

    public static Object copyConstruct(Object obj) throws Exception
    {
        Class<?> clazz = obj.getClass();
        Constructor<?> cnstr = clazz.getConstructor(clazz);
        Object copiedObject = cnstr.newInstance(obj);
        return copiedObject;
    }

    public static Object constructRandom(Class<?> clazz) throws Exception
    {
        if (isPrimitiveType(clazz)) {
            return constructRandomPrimitive(clazz, null);
        }
        Constructor<?> cnstr = clazz.getConstructor();
        Object obj = cnstr.newInstance();
        Set<Method> setters = ClassAnalyzer.getAllSetAndGetMethods(clazz, false, true, false, "set");
        for (Method setter : setters)
        {
            if (checkSetterValid(setter) == false) {
                continue;
            }
            Class<?> valueType = setter.getParameterTypes()[0];
            Object value;
            ModelBeanDefaultClass annotation = setter.getAnnotation(ModelBeanDefaultClass.class);
            if (annotation != null && annotation.factory() != null)
            {
                value = createObject(annotation.factory());
            }
            else if (isPrimitiveType(valueType))
            {
                value = constructRandomPrimitive(valueType, setter);
            }
            else if (isEnumType(valueType))
            {
                value = constructRandomEnum(valueType);
            }
            else if (isCollectionType(valueType))
            {
                value = constructRandomCollection(setter, valueType);
            }
            else
            {
                if (annotation != null)
                {
                    valueType = annotation.value();
                }
                value = constructRandom(valueType);
            }
            if (Modifier.isPrivate(setter.getModifiers())) {
                setter.setAccessible(true);
            }
            try
            {
                setter.invoke(obj, value);
            }
            catch (Exception e)
            {
                throw new Exception("Failed to invoke setter method: " + setter.getName(), e);
            }
        }
        return obj;
    }

    private static Object createObject(Class<? extends ModelBeanFactory<?>> factory)
    {
        try {
            ModelBeanFactory<?> objectFactory = factory.newInstance();
            return objectFactory.create();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create value", e);
        }
    }

    private static boolean checkSetterValid(Method setter)
    {
        if (SET_ID_SETTER.equals(setter.getName())) {
            return false;
        }
        else if (setter.getParameterTypes().length != 1) {
            throw new IllegalArgumentException("Setter method with bad signature: " + setter);
        }
        return true;
    }

    private static Object constructRandomPrimitive(Class<?> valueType, Method setter)
    {
        ValueManager manager = valueManagers.get(valueType);
        if (manager == null) {
            throw new NullPointerException("No value manager for type: " + valueType.getName());
        }
        Object value = manager.createRandomValue(setter);
        return value;
    }

    private static Object constructRandomEnum(Class<?> type) throws Exception
    {
        Method valuesMethod = type.getMethod("values");
        Object[] values = (Object[])valuesMethod.invoke(null);
        int valueCount = values.length;
        int index = (int)(Math.random() * valueCount);
        return values[index];
    }

    private static Object constructRandomCollection(Method setter, Class<?> type)
        throws Exception
    {
        if (type.isAssignableFrom(type) && type != Map.class)
        {
            Collection<Object> collection;
            if (type == Set.class)
            {
                collection = new HashSet<>();
            }
            else if (type == List.class)
            {
                collection = new ArrayList<>();
            }
            else
            {
                throw new IllegalArgumentException("Unexpected collection type: " + type);
            }
            addObjectsToCollectionIfPossible(setter, collection);
            return collection;
        }
        else if (type == Map.class)
        {
            return new HashMap<>();
        }
        else
        {
            throw new IllegalArgumentException("Unexpected collection type: " + type);
        }
    }

    private static void addObjectsToCollectionIfPossible(Method setter, Collection<Object> c)
        throws Exception
    {
        String setterName = setter.getName();
        ModelCollectionType type = setter.getAnnotation(ModelCollectionType.class);
        Class<?> clazz;
        if (type == null)
        {
            String className = setterName.substring(3, setterName.length() - 1);
            clazz = knownModelBeans.get(className);
            if (clazz == null) {
                throw new RuntimeException("Encountered collection with unknown class: " + className +
                        "\n\tDid you forget to add @ModelCollectionType to your collection setter?");
            }
        }
        else
        {
            clazz = type.value();
        }
        c.add(constructRandom(clazz));
        c.add(constructRandom(clazz));
    }

    public static boolean isCollectionType(Class<?> type)
    {
        if (collectionTypes.contains(type)) {
            return true;
        }
        return false;
    }

    public static boolean isPrimitiveType(Class<?> type)
    {
        if (primitiveTypes.contains(type))
        {
            return true;
        }
        return false;
    }

    public static boolean isEnumType(Class<?> type)
    {
        return Enum.class.isAssignableFrom(type);
    }

    public static String compareObjects(Object a, Object b)
    {
        return compareObjects(a, b, null, false);
    }

    public static String compareObjects(Object a, Object b,
                                        boolean compareSavedPropertiesOnly)
    {
        return compareObjects(a, b, null, compareSavedPropertiesOnly);
    }

    public static String compareObjects(Object a, Object b, Set<String> ignoredPaths,
                                        boolean compareSavedPropertiesOnly)
    {
        if (ignoredPaths == null)
        {
            ignoredPaths = new HashSet<>();
        }

        StringBuilder result = new StringBuilder();
        compareObjects(a, b, a.getClass().getSimpleName(),
                       result, ignoredPaths, compareSavedPropertiesOnly);
        String resultMessage = result.toString();
        return (resultMessage.isEmpty()) ? null : resultMessage;
    }

    private static void compareObjects(Object a, Object b, String path,
                                       StringBuilder result, Set<String> ignoredPaths,
                                       boolean compareSavedPropertiesOnly)
    {
        if (terminatePathForNull(a, b, path, result, ignoredPaths)) {
            return;
        }

        if (a == b) {
            logError(path + ": Objects A and B are reference to the same object\n",
                          path, result, ignoredPaths);
            return;
        }

        try
        {
            Method[] methods = a.getClass().getMethods();
            for (Method method : methods)
            {
                if (method.getName().startsWith("get") == false) {
                    continue;
                }
                else if (method.getParameterTypes().length != 0) {
                    continue;
                }
                else if (isComparable(method.getReturnType()) == false) {
                    continue;
                }

                Object aVal = method.invoke(a);
                Object bVal = method.invoke(b);
                Class<?> returnType = method.getReturnType();
                String newPath = path + "." + method.getName();
                if (isPrimitiveType(returnType) || returnType.isEnum())
                {
                    if (compareSavedPropertiesOnly == false
                            || isPersistedProperty(method.getName(), a.getClass()))
                    {
                        compareValues(aVal, bVal, newPath, result, ignoredPaths);
                    }
                }
                else if (isCollectionType(returnType))
                {
                    if(compareSavedPropertiesOnly == false || isPersistedProperty(method.getName(), a.getClass()))
                    {
                        compareCollections(aVal, bVal, newPath, result, ignoredPaths,
                                           compareSavedPropertiesOnly);
                    }
                }
                else
                {
                    compareObjects(aVal, bVal, newPath, result, ignoredPaths,
                                   compareSavedPropertiesOnly);
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(path, e);
        }
    }

    private static void compareCollections(Object a, Object b, String path,
                                           StringBuilder result, Set<String> ignoredPaths,
                                           boolean compareSavedPropertiesOnly)
    {
        if (a instanceof Collection && !((Collection<?>) a).isEmpty())
        {
            // first check is to make sure we don't have the same instances in both
            // collections. Skip this check when dealing with Strings since they are immutable.
            try {
                Object[] itemsA = ((Collection<?>)a).toArray();
                Object firstItem = itemsA[0];
                if (!(firstItem instanceof String))
                {

                    for (Object itemA : ((Collection<?>)a))
                    {
                        boolean foundMatchForA = false;
                        for (Object itemB : ((Collection<?>)b))
                        {
                            if (!isPrimitiveType(itemA.getClass()) && itemA == itemB) // same object reference
                            {
                                logError(path + ": Objects A and B are reference to the same object"
                                         + " in different collections\n",
                                         path, result, ignoredPaths);
                                return;
                            }
                            else if (compareObjects(itemA, itemB, ignoredPaths, compareSavedPropertiesOnly) == null)
                            {
                                foundMatchForA = true;
                            }
                        }
                        if (foundMatchForA == false)
                        {
                            logError(path + ": Could not find a matching object for item in collection A\n",
                                     path, result, ignoredPaths);
                        }
                    }
                }
            }
            catch (Exception error)
            {
                return;
            }
        }
    }

    private static boolean terminatePathForNull(Object a, Object b, String path,
                                                StringBuilder result, Set<String> ignoredPaths)
    {
        if (a == null)
        {
            if (b == null)
            {
                System.out.println("Path terminated. " + path);
                return true;
            }
            else
            {
                logError(String.format("Wrong value for bean: %s. A=null, B!=null\n", path),
                              path, result, ignoredPaths);
                return true;
            }
        }
        else
        {
            if (b == null)
            {
                logError(String.format("Wrong value for bean: %s. A!= null, B=null\n", path),
                         path, result, ignoredPaths);
                return true;
            }
        }
        return false;
    }

    private static void compareValues(Object aVal, Object bVal, String path,
                                      StringBuilder result, Set<String> ignoredPaths)
    {
        ignoredPaths.add("getLastUpdateAt");
        boolean equals = true;
        if (aVal == null)
        {
            if (bVal != null) {
                equals = false;
            }
        }
        else if (aVal.equals(bVal) == false)
        {
            equals = false;
        }

        if (equals == false)
        {
            String msg = String.format("Wrong value for method: %s. A=%s, B=%s\n",
                                       path, aVal, bVal);
            logError(msg, path, result, ignoredPaths);
        }
    }

    private static boolean isPersistedProperty(String setterName, Class<?> clazz)
    {
        DataService service = DataService.getDataService();
        if (service == null) {
            return false; // Is this correct? At least this will cause unit test failure.
        }

        ClassMetadata meta = service.getMetadata(clazz);
        if (meta == null) {
            return false;
        }
        String[] properties = meta.getPropertyNames();
        String property = Character.toLowerCase(setterName.charAt(3)) + setterName.substring(4);
        for (String propertyName : properties)
        {
            if (property.equals(propertyName)) {
                return true;
            }
        }
        return false;
    }

    private static void logError(String message, String path,
                                 StringBuilder result, Set<String> ignoredPaths)
    {
        if (ignoredPaths.contains(path)) {
            return; // don't log error
        }
        for (String ignore : ignoredPaths)
        {
            int index = path.lastIndexOf('.');
            if (index > -1 && ignore.equals(path.substring(index + 1)))
            {
                return; // don't log error
            }
        }
        result.append(message);
    }

    private static boolean isComparable(Class<?> type)
    {
        if (isPrimitiveType(type) || type.isEnum()) {
            return true;
        }
        else if (type.getAnnotation(ModelBean.class) != null)
        {
            return true;
        }
        else if (isCollectionType(type)) {
            return true;
        }
        return false;
    }

    private static interface ValueManager
    {
        public Object createRandomValue(Method method);
    }

    static
    {
        valueManagers = new HashMap<>();
        ValueManager mgr;
        mgr = new ValueManager()
        {
            @Override public Object createRandomValue(Method method)
            {
                return Integer.valueOf((int)(getRandomDouble(method)));
            }
        };
        valueManagers.put(Integer.class, mgr);
        valueManagers.put(Integer.TYPE, mgr);

        // Long
        mgr = new ValueManager()
        {
            @Override public Object createRandomValue(Method method)
            {
                return Long.valueOf((long)(getRandomDouble(method)));
            }
        };
        valueManagers.put(Long.class, mgr);
        valueManagers.put(Long.TYPE, mgr);

        // Double
        mgr = new ValueManager()
        {
            @Override public Object createRandomValue(Method method)
            {
                return getRandomDouble(method);
            }
        };
        valueManagers.put(Double.class, mgr);
        valueManagers.put(Double.TYPE, mgr);

        // Boolean
        mgr = new ValueManager()
        {
            @Override public Object createRandomValue(Method method)
            {
                double val = Math.random();
                return val < 0.5;
            }
        };
        valueManagers.put(Boolean.class, mgr);
        valueManagers.put(Boolean.TYPE, mgr);

        // String
        mgr = new ValueManager()
        {
            @Override public Object createRandomValue(Method method)
            {
                return getRandomString(method);
//                return "String-" + Math.random();
            }
        };
        valueManagers.put(String.class, mgr);

        // Date
        mgr = new ValueManager()
        {
            @Override public Object createRandomValue(Method method)
            {
                return new Date((long)(getRandomDouble(method)));
            }
        };
        valueManagers.put(Date.class, mgr);
    }

    private static String getRandomString(Method method)
    {
        int length = getLength(method);
        String string = ("" + Math.random()).replace('.', '0');
        while (length > string.length())
        {
            string += string;
        }
        while (length != string.length())
        {
            string = string.substring(1);
        }
        return string;
    }

    private static double getRandomDouble(Method method)
    {
        double min = getMin(method);
        double max = getMax(method);
        return min + ((max - min) * Math.random());
    }

    private static int getLength(Method method)
    {
        StringLength length = (method == null) ? null : method.getAnnotation(StringLength.class);
        if (length == null) {
            return 8;
        }
        return length.value();
    }

    private static long getMin(Method method)
    {
        Min min = (method == null) ? null : method.getAnnotation(Min.class);
        if (min == null) {
            return 0;
        }
        return min.value();
    }

    private static long getMax(Method method)
    {
        Max max = (method == null) ? null : method.getAnnotation(Max.class);
        if (max == null) {
            return Integer.MAX_VALUE;
        }
        return max.value();
    }

    static
    {
        collectionTypes = new HashSet<>();
        collectionTypes.add(Set.class);
        collectionTypes.add(List.class);
        collectionTypes.add(Map.class);
    }

    static
    {
        primitiveTypes = new HashSet<>();
        primitiveTypes.add(Double.TYPE);
        primitiveTypes.add(Double.class);
        primitiveTypes.add(Long.TYPE);
        primitiveTypes.add(Long.class);
        primitiveTypes.add(Integer.TYPE);
        primitiveTypes.add(Integer.class);
        primitiveTypes.add(Boolean.TYPE);
        primitiveTypes.add(Boolean.class);
        primitiveTypes.add(String.class);
        primitiveTypes.add(Void.TYPE);
        primitiveTypes.add(Date.class);
    }

    static
    {
        knownModelBeans = new HashMap<>();
        addKnownClass(User.class);
    }

    private static void addKnownClass(Class<?> clazz)
    {
        knownModelBeans.put(clazz.getSimpleName(), clazz);
    }
}
