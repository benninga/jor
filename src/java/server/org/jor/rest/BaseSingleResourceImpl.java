package org.jor.rest;

import org.jor.model.user.User;
import org.jor.server.rest.FreeMarkerHelper;
import org.jor.server.rest.SecurityResourceFilter;
import org.jor.server.services.db.DataService;
import org.jor.server.services.db.ObjectJDO;
import org.jor.server.services.security.CheckPoint;
import org.jor.server.services.security.SecurityManager;
import org.jor.server.services.user.SecurityPolicy;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;
import org.jor.shared.security.Action;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

@Produces(MediaType.TEXT_HTML)
public abstract class BaseSingleResourceImpl<T extends ObjectJDO> implements BaseSingleResource<T>
{
    private static final Logger LOG = LoggerFactory.getLogger(BaseSingleResourceImpl.class);
    
    @DefaultValue(GET_OPERATION) @QueryParam(OPERATION_TAG)
    private String operationVal;
    
    @Context
    private UriInfo uriInfo;
    
    @PathParam(ID)
    private String objectId;
    
    private T object;
    
    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Override
    public Response getHtml()
    {
        if (DELETE_OPERATION.equals(operationVal))
        {
            return delete();
        }
        String page = internalGetHtml();
        return Response.ok().entity(page).build();
    }
    
    @GET
    @Produces({APPLICATION_JAVA, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public T get()
    {
        if (CREATE_TAG.equals(getObjectId()))
        {
            object = getNewObject();
        }
        else if (object == null)
        {
            if (getObjectId() == null || "null".equals(getObjectId())) {
                throw new NullPointerException("Object ID is not specified");
            }
            object = getExistingObject(Long.parseLong(getObjectId()));
        }
        return object;
    }
    
    @GET
    @Path("json")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public T getJSON()
    {
        return get();
    }

    @GET
    @Path("xml")
    @Produces(MediaType.APPLICATION_XML)
    @Override
    public T getXML()
    {
        return get();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Override
    public final Response update(MultivaluedMap<String, String> form)
    {
        return createOrUpdate(form);
    }
    
    @POST
    @Consumes({APPLICATION_JAVA, MediaType.APPLICATION_JSON})
    @Produces({APPLICATION_JAVA, MediaType.APPLICATION_JSON})
    @Override
    @SecurityPolicy(actions={Action.CREATE, Action.UPDATE})
    public final T update(T _object)
    {
        return createOrUpdate(_object);
    }
    
    @DELETE
    @Produces(MediaType.TEXT_HTML)
    public Response delete()
    {
        deleteObject();
        return Response.seeOther(getListURI()).build();
    }
    
    @DELETE
    @Produces({APPLICATION_JAVA, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void deleteObject()
    {
        DataService service = DataService.getDataService();
        T _object = get();
        service.delete(_object);
    }
    
    protected T baseXMLCreate(T _object)
    {
        DataService service = DataService.getDataService();
        T updated = service.save(_object);
        String msg =
            String.format("Created %s with key: %s", updated.getClass().getName(), updated.getKey().getId());
        LOG.info(msg);
        return updated;
    }
    
    protected String internalGetHtml()
    {
        get(); // also updates local reference
        Map<String, Object> root = new HashMap<>();
        Map<String, Object> info = FreeMarkerHelper.replaceNullWithEmptyString(getObject().entityToMap());
        root.put("object", info);
        String page = FreeMarkerHelper.processTemplateFile(getResourceTemplateFile(), root);
        return page;
    }
    
    protected abstract String getResourceTemplateFile();
    
    protected abstract T getNewObject();
    
    protected abstract T getExistingObject(Long id);

    protected abstract Class<? extends BaseListResource<T>> getListResource();
    
    protected abstract T copyConstructor(T item);
    
    public abstract T create(JAXBElement<T> jaxbObject);
    
    protected static <T extends ObjectJDO> boolean isNew(T _object)
    {
        return (_object.getKey() == null) || (_object.getKey().getId() == null);
    }
    
    protected Response createOrUpdate(MultivaluedMap<String, String> form)
    {
        T _object = get();
        _object.objectFromMap(form);
        createOrUpdate(_object);
        URI uri = getURI();
        return Response.seeOther(uri).entity(internalGetHtml()).build();
    }
    
    protected T createOrUpdate(T _object)
    {
        DataService service = DataService.getDataService();
        boolean createNew = isNew(_object);
        T updated = service.save(_object);
        setObject(updated);
        if (createNew) // create
        {
            setObjectId(Long.toString(updated.getKey().getId()));
        }
        return copyConstructor(updated);
    }
    
    protected URI getURI()
    {
        UriBuilder ub = UriBuilder.fromResource(this.getClass());
        URI uri = ub.build(getObjectId());
        return uri;
    }
    
    protected URI getListURI()
    {
        UriBuilder ub = UriBuilder.fromResource(getListResource());
        URI uri = ub.build(getObjectId());
        return uri;
    }
    
    protected void setUriInfo(UriInfo info)
    {
        this.uriInfo = info;
    }
    
    protected UriInfo getUriInfo()
    {
        return this.uriInfo;
    }
    
    public void setObjectId(String _objectId)
    {
        this.objectId = _objectId;
    }
    
    protected String getObjectId()
    {
        return this.objectId;
    }
    
    protected T getObject()
    {
        return this.object;
    }
    
    protected void setObject(T _object)
    {
        this.object = _object;
    }
    
    protected void checkAccess(String methodPath, Action ... actions)
    {
        Path resourcePath = this.getClass().getAnnotation(Path.class);
        String resourcePathValue = (resourcePath == null) ? null : resourcePath.value();
        String path = SecurityResourceFilter.constructRestPath(resourcePathValue, methodPath);
        
        CheckPoint point = new CheckPoint();
        point.addResources(path);
        point.addActions(actions);
        
        SecurityResourceFilter.checkAccess(point);
    }
    
    protected static User getCurrentUser()
    {
        return SecurityManager.getSecurityManager().getCurrentUser(); 
    }
}
