package org.jor.utils;


import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class TestJsonUtils
{
    @Test
    public void testBasicSerialization()
    {
        MyBean bean = new MyBean();
        bean.setName("my bean name");
        bean.setAge(73);
        String json = JsonUtils.getString(bean);
        MyBean updated = JsonUtils.fromString(json, MyBean.class);
        String updatedJson = JsonUtils.getString(updated);
        
        Assert.assertEquals("Same Json string", json, updatedJson);
        Assert.assertEquals("Same name", bean.getName(), updated.getName());
        Assert.assertEquals("Same age", bean.getAge(), updated.getAge());
    }
    
    @Test
    public void testBasicDateAndLongSerialization()
    {
        MyDateClass bean = new MyDateClass();
        bean.setToday(new Date(1320459551497L));
        bean.setMyLong(new Long(34));
        bean.setMyInteger(new Integer(15));
        
        String json = JsonUtils.getString(bean);
        MyDateClass converted = JsonUtils.fromString(json, MyDateClass.class);
        
        Assert.assertEquals("Raw JSON is wrong. Should have quotes on date",
                            "{\"today\":\"1320459551497\",\"myLong\":\"34\",\"myInteger\":15}", json);
        Assert.assertEquals("Not same date!", bean.getToday().getTime(), converted.getToday().getTime());
        Assert.assertEquals("Not same long", bean.getMyLong(), converted.getMyLong());
        Assert.assertEquals("Not same integer", bean.getMyInteger(), converted.getMyInteger());
        
        
        // Test serializing null dates
        bean = new MyDateClass();
        bean.setToday(null);
        bean.setMyLong(null);
        bean.setMyInteger(null);
        
        json = JsonUtils.getString(bean);
        converted = JsonUtils.fromString(json, MyDateClass.class);
        
        Assert.assertEquals("Raw JSON is wrong. Should have quotes on date",
                            "{\"today\":null,\"myLong\":null,\"myInteger\":null}", json);
        Assert.assertNull("Date should be null", bean.getToday());
        Assert.assertNull("Long should be null", bean.getMyLong());
        Assert.assertNull("Integer should be null", bean.getMyInteger());
    }
    
    private static class MyBean
    {
        private String name;
        private int age;
        
        public String getName()
        {
            return name;
        }
        public void setName(String name)
        {
            this.name = name;
        }
        public int getAge()
        {
            return age;
        }
        public void setAge(int age)
        {
            this.age = age;
        }
    }
    
    public static class MyDateClass implements IMyDateClass
    {
        private Date today;
        private Long myLong;
        private Integer myInteger;
        
        @Override public Date getToday()
        {
            return today;
        }
        
        @Override public void setToday(Date today)
        {
            this.today = today;
        }
        
        @Override public Long getMyLong()
        {
            return myLong;
        }
        
        @Override public void setMyLong(Long myLong)
        {
            this.myLong = myLong;
        }
        
        @Override public Integer getMyInteger()
        {
            return myInteger;
        }
        
        @Override public void setMyInteger(Integer myInteger)
        {
            this.myInteger = myInteger;
        }
    }
    
    private static interface IMyDateClass
    {
        public Date getToday();
        public void setToday(Date today);
        
        public Long getMyLong();
        public void setMyLong(Long myLong);
        
        public Integer getMyInteger();
        public void setMyInteger(Integer myLong);
    }
    
}
