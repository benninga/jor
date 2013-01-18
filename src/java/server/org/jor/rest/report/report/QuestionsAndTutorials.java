package org.jor.rest.report.report;

import java.util.List;

import org.jor.server.services.db.DataService;

import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

public class QuestionsAndTutorials extends BaseReport
{
    private String questionsQuery =
            "SELECT week_of_year || '_' || year AS week_num, COUNT(id) AS question_count"
          + " FROM"
          + " ("
          + "     SELECT CAST (EXTRACT(doy FROM created_at) / 7 AS integer) AS week_of_year,"
          + "            EXTRACT(year FROM created_at) AS year, id"
          + "     FROM questions"
          + "     WHERE created_at > now() - interval '60 days'"
          + " ) a"
          + " GROUP BY week_of_year, year"
          + " ORDER BY year ASC, week_of_year ASC";

    private String answersQuery =
            " SELECT week_of_year || '_' || year AS week_num, COUNT(id) AS answer_count"
            + " FROM"
            + " ("
            + "     SELECT CAST (EXTRACT(doy FROM created_at) / 7 AS integer) AS week_of_year,"
            + "            EXTRACT(year FROM created_at) AS year, id"
            + "     FROM answers"
            + "     WHERE created_at > now() - interval '60 days'"
            + " ) a"
            + " GROUP BY week_of_year, year"
            + " ORDER BY year ASC, week_of_year ASC";

    private String tutorialsQuery =
            " SELECT week_of_year || '_' || year AS week_num, COUNT(id) AS tutorial_count"
          + " FROM"
          + " ("
          + "     SELECT DISTINCT CAST (EXTRACT(doy FROM created_at) / 7 AS integer) AS week_of_year,"
          + "                     EXTRACT(year FROM created_at) AS year, id"
          + "     FROM steps"
          + "     WHERE created_at > now() - interval '60 days'"
          + " ) a"
          + " GROUP BY week_of_year, year"
          + " ORDER BY year ASC, week_of_year ASC";
    
    public QuestionsAndTutorials(Query query)
    {
        super(query);
    }

    @Override
    public DataTable getData()
    {
        DataService service = DataService.getDataService(PROD_POSTGRES_DB);
        List<Object[]> questionRows = service.runSQLQuery(questionsQuery);
        List<Object[]> answerRows = service.runSQLQuery(answersQuery);
        List<Object[]> tutorialRows = service.runSQLQuery(tutorialsQuery);
     
        // FIXME: We can do some better error handling in case we have no results (i.e. week with no answes)
        // For now, Sanity check, we assume we'll get some results for each week from all three queries.
        if (questionRows.size() != answerRows.size() || questionRows.size() != tutorialRows.size()) {
            throw new RuntimeException(String.format("Mismatched results. Questions %d, Answers %d, Tutorials %d",
                                                     questionRows.size(), answerRows.size(), tutorialRows.size()));
        }

        addColumn("week_num", ValueType.TEXT, "Week Number");
        addColumn("question_count", ValueType.NUMBER, "Question Count");
        addColumn("answer_count", ValueType.NUMBER, "Answer Count");
        addColumn("tutorial_count", ValueType.NUMBER, "Tutorial Count");
        
        for (int i = 0; i < questionRows.size(); i ++)
        {
            Object[] questionRow = questionRows.get(i);
            addRow(questionRow[0], questionRow[1], answerRows.get(i)[1], tutorialRows.get(i)[1]);
        }
        return getTable();
    }

}
