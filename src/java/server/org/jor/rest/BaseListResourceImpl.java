package org.jor.rest;

import org.jor.server.rest.FreeMarkerHelper;
import org.jor.server.services.db.DataHelper;
import org.jor.server.services.db.DataService;
import org.jor.server.services.db.ObjectJDO;
import org.jor.utils.JaxbList;
import org.jor.utils.XmlUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public abstract class BaseListResourceImpl<T extends ObjectJDO> implements BaseListResource<T>
{
    protected static final String PAGE_NUMBER = "page";
    
    
    @Context
    private UriInfo uriInfo;
    
    @GET 
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML + ";qs=4")
    @Override
    public Response getHtml()
    {
        String page = internalGetHtml();
        return Response.ok().entity(page).build();
    }
    
    protected String internalGetHtml()
    {
        MultivaluedMap<String, String> queryParams = getUriInfo().getQueryParameters();
        String pageNumberStr = queryParams.getFirst(PAGE_NUMBER);
        Integer pageNumber = 0;
        Integer firstRow = 0;
        Integer maxResults = 50;
        if (pageNumberStr != null)
        {
            pageNumber = Integer.parseInt(pageNumberStr);
            queryParams.remove(PAGE_NUMBER);
        }
        firstRow = pageNumber * maxResults;
        String nextPageUri = buildNextPageUri(pageNumber + 1, queryParams);
        
        String orderBy = null;
        
        List<Map<String, Object>> configs =
            DataHelper.getAllObjectsAsMap(getResourceType(), queryParams, orderBy, firstRow, 50);

        Map<String, Object> root = new HashMap<>();
        
        root.put("objects", configs);
        root.put("nextPageUri", nextPageUri);
        root.put("currentPage", pageNumber);
        String page = FreeMarkerHelper.processTemplateFile(getResourceTemplateFile(), root);
        
        return page;
    }
    
    private String buildNextPageUri(Integer nextPage, MultivaluedMap<String, String> queryParams)
    {
        UriBuilder b = getUriInfo().getAbsolutePathBuilder();
        for (String name : queryParams.keySet())
        {
            if (PAGE_NUMBER.equals(name)) {
                continue;
            }
            for (String value : queryParams.get(name))
            {
                if (value != null && value.isEmpty() == false) {
                    b.queryParam(name, value);
                }
            }
        }
        b.queryParam(PAGE_NUMBER, nextPage);
        URI uri = b.build();
        return uri.toASCIIString();
    }

    protected abstract String getResourceTemplateFile();
    
    protected abstract T copyConstructor(T object);
    
    protected UriInfo getUriInfo()
    {
        return uriInfo;
    }

    protected abstract Class<T> getResourceType();
    
    @GET
    @Consumes({APPLICATION_JAVA, MediaType.APPLICATION_JSON})
    @Produces({APPLICATION_JAVA, MediaType.APPLICATION_JSON})
    @Override
    public Object[] get()
    {
        List<T> objects = internalGet();
        List<Object> castObjects = new ArrayList<>(objects.size());
        for (T object : objects) {
            castObjects.add(object);
        }
        return castObjects.toArray();
    }
    
    protected List<T> internalGet()
    {
        Class<T> clazz = getResourceType();
        MultivaluedMap<String, String> queryParams = getUriInfo().getQueryParameters();
        DataService service = DataService.getDataService();
        service.begin();
        List<T> objects = DataHelper.getAllObjects(clazz, queryParams);
        // We need to do a copy constructor call to get rid of Hibernate lazy loading and data structures
        List<T> copiedObjects = new ArrayList<>(objects.size());
        for (T object : objects) {
            copiedObjects.add(copyConstructor(object));
        }
        service.commit();
        return copiedObjects;
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML + ";qs=3")
    @Override
    public String getXML()
    {
        List<T> objects = internalGet();
        JaxbList<T> list = new JaxbList<>(objects);
        return XmlUtils.getString(list, JaxbList.class, getResourceType());
    }
}
