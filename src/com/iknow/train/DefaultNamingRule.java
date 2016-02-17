package com.iknow.train;

/**
 * first character means the class
 */
public class DefaultNamingRule implements ISampleNamingRule
{
    @Override
    public char getClassification(String fileName)
    {
        String sampleName = fileName.substring(0,fileName.indexOf("."));
        char ch= sampleName.charAt(0);
        return ch;
    }
}
