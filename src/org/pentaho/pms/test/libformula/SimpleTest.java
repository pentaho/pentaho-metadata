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
import org.pentaho.di.compatibility.Row;
import org.pentaho.di.compatibility.Value;
import org.pentaho.di.core.exception.KettleException;

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

        String strFormula = "LEFT( TRIM( [name] ) ; 3 )"; //$NON-NLS-1$
        
        LValue x = parser.parse(strFormula);

        if (x instanceof FormulaFunction)
        {
            FormulaFunction formulaFunction = (FormulaFunction) x;
            System.out.println("Function detected: "+formulaFunction.getFunctionName()); //$NON-NLS-1$
            
            // OK, look in the function registry...
            FunctionDescription metaData = functionRegistry.getMetaData(formulaFunction.getFunctionName());
            System.out.println("  Description      : "+metaData.getDescription(locale)); //$NON-NLS-1$
            System.out.println("  Display name     : "+metaData.getDisplayName(locale)); //$NON-NLS-1$
            System.out.println("  Nr of parameters : "+metaData.getParameterCount()); //$NON-NLS-1$

            MySQLDialect mysqlDialect = new MySQLDialect();
            Map hFunctions = mysqlDialect.getFunctions();
            Collection col = hFunctions.values();
            for (Iterator iter = col.iterator(); iter.hasNext();)
            {
                SQLFunction sqlFunction = (SQLFunction) iter.next();
                System.out.println("hibernate function found: "+sqlFunction);                 //$NON-NLS-1$
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
        System.out.println("result of formula: ["+strFormula+"] = "+result.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static void main(String[] args) throws KettleException, ParseException, EvaluationException
    {
        List<Row> rows = new ArrayList<Row>();
        
        Row r1 = new Row();
        r1.addValue(new Value("year", (long)2007)); //$NON-NLS-1$
        r1.addValue(new Value("month", (long)02)); //$NON-NLS-1$
        r1.addValue(new Value("day", (long)20)); //$NON-NLS-1$
        r1.addValue(new Value("name", "  Casters  ")); //$NON-NLS-1$ //$NON-NLS-2$
        r1.addValue(new Value("firstname", "Matt")); //$NON-NLS-1$ //$NON-NLS-2$
        Row r2 = new Row();
        r2.addValue(new Value("year", (long)2006)); //$NON-NLS-1$
        r2.addValue(new Value("month", (long)12)); //$NON-NLS-1$
        r2.addValue(new Value("day", (long)31)); //$NON-NLS-1$
        r2.addValue(new Value("name", "  Bar  ")); //$NON-NLS-1$ //$NON-NLS-2$
        r2.addValue(new Value("firstname", "Foo")); //$NON-NLS-1$ //$NON-NLS-2$
        
        rows.add(r1);
        rows.add(r2);
        
        SimpleTest test = new SimpleTest(r1);
        test.runTest();
        
        test.getContext().setRow(r2);
        test.runTest();
        
    }
    
    
}
