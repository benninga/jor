package org.jor.rest.project;

import static org.jor.rest.RestConstants.ID;
import static org.jor.shared.api.rest.ProjectResourceConstants.SINGLE_PROJECT_PATH;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.jor.rest.BaseListResource;
import org.jor.rest.BaseSingleResourceImpl;
import org.jor.server.services.db.DataService;
import org.jor.server.services.db.ObjectJDO;
import org.jor.server.services.user.SecurityPolicy;
import org.jor.shared.security.Action;

@Path(SINGLE_PROJECT_PATH + "/{" + ID + "}")
public class ProjectResourceImpl extends BaseSingleResourceImpl<ObjectJDO> implements ProjectResource
{

    @Override
    protected String getResourceTemplateFile()
    {
        return PROJECT_PAGE_FTL;
    }

    @Override
    protected ObjectJDO createOrUpdate(ObjectJDO project)
    {
            DataService service = DataService.getDataService();
            // Start transaction. Ensure that on save error, user does not lose credit points.
            service.begin();
            project = super.createOrUpdate(project);
            service.commit();
        
        return project;
    }

    @Override
    protected ObjectJDO getNewObject()
    {
        return null;
    }

    @Override
    protected ObjectJDO getExistingObject(Long id)
    {
    	return null;
//        return new Project(DataHelper.getObject(Project.class, id));
    }

    @Override
    protected Class<? extends BaseListResource<ObjectJDO>> getListResource()
    {
        return ProjectListResourceImpl.class;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Override
    @SecurityPolicy(actions=Action.CREATE)
    public ObjectJDO create(JAXBElement<ObjectJDO> jaxbObject)
    {
        return super.baseXMLCreate(jaxbObject.getValue());
    }

	@Override
	protected ObjectJDO copyConstructor(ObjectJDO item) {
		// TODO Auto-generated method stub
		return null;
	}
}
