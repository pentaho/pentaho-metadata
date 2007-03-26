/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
*/
package org.pentaho.pms.test;

import java.util.Collection;
import java.util.Iterator;

import org.pentaho.pms.core.CWM;
import org.pentaho.pms.cwm.pentaho.meta.businessinformation.CwmDescription;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmExpression;
import org.pentaho.pms.cwm.pentaho.meta.core.CwmPackage;
import org.pentaho.pms.cwm.pentaho.meta.relational.CwmColumn;
import org.pentaho.pms.cwm.pentaho.meta.relational.CwmTable;

import be.ibridge.kettle.core.Row;
import be.ibridge.kettle.core.value.Value;

/**
 * Test program for our meta-model driver meta-data implementation...

 * @author Matt
 *
 */
public class TestCWM
{
    public static final String DOMAIN = "SomeDomain";  // The domain name
    
    private static final String TEST_TABLE_NAME = "PentahoTable1";
    
    private static CWM cwm = CWM.getInstance(DOMAIN);

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        TestCWM testCWM = new TestCWM();
        testCWM.storeTable();
        testCWM.readBack();
        //testCWM.removeTable();
        //testCWM.readBack();
        
        String[] domainNames = CWM.getDomainNames();
        for (int i=0;i<domainNames.length;i++)
        {
            System.out.println("Package #"+(i+1)+" found : "+domainNames[i]);
            cwm.removePackage(domainNames[i]);
        }
        
        CWM.quitAndSync();
    }
    
    public void storeTable()
    {
        CwmTable table = cwm.getTable(TEST_TABLE_NAME);
        if (table==null)
        {
            System.out.println("Table ["+TEST_TABLE_NAME+"] not found: creating...");
        }
        else
        {
            System.out.println("Table ["+TEST_TABLE_NAME+"] found: overwriting...");
        }
        cwm.beginTransaction();
        
        Row fields = new Row();
        { 
          Value field = new Value("field1", Value.VALUE_TYPE_STRING   );  
          field.setLength(35);    
          field.setOrigin("field1 description"); 
          fields.addValue(field);
        } 
        { Value field = new Value("field2", Value.VALUE_TYPE_NUMBER   );  field.setLength(7,2);   field.setOrigin("field2 description"); fields.addValue(field); } 
        { Value field = new Value("field3", Value.VALUE_TYPE_INTEGER  );  field.setLength(5);     field.setOrigin("field3 description"); fields.addValue(field); } 
        { Value field = new Value("field4", Value.VALUE_TYPE_DATE     );                          field.setOrigin("field4 description"); fields.addValue(field); } 
        { Value field = new Value("field5", Value.VALUE_TYPE_BIGNUMBER);  field.setLength(52,16); field.setOrigin("field5 description"); fields.addValue(field); } 
        { Value field = new Value("field6", Value.VALUE_TYPE_BOOLEAN  );                          field.setOrigin("field6 description"); fields.addValue(field); } 
        
        table = cwm.createTable(TEST_TABLE_NAME, fields);
        
        // Add descriptions to table and columns...
        
        CwmDescription description = cwm.createDescription("This is a table description");
        cwm.setDescription(table, description);
        
        Collection collection = table.getOwnedElement();
        CwmColumn[] columns = (CwmColumn[]) collection.toArray(new CwmColumn[collection.size()]);
        
        for (int i=0;i<fields.size();i++)
        {
            Value field = fields.getValue(i);
            CwmColumn column = columns[i];
            
            // Add a description to the column
            //
            description = cwm.createDescription(field.getOrigin());
            cwm.setDescription(column, description);
        }
        
        // Try to create a package here...
        CwmPackage p = cwm.createPackage(DOMAIN+" package");
        p.getImportedElement().add(table);
        cwm.setDescription(p, cwm.createDescription("This is a package description for ["+DOMAIN+"]") );
        
        cwm.endTransaction();
        
        System.out.println("Finished writing to table ["+TEST_TABLE_NAME+"].");
    }
    
    public void readBack()
    {
        CwmTable table = cwm.getTable(TEST_TABLE_NAME);
        if (table!=null) 
        {
            System.out.println("Readback found table : "+table.getName());
            CwmDescription[] tableDescription = cwm.getDescription(table);
            for (int i=0;i<tableDescription.length;i++)
            {
                System.out.println("Table description #"+(i+1)+" : "+tableDescription[i].getBody());
            }

            Collection collection = table.getOwnedElement();
            for (Iterator iter = collection.iterator(); iter.hasNext();)
            {
                CwmColumn column = (CwmColumn) iter.next();
                System.out.print("Column: "+column.getName()+", type="+column.getType().getName()+", length="+column.getLength()+", precision="+column.getPrecision());

                // The formula
                CwmExpression expression = column.getInitialValue();
                if (expression!=null)
                {
                    System.out.print(" Formula: "+expression.getBody()+" ("+expression.getLanguage()+")");
                }
                
                // The descriptions
                CwmDescription[] columnDescription = cwm.getDescription(column);
                for (int i=0;i<columnDescription.length;i++)
                {
                    System.out.print(" ["+columnDescription[i].getBody()+"]");
                }
                
                System.out.println();
            }
            
            CwmPackage p = cwm.getPackage(DOMAIN);
            if (p!=null)
            {
                System.out.println("Package found ! --> "+p.getName());
                // do we have a package description?
                CwmDescription description[] = cwm.getDescription(p);
                for (int i=0;i<description.length;i++)
                {
                    System.out.println("Package description #"+(i+1)+" --> "+description[i].getBody());
                }
            }
        }
        else 
        {
            System.out.println("Couldn't find table "+TEST_TABLE_NAME);
        }
    }   
    
    public void removeTable()
    {
        cwm.removeTable(TEST_TABLE_NAME);
    }
 }
