package org.jor.shared.api.rest;

public interface ProjectResourceConstants extends CommonConstants
{
    String SAVE_QUERY_PARAM = "save";
    String PLAN_ID_PARAM = "planId";
    
    String PROJECT_PATH = "/project";
    String COMPUTE_BY_GOALS_PATH = "/goals";
    String DEFAULT_PATH = "/default";
    String HEADERS_PATH = "/headers";
    
    String PROJECT_URL = BASE_URL + PROJECT_PATH;
    String DEFAULT_URL = PROJECT_URL + DEFAULT_PATH;
    String HEADERS_URL = PROJECT_URL + HEADERS_PATH;
    
    // URL and Paths for single project
    /**
     * @see org.jor.rest.project.ProjectResourceImpl
     */
    String SINGLE_PROJECT_PATH = PROJECT_PATH + "/single";
    String SINGLE_PROJECT_URL = BASE_URL + SINGLE_PROJECT_PATH;
}
