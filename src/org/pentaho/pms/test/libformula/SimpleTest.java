package org.pentaho.pms.test.libformula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.function.SQLFunction;
import org.jfree.formula.EvaluationException;
import org.jfree.formula.LibFormulaBoot;
import org.jfree.formula.function.FunctionDescription;
import org.jfree.formula.function.FunctionRegistry;
import org.jfree.formula.lvalues.FormulaFunction;
import org.jfree.formula.lvalues.LValue;
import org.jfree.formula.lvalues.TypeValuePair;
import org.jfree.formula.parser.FormulaParser;
import org.jfree.formula.parser.ParseException;

import be.ibridge.kettle.core.Row;
import be.ibridge.kettle.core.exception.KettleException;
import be.ibridge.kettle.core.value.Value;

public class SimpleTest
{
    private RowForumulaContext context;
    
    public SimpleTest(Row row)
    {
        context = new RowForumulaContext(row);
        
        LibFormulaBoot.getInstance().start();
    }

    /**
     * @return the context
     */
    public RowForumulaContext getContext()
    {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(RowForumulaContext context)
    {
        this.context = context;
    }

    public void runTest() throws ParseException, EvaluationException
    {
        FormulaParser parser = new FormulaParser();
        FunctionRegistry functionRegistry = context.getFunctionRegistry();
        Locale locale = Locale.ENGLISH; 

        String strFormula = "LEFT( TRIM( [name] ) ; 3 )";
        
        LValue x = parser.parse(strFormula);

        if (x instanceof FormulaFunction)
        {
            FormulaFunction formulaFunction = (FormulaFunction) x;
            System.out.println("Function detected: "+formulaFunction.getFunctionName());
            
            // OK, look in the function registry...
            FunctionDescription metaData = functionRegistry.getMetaData(formulaFunction.getFunctionName());
            System.out.println("  Description      : "+metaData.getDescription(locale));
            System.out.println("  Display name     : "+metaData.getDisplayName(locale));
            System.out.println("  Nr of parameters : "+metaData.getParameterCount());

            MySQLDialect mysqlDialect = new MySQLDialect();
            Map hFunctions = mysqlDialect.getFunctions();
            Collection col = hFunctions.values();
            for (Iterator iter = col.iterator(); iter.hasNext();)
            {
                SQLFunction sqlFunction = (SQLFunction) iter.next();
                System.out.println("hibernate function found: "+sqlFunction);                
            }
            
            /*
            Function[] functions = functionRegistry.getFunctions();
            for (int i = 0; i < functions.length; i++)
            {
                Function function = functions[i];
                System.out.println("  #"+(i+1)+" : "+function.getCanonicalName());
            }
            */

            /*
            LValue[] childValues = x.getChildValues();
            for (int i=0;i<childValues.length;i++)
            {
                if (childValues[i] instanceof FormulaFunction)
                {
                    System.out.println("  Child value #"+i+" : "+((FormulaFunction)childValues[i]).getFunctionName());
                }
                else
                {
                    System.out.println("  Child value #"+i+" : "+childValues[i]);
                }
            }
            */
        }
        
        x.initialize(context);
        System.out.println (x);
        TypeValuePair result = x.evaluate();
        System.out.println("result of formula: ["+strFormula+"] = "+result.getValue());
    }

    public static void main(String[] args) throws KettleException, ParseException, EvaluationException
    {
        List rows = new ArrayList();
        
        Row r1 = new Row();
        r1.addValue(new Value("year", (long)2007));
        r1.addValue(new Value("month", (long)02));
        r1.addValue(new Value("day", (long)20));
        r1.addValue(new Value("name", "  Casters  "));
        r1.addValue(new Value("firstname", "Matt"));
        Row r2 = new Row();
        r2.addValue(new Value("year", (long)2006));
        r2.addValue(new Value("month", (long)12));
        r2.addValue(new Value("day", (long)31));
        r2.addValue(new Value("name", "  Bar  "));
        r2.addValue(new Value("firstname", "Foo"));
        
        rows.add(r1);
        rows.add(r2);
        
        SimpleTest test = new SimpleTest(r1);
        test.runTest();
        
        test.getContext().setRow(r2);
        test.runTest();
        
    }
    
    
}
