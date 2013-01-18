package org.jor.rest.report;

import static org.jor.shared.api.rest.CommonConstants.ID_PATH;
import static org.jor.shared.api.rest.report.ReportResourceConstants.REPORT_PATH;

import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jor.rest.report.report.ActivityTypeDistribution;
import org.jor.rest.report.report.QuestionsAndTutorials;
import org.jor.rest.report.report.UserDistributionByCountry;
import org.jor.rest.report.report.activity.InsiderActivityWeeklyCohorts;
import org.jor.rest.report.report.activity.UserActivityCohorts;
import org.jor.rest.report.report.growth.ConfirmedUsersRateByMonth;
import org.jor.rest.report.report.growth.NewUsersByWeek;
import org.jor.rest.report.report.sales.InsiderActivityLast14Days;
import org.jor.rest.report.report.usage.MonthlyActiveUsers;
import org.jor.shared.api.rest.report.ReportResourceConstants;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableCell;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.query.Query;
import com.google.visualization.datasource.render.HtmlRenderer;
import com.ibm.icu.util.ULocale;

@Path(REPORT_PATH  + ID_PATH)
public class ReportResourceImpl implements ReportResource, ReportResourceConstants
{
    private static final Logger LOG = LoggerFactory.getLogger(ReportResourceImpl.class);
    
    @PathParam(ID)
    private String reportId;
    
    public ReportResourceImpl()
    {
        reportId = null;
    }
    
    public ReportResourceImpl(String reportId)
    {
        this.reportId = reportId;
    }
    
    @Override
    public DataTable getData()
    {
        return getData(null);
    }
    
    @Override
    public DataTable getData(Query query)
    {
        LOG.info("Processing for report id: " + reportId);
        query = (query == null) ? new Query() : query;
        
        // FIXME: Better mapping needed
        if ("1".equals(reportId)) {
            return new UserDistributionByCountry(query).getData();
        } else if ("2".equals(reportId)) {
            return new NewUsersByWeek(query).getData();
        }
        else if ("3".equals(reportId)) {
            return new ActivityTypeDistribution(query).getData();
        }
        else if ("4".equals(reportId)) {
            return new InsiderActivityLast14Days(query).getData();
        }
        else if ("5".equals(reportId)) {
            return new UserActivityCohorts(query).getData();
        }
        else if ("6".equals(reportId)) {
            return new QuestionsAndTutorials(query).getData();
        }
        else if ("7".equals(reportId)) {
            return new InsiderActivityWeeklyCohorts(query).getData();
        }
        else if ("8".equals(reportId)) {
            return new ConfirmedUsersRateByMonth(query).getData();
        }
        else if ("9".equals(reportId)) {
            return new MonthlyActiveUsers(query).getData();
        }
        return new DataTable();
    }
    
    @GET
    @Path(HTML_PATH)
    @Produces({ MediaType.TEXT_HTML })
    public String getHtml()
    {
        LOG.info("Processing HTML for report id: " + reportId);
        DataTable data = getData();
        String html = HtmlRenderer.renderDataTable(data, ULocale.US).toString();
        return html;
    }
    
    @GET
    @Path(CSV_PATH)
    @Produces({ MediaType.TEXT_PLAIN})
    public String getCsv()
    {
        LOG.info("Processing CSV for report id: " + reportId);
        DataTable data = getData();
        
        StringWriter out = new StringWriter();
        CSVWriter writer = new CSVWriter(out);
        int columnCount = data.getNumberOfColumns();
        
        // Add the headers to the CSV data
        String[] headers = new String[columnCount];
        for (int i = 0; i < columnCount; i ++)
        {
            String columnLabel = data.getColumnDescription(i).getLabel();
            headers[i] = columnLabel;
        }
        writer.writeNext(headers);
        
        // Add the actual rows.
        String[] rowData = new String[columnCount];
        for (TableRow row : data.getRows())
        {
            int i = 0;
            for (TableCell cell : row.getCells())
            {
                String cellValue = cell.getValue().toString();
                rowData[i ++] = cellValue;
            }
            writer.writeNext(rowData);
        }
        return out.toString();
    }
}
