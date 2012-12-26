package org.jor.rest.project;

import org.jor.rest.BaseSingleResource;
import org.jor.rest.RestConstants;
import org.jor.server.services.db.ObjectJDO;
import org.jor.shared.api.rest.ProjectResourceConstants;

public interface ProjectResource extends BaseSingleResource<ObjectJDO>, RestConstants, ProjectResourceConstants
{
    // Typing interface?
}
