package org.jor.server.services.db;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

public class SimplifiedQuery extends Query
{
    private static final String SPACE = " ";
    private Class<?> clazz;
    private String[] selectColumns;
    private List<String> filters;
    private String orderBy;
    
    protected SimplifiedQuery(Session session, Class<?> clazz)
    {
        super(session);
        this.clazz = clazz;
        selectColumns = null;
        filters = new ArrayList<>();
        orderBy = null;
    }
    
    private void getSelectClause(StringBuilder builder)
    {
        if(selectColumns != null)
        {
            builder.append("SELECT ");
            String comma = "";
            for (String column : selectColumns)
            {
                builder.append(comma).append(column);
                comma = ",";
            }
            builder.append(SPACE);
        }
    }
    
    private void getFromClause(StringBuilder builder)
    {
        builder.append("FROM " + clazz.getName());
    }
    
    private void getWhereClause(StringBuilder builder)
    {
        if (filters.size() > 0)
        {
            builder.append(" WHERE");
            String AND = SPACE;
            for(String filter : filters)
            {
                builder.append(AND).append(filter);
                AND = " AND ";
            }
        }
    }
    
    private void getOrderBy(StringBuilder builder)
    {
        if (orderBy == null) {
            return;
        }
        builder.append(" ORDER BY ").append(orderBy);
    }
    
    @Override
    protected String getQueryString()
    {
        StringBuilder builder = new StringBuilder();
        getSelectClause(builder);
        getFromClause(builder);
        getWhereClause(builder);
        getOrderBy(builder);
        String queryStr = builder.toString();
        return queryStr;
    }
    
    public void setSelectColumns(String[] selectColumns)
    {
        this.selectColumns = selectColumns;
    }
    
    public void addFilter(Filter filter)
    {
        this.filters.add(filter.getFilterString());
    }
    
    public void setOrderBy(String orderBy)
    {
        this.orderBy = orderBy;
    }

    public static class Filter
    {
        private String filter;
        private Param param;
        private String primitiveValue;
        
        public Filter(String columnName, OP operand, Param param)
        {
            this.param = param;
            filter = columnName + SPACE + operand.sqlText + " :" + param.paramName;
        }
        
        public Filter(String columnName, OP operand, String primitiveValue)
        {
            this.primitiveValue = primitiveValue;
            filter = columnName + SPACE + operand.sqlText + primitiveValue;
        }
        
        public Param getParam()
        {
            return param;
        }
        
        public String getPrimitiveValue()
        {
            return primitiveValue;
        }
        
        public String getFilterString()
        {
            return filter;
        }
    }
    
    public static class Param
    {
        String paramName;
        public Param(String paramName)
        {
            this.paramName = paramName;
        }
        
        public String getParamName()
        {
            return paramName;
        }
    }
    
    public static enum OP
    {
        
        LT("<"), GT(">"), LTE ("<="), GTE(">="), EQ("="), NE("!="), IN("IN");

        public final String sqlText;
        
        OP(String text)
        {
            this.sqlText = text;
        }
    }
    
}
