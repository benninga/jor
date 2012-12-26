package org.jor.server.services.db;

import static org.jor.shared.api.IModelObject.FIELD_DESCRIPTION;
import static org.jor.shared.api.IModelObject.FIELD_ID;
import static org.jor.shared.api.IModelObject.FIELD_NAME;
import static org.jor.shared.api.IModelObject.FIELD_CREATED_BY;
import static org.jor.shared.api.IModelObject.FIELD_LAST_UPDATE_AT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.jor.jdo.ModelObject;
import org.jor.rest.RestConstants;
import org.jor.server.rest.FreeMarkerHelper;
import org.jor.server.services.db.SimplifiedQuery.Filter;
import org.jor.server.services.db.SimplifiedQuery.OP;
import org.jor.server.services.db.SimplifiedQuery.Param;

public class DataHelper
{
    public static <T extends ObjectJDO> List<Map<String, Object>> getAllObjectsAsMap(Class<T> clazz)
    {
        return getAllObjectsAsMap(clazz, null);
    }
    
    public static <T extends ObjectJDO> List<Map<String, Object>> getAllObjectsAsMap(Class<T> clazz,
                                                                                     MultivaluedMap<String, String> queryParams)
    {
        return getAllObjectsAsMap(clazz, queryParams, null, null, null);
    }
    
    public static <T extends ObjectJDO> List<Map<String, Object>> getAllObjectsAsMap(Class<T> clazz,
                                                                                     MultivaluedMap<String, String> queryParams,
                                                                                     String orderBy,
                                                                                     Integer firstResult, Integer maxResults)
    {
        Collection<T> entities = getAllObjects(clazz, queryParams, orderBy, firstResult, maxResults);
        
        List<Map<String, Object>> results = new ArrayList<>(50);
        for (T entity : entities)
        {
            Map<String, Object> result = entity.entityToMap();
            result = FreeMarkerHelper.replaceNullWithEmptyString(result);
            results.add(result);
        }
        return results;
    }
    
    public static <T extends ObjectJDO> List<T> getAllObjects(Class<T> clazz,
                                                              MultivaluedMap<String, String> queryParams)
    {
        return getAllObjects(clazz, queryParams, null, null, null);
    }
    
    public static <T extends ObjectJDO> List<T> getAllObjects(Class<T> clazz,
                                                              MultivaluedMap<String, String> queryParams,
                                                              String orderBy, Integer firstResult, Integer maxResults)
    {
        DataService service = DataService.getDataService();
        
        List<Filter> filters = new ArrayList<>();
        Map<String, Object> parameterValues = new HashMap<>();
        
        String orderByQuery = queryParams.getFirst(RestConstants.ORDER_BY);
        if (orderBy == null) {
            orderBy = orderByQuery;
        }
        
        if (queryParams != null)
        {
            processParameterValues(queryParams, filters, parameterValues);
        }
        List<T> results = service.getAllObjects(clazz, filters, parameterValues,
                                                orderBy, firstResult, maxResults);
        return results;
    }
    
    public static void processParameterValues(final MultivaluedMap<String, String> queryParams,
                                              final List<Filter> filters,
                                              final Map<String, Object> parameterValues)
    {
        if (queryParams == null) {
            return;
        }

        int counter = 0;
        for (String columnName : queryParams.keySet())
        {
            String value = queryParams.getFirst(columnName);
            if (value == null || value.isEmpty()) {
                continue;
            }
            if (RestConstants.ORDER_BY.equals(columnName))
            {
                continue;
            }
            if (RestConstants.CLEAR_CACHE.equals(columnName))
            {
                continue;
            }
            else
            {
                String paramName = "p" + (++counter);
                Filter filter = new Filter(columnName, OP.EQ, new Param(paramName));
                filters.add(filter);
                Object valueObj = getParameterObjectValue(value);
                parameterValues.put(paramName, valueObj);
            }
        }
    }
    
    public static List<ModelObject> getHeaders(Class<? extends ObjectJDO> clazz,
                                               String[] columns,
                                               MultivaluedMap<String, String> queryParams)
    {
        DataService service = DataService.getDataService();
        // Start by creating the query
        SimplifiedQuery query = service.createSimpleQuery(clazz);
        
        List<Filter> filters = new ArrayList<>();
        Map<String, Object> parameterValues = new HashMap<>();
        
        
        if (queryParams != null)
        {
            DataHelper.processParameterValues(queryParams, filters, parameterValues);
            for (Filter filter : filters)
            {
                query.addFilter(filter);
            }
            // Update the ORDER By
            String orderBy = queryParams.getFirst(RestConstants.ORDER_BY);
            query.setOrderBy(orderBy);
        }
        
        query.setSelectColumns(columns);
        
        List<Object[]> results = query.execute(parameterValues);
        List<ModelObject> headers = new ArrayList<>();
        
        // Create the model objects
        for (Object[] result : results)
        {
            Map<String, Object> valueMap = new HashMap<>();
            for (int i = 0; i < columns.length; i ++)
            {
                valueMap.put(columns[i], result[i]);
            }
            
            ModelObject object =
                new ModelObject((Long)valueMap.get(FIELD_ID),
                                (String)valueMap.get(FIELD_NAME),
                                (String)valueMap.get(FIELD_DESCRIPTION),
                                (Date)valueMap.get(FIELD_LAST_UPDATE_AT),
                                (Long)valueMap.get(FIELD_CREATED_BY));
            headers.add(object);
        }
        return headers;
    }
    
    private static Object getParameterObjectValue(String value)
    {
        // NOTE: We need a better way to know the type of a variable
        Object valueObj = value;
        try
        {
            valueObj = Long.parseLong(value);
        }
        catch (NumberFormatException e) {
            // Keep value as string
        }
        return valueObj;
    }
    
    public static <T extends ObjectJDO> T getObject(Class<T> clazz, Long objectId)
    {
        DataService service = DataService.getDataService();
        T item = service.getObject(clazz, new ObjectKey(objectId));
        if (item == null) {
            throw new RuntimeException(String.format("No object of type %s with ID %s", clazz.getName(), objectId));
        }
        return item;
    }
    
    public static <T extends ObjectJDO> List<Map<String, String>> getHeaders(Class<T> clazz)
    {
        DataService service = DataService.getDataService();
        List<Object> resultObjects = service.runQuery(clazz, new String[] { FIELD_ID, FIELD_NAME, FIELD_DESCRIPTION });
        List<Object[]> results = new ArrayList<>(resultObjects.size());
        for (Object obj : resultObjects) {
            results.add((Object[])obj);
        }
        
        List<Map<String, String>> headers = new ArrayList<>();
        for (Object[] result : results)
        {
            Map<String, String> header = new HashMap<>();
            header.put(FIELD_ID, "" + result[0]);
            header.put(FIELD_NAME, "" + result[1]);
            header.put(FIELD_DESCRIPTION, "" + result[2]);
            headers.add(header);
        }
        return headers;
    }
}
