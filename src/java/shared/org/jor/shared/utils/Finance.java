package org.jor.shared.utils;


public class Finance
{
    public static int CENTS_PER_DOLLAR = 100;
    
    public static String formatAsDollarsFromCents(int value)
    {
        int cents = value % 100;
        int dollars = value / 100;
        String valueString = dollars + "";
        int length = valueString.length();
        StringBuilder builder = new StringBuilder(valueString);
        if (length > 3)
        {
            int commaCount = 0;
            while ((commaCount+1) * 3 < length)
            {
                builder.insert(length - ((commaCount + 1) * 3), ",");
                commaCount++;
            }
        }
        
        String centsStr = (cents > 9) ? "" + cents : "0" + cents;
        builder.append(".").append(centsStr);
        return "$" + builder.toString();
    }
    
    public static double PMT(double principalAmount, double interestRatePerYear, double lengthInYears)
    {
        return PMT(principalAmount, interestRatePerYear, lengthInYears, 12);
    }
    
    /** As described by http://oakroadsystems.com/math/loan.htm 
     * 
     *                          periodic rate * principal
     * payment =   ----------------------------------------------------
     *               1 - ((1 + periodic rate) ^ -lifetime payments)
     * 
     * 
     * Mirror: http://web.archive.org/web/20110612090447/http://oakroadsystems.com/math/loan.htm
     */
    public static double PMT(double principalAmount, double interestRatePerYear, double lengthInYears, double paymentsPerYear)
    {
        double interestRatePerPeriod = interestRatePerYear / paymentsPerYear;
        double paymentsOverLifetime = paymentsPerYear * lengthInYears;
        
        return (interestRatePerPeriod * principalAmount) / (1.0 - Math.pow(1.0 + interestRatePerPeriod, -paymentsOverLifetime));
    }

    public static double compounded(double principal, double rate, double compoundings)
    {
        return principal * Math.pow(1.0 + rate, compoundings);
    }
}
