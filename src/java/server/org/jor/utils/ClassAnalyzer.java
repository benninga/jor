package org.jor.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

public class ClassAnalyzer
{
    private static final String GET_PREFIX = "get";
    private static final String SET_PREFIX = "set";

    public static Set<Method> getAllSetAndGetMethods(Class<?> clazz,
                                                     boolean includeInterfaces,
                                                     boolean allowNonPublic,
                                                     boolean includeIgnored,
                                                     String prefix)
    {
        Set<Class<?>> processed = getCandidateClasses(clazz, includeInterfaces);

        Map<String, List<Method>> methodMaps = new HashMap<>();
        
        for (Class<?> current : processed)
        {
            Method[] allMethods = current.getDeclaredMethods();
            for (Method method : allMethods)
            {
                String name = method.getName();
                
                if ((!name.startsWith(SET_PREFIX) && !name.startsWith(GET_PREFIX))
                        || (prefix != null && !name.startsWith(prefix)))
                {
                    continue;
                }
                
                List<Method> list = methodMaps.get(name);
                if (list == null)
                {
                    list = new ArrayList<>();
                    methodMaps.put(name, list);
                }
                list.add(method);
            }
        }
        // We now want to sort and choose one method per name and get it from
        // the last class in the hierarchy chain
        MethodComparator comparator = new MethodComparator();
        Set<Method> methods = new HashSet<>();
        for (List<Method> methodList : methodMaps.values())
        {
            Collections.sort(methodList, comparator);
            methods.add(methodList.get(0));
        }
        
        // We now want to filter ignored methods
        for (Iterator<Method> iter = methods.iterator(); iter.hasNext(); )
        {
            Method method = iter.next();
            int modifier = method.getModifiers();
            if (Modifier.isStatic(modifier)
                    || (!Modifier.isPublic(modifier) && !allowNonPublic)
                    || (ignoredMethod(method) && !includeIgnored))
            {
                iter.remove();
            }
        }
        return methods;
    }
    
    private static Set<Class<?>> getCandidateClasses(Class<?> clazz,
                                                     boolean includeInterfaces)
    {
        List<Class<?>> classes = new ArrayList<>(100);
        Set<Class<?>> processed = new HashSet<>();
        classes.add(clazz);
        
        while (classes.isEmpty() == false)
        {
            Class<?> current = classes.remove(0);
            if (isValidClass(current.getSuperclass())) {
                classes.add(current.getSuperclass());
            }
            if (includeInterfaces)
            {
                for (Class<?> interfaze : current.getInterfaces())
                {
                    if (isValidClass(interfaze)) {
                        classes.add(interfaze);
                    }
                }
            }
            processed.add(current);
        }
        return processed;
    }
    
    private static boolean ignoredMethod(Method method)
    {
        Class<?> clazz = method.getDeclaringClass();
        JsonIgnoreProperties ignore = clazz.getAnnotation(JsonIgnoreProperties.class);
        if (ignore == null) {
            return false;
        }
        String propertyName = convertMethodNameToPropertyName(method.getName());
        for (String ignoredName : ignore.value())
        {
            if (propertyName.equals(ignoredName)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isValidClass(Class<?> clazz)
    {
        if (clazz == null || clazz == Class.class || clazz == Object.class) {
            return false;
        }
        return true;
    }
    
    public static Map<String, Set<Method>> getAllGetAndSetMethodsAsMap(Class<?> clazz,
                                                                       boolean includeInterfaces,
                                                                       boolean allowNonPublic,
                                                                       boolean includeIgnored)
    {
        Set<Method> methods =
            getAllSetAndGetMethods(clazz, includeInterfaces, allowNonPublic, includeIgnored, null);
        return getAllGetAndSetMethodsAsMap(methods);
    }
    
    public static Map<String, Set<Method>> getAllGetAndSetMethodsAsMap(Set<Method> allMethods)
    {
        Map<String, Set<Method>> map = new HashMap<>();
        for (Method method : allMethods)
        {
            String name = method.getName();
            Set<Method> methods = map.get(name);
            if (methods == null) {
                methods = new HashSet<>();
                map.put(name, methods);
            }
            methods.add(method);
        }
        return map;
    }
    
    public static String convertMethodNameToPropertyName(String methodName)
    {
        String name = methodName;
        name = name.substring(4);
        return "" + Character.toLowerCase(methodName.charAt(3)) + name;
    }
    
    public static String convertMethodNameToFieldName(String methodName)
    {
        String name = methodName;
        name = name.substring(3);
        StringBuilder b = new StringBuilder();
        b.append("FIELD");
        for (int i = 0; i < name.length(); i ++)
        {
            char ch = name.charAt(i);
            if (Character.isUpperCase(ch)) { 
                b.append("_");
            }
            b.append(Character.toUpperCase(ch));
        }
        return b.toString();
    }
    
    private static class MethodComparator implements Comparator<Method>
    {
        @Override
        public int compare(Method o1, Method o2)
        {
            // Give o1 is smaller if it is assignable from o2 (i.e. it is a super class)
            int value = (o1.getDeclaringClass().isAssignableFrom(o2.getDeclaringClass())) ? 1 : -1;
            return value;
        }
        
    }
}
