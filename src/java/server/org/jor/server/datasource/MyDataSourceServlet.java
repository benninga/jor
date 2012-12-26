package org.jor.server.datasource;

import javax.servlet.http.HttpServletRequest;

import org.jor.rest.report.ReportResource;
import org.jor.rest.report.ReportResourceImpl;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;
import com.google.visualization.datasource.Capabilities;
import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;

public class MyDataSourceServlet extends DataSourceServlet
{
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger(MyDataSourceServlet.class);

    @Override
    protected boolean isRestrictedAccessMode()
    {
        return true;
    }
    
    @Override
    public Capabilities getCapabilities()
    {
        return Capabilities.ALL;
    }
    
    @Override
    public DataTable generateDataTable(Query query, HttpServletRequest request)
    {
        String queryId = request.getParameter("queryId");
        if (queryId == null)
        {
            LOG.warn("No query id specified. Query id parameter is null");
            return new DataTable();
        }
        
        ReportResource resource = new ReportResourceImpl(queryId);
        return resource.getData(query);
    }

}
