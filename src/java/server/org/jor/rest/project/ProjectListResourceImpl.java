package org.jor.rest.project;

import static org.jor.shared.api.rest.ProjectResourceConstants.PROJECT_PATH;

import javax.ws.rs.Path;

import org.jor.rest.BaseListResourceImpl;
import org.jor.server.services.db.ObjectJDO;


@Path(PROJECT_PATH )
public class ProjectListResourceImpl extends BaseListResourceImpl<ObjectJDO>
{
    @Override
    protected String getResourceTemplateFile()
    {
        return PROJECT_LIST_PAGE_FTL;
    }

    @Override
    protected Class<ObjectJDO> getResourceType()
    {
        return ObjectJDO.class;
    }
//    
//    @GET
//    @Path(HEADERS_PATH)
//    @Produces({APPLICATION_JAVA, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//    public List<ModelObject> getHeaders()
//    {
//        
//        String[] columns = new String[] { FIELD_ID, FIELD_NAME, FIELD_DESCRIPTION,
//                                          FIELD_LAST_UPDATE_AT, FIELD_CREATED_BY, };
//        MultivaluedMap<String, String> queryParams = getUriInfo().getQueryParameters();
//        List<ModelObject> headers = DataHelper.getHeaders(getResourceType(), columns, queryParams);
//        return headers;
//    }
    
//    @GET
//    @Path(HEADERS_PATH + "/xml")
//    @Produces(MediaType.APPLICATION_XML)
//    public String getHeadersXml()
//    {
//        List<ModelObject> headers = getHeaders();
//        JaxbList<ModelObject> list = new JaxbList<ModelObject>(headers);
//        return XmlUtils.getString(list, JaxbList.class, ModelObject.class);
//    }

    @Override
    public ObjectJDO copyConstructor(ObjectJDO project)
    {
        return null;
    }
}
