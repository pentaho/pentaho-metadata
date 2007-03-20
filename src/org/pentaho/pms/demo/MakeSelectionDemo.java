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
package org.pentaho.pms.demo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;
import org.pentaho.pms.mql.MQLQuery;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessColumnString;
import org.pentaho.pms.schema.BusinessView;
import org.pentaho.pms.schema.OrderBy;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.WhereCondition;
import org.pentaho.pms.schema.dialog.OrderByDialog;
import org.pentaho.pms.schema.dialog.WhereConditionsDialog;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.Settings;

import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.database.Database;
import be.ibridge.kettle.core.database.DatabaseMeta;
import be.ibridge.kettle.core.dialog.EnterSelectionDialog;
import be.ibridge.kettle.core.dialog.EnterTextDialog;
import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.core.dialog.PreviewRowsDialog;
import be.ibridge.kettle.core.util.EnvUtil;

public class MakeSelectionDemo
{
    public static void main(String[] args) throws Exception
    {
        // Initialisation stuff for SWT and Kettle, ignore it for Web-based reporting.
        //
        CWM.getModelNames();
        Display display = new Display();
        Shell shell = new Shell(display);
        // Make the directory?
        Const.checkPentahoMetadataDirectory();

        EnvUtil.environmentInit();
        Props.init(display, Const.getPropertiesFile());
        Props props = Props.getInstance();
        
        MQLQuery previousQuery = null;
        try
        {
            previousQuery = new MQLQuery( Const.getQueryFile() );
        }
        catch(IOException e)
        {
            System.out.println("Unable to load previous query file from file ["+Const.getQueryFile()+"] : "+e.toString());
        }
        
        previousQuery = executeDemo(shell, props, previousQuery, true);
        if (previousQuery!=null)
        {
            Const.checkPentahoMetadataDirectory();
            try
            {
                previousQuery.save(Const.getQueryFile());
            }
            catch(IOException e)
            {
                System.out.println("Unable to save query to file ["+Const.getQueryFile()+"] : "+e.toString());
            }
        }
    }

    public static final void executeDemo(Shell shell, Props props) throws Exception
    {
        executeDemo(shell, props, null, true);
    }

    public static final MQLQuery executeDemo(Shell shell, Props props, MQLQuery previousQuery, boolean shutdown) throws Exception
    {
        MQLQuery previous = previousQuery;
        previousQuery = null;

        // What models do we have in the repository?
        String[] models = CWM.getModelNames();

        // Let the user select which model to use.
        String modelName = null;
        if( models != null && models.length == 1 ) {
        		modelName = models[0];
        } else {
            EnterSelectionDialog modelSelectionDialog = new EnterSelectionDialog(shell, models, "Select a model", "Select a model");
            if (previous!=null) {
                // What was the previous model name?
                String previousModel = previous.getSchemaMeta().getModelName();
                int idx = Const.indexOfString(previousModel, models);
                if (idx>=0) modelSelectionDialog.setSelectedNrs(new int[] { idx }); // Select this
            }
            
            modelName = modelSelectionDialog.open();
        }
        if (modelName!=null)
        {
            // Select/pre-load the model from the meta-data repository
            CWM cwm = CWM.getInstance(modelName);
            
            // Load it all into memory because the CWM is waaaay to complex to deal with directly ;-)
            // If you are interested in the gory details, look at the CwmSchemaFactory class.
            // It is not overly complex, but it is boring stuff.
            //
            CwmSchemaFactoryInterface cwmSchemaFactory = Settings.getCwmSchemaFactory();
            SchemaMeta schemaMeta = cwmSchemaFactory.getSchemaMeta(cwm);
            
            //String[] usedLocale = schemaMeta.getUsedLocale();
            
            // Select a locale...
            String[] locales = new String[] { "en_US" };
            String selectedLocale = null;
            if( locales != null && locales.length == 1 ) {
            		selectedLocale = locales[0];
            } else {
            		EnterSelectionDialog localeSelectionDialog = new EnterSelectionDialog(shell, locales, "Select locale", "Select the locale to use");
            		selectedLocale = localeSelectionDialog.open();            	
            }
            
            if (selectedLocale!=null)
            {
                schemaMeta.setActiveLocale(selectedLocale);
            }
            
//            QueryDialog queryDialog = new QueryDialog(shell, schemaMeta, previous);
//            previousQuery = queryDialog.open();
            
            
            // Now present the user with a choice of business views...
            //
            String[] businessViewNames = schemaMeta.getViewIDs();
            String viewName = null;
            if( businessViewNames != null && businessViewNames.length == 1 ) {
            		viewName = businessViewNames[0];
            } else {
                EnterSelectionDialog viewSelectionDialog = new EnterSelectionDialog(shell, businessViewNames, "Select a business view", "Select a business view");
                if (previous!=null)
                {
                    // What was the previous business view?
                    String previousViewName = previous.getView().getDisplayName(selectedLocale);
                    int idx = Const.indexOfString(previousViewName, businessViewNames);
                    if (idx>=0)
                    {
                        viewSelectionDialog.setSelectedNrs(new int[] { idx } ); // Select it
                    }
                }
                viewName = viewSelectionDialog.open();
            }
            if (viewName!=null)
            {
                BusinessView businessView = schemaMeta.findView(viewName); // This is the business view that was selected.
                
                System.out.println("Found view named: "+businessView);
                System.out.println("Contains "+businessView.getRootCategory().nrBusinessCategories()+" categories.");
                System.out.println("Has "+businessView.nrBusinessTables()+" business tables.");
                System.out.println("Describes "+businessView.nrRelationships()+" relationships.");
                
                // Show the "flat" view of categories
                List strings = businessView.getFlatCategoriesView(schemaMeta.getActiveLocale());
                String[] flatView = BusinessColumnString.getFlatRepresentations(strings);
                
                // Select the columns
                EnterSelectionDialog columnSelectionDialog = new EnterSelectionDialog(shell, flatView, "Select the columns", "Select a couple of columns to put on the report");
                if (previous!=null)
                {
                    // What was the previous selection?
                    List indexes=new ArrayList();
                    for (int i=0;i<previous.getSelections().size();i++)
                    {
                        BusinessColumn bc = (BusinessColumn) previous.getSelections().get(i);
                        int idx = BusinessColumnString.getBusinessColumnIndex(strings, bc);
                        if (idx>=0)
                        {
                            indexes.add(new Integer(idx));
                        }
                    }
                    
                    int[] idxs=new int[indexes.size()];
                    for (int i=0;i<indexes.size();i++) idxs[i] = ((Integer)indexes.get(i)).intValue();
                    columnSelectionDialog.setSelectedNrs(idxs);  // Select these columns!
                }

                columnSelectionDialog.setMulti(true);
                columnSelectionDialog.setFixed(true);
                if (columnSelectionDialog.open()!=null)
                {
                	MQLQuery query = new MQLQuery( schemaMeta, businessView, selectedLocale );
                    int[] indices = columnSelectionDialog.getSelectionIndeces();
                    List selectionList = new ArrayList();
                    for (int i=0;i<indices.length;i++)
                    {
                        BusinessColumnString bcs = (BusinessColumnString) strings.get(indices[i]);
                        if (bcs.getBusinessColumn()!=null) // Ignore categories themselves if they are selected.
                        {
                            selectionList.add(bcs.getBusinessColumn());
                            query.addSelection( bcs.getBusinessColumn() );
                        }
                    }
                    BusinessColumn selection[] = (BusinessColumn[])selectionList.toArray(new BusinessColumn[selectionList.size()]);

                    OrderBy[] orderBy = null;
                    if (previous!=null && previous.getOrder().size()>0)
                    {
                        // Perhaps we can recover an order by clause?
                        List orderList = previous.getOrder();
                        orderBy = (OrderBy[]) orderList.toArray(new OrderBy[orderList.size()]);
                    }

                    // Now let the user set the ordering too...
                    //
                    OrderByDialog orderByDialog = new OrderByDialog(shell, selection, orderBy, schemaMeta.getActiveLocale());
                    orderBy = orderByDialog.open();
                    if (orderBy!=null)
                    {
                        query.setOrder( Arrays.asList(orderBy) );
                    }
                    
                    String text = "";
                    
                    // here is a sample constraint ('where' clause)
//                    query.addConstraint( "CUSTOMERS", "Customer #", "<", "120" );
//                    query.addConstraint(null, "BT_MOVEMENTFACT", "PC_MOVEMENTFACT_SPLIT_COUNT", ">= 100");
                    
                    WhereCondition[] conditions = new WhereCondition[] 
                        { 
                            // new WhereCondition(null, businessView.findBusinessColumn("PC_MOVEMENTFACT_SPLIT_COUNT"), ">= 100") 
                        };
                    if (previous!=null)
                    {
                        // Perhaps we can recover a couple of constraints?
                        List constraints = previous.getConstraints();
                        conditions = (WhereCondition[]) constraints.toArray(new WhereCondition[constraints.size()]);
                    }
                    WhereConditionsDialog conditionsDialog = new WhereConditionsDialog(shell, businessView, conditions, schemaMeta.getActiveLocale());
                    conditions = conditionsDialog.open();
                    if (conditions!=null)
                    {
                        query.getConstraints().addAll(Arrays.asList(conditions));
                    }
                    
                    String queryXML = query.getXML();
                    System.out.println( query.getXML() );  
                    
                    MQLQuery query2 = new MQLQuery( queryXML, selectedLocale, cwmSchemaFactory );
                    
                    //String sql = query.getQuery();
                    String sql = query2.getQuery( true );
                    
                    System.out.println( query.getXML() );  
//                    System.out.println( sql );  
//                    System.out.println( sql2 );  
                    
                    // What is the connection information?
                    // We might need that to launch the transformation.
                    //
                    DatabaseMeta databaseMeta = selection[0].getPhysicalColumn().getTable().getDatabaseMeta();
                    text+= "-- Name:    "+databaseMeta.getName()+Const.CR;
                    
                    // URL?
                    String url = databaseMeta.getURL();
                    text += "-- URL:    "+url+Const.CR;
                    
                    // JDBC Driver class name?
                    String className = databaseMeta.getDriverClass();
                    text+= "-- Driver: "+className+Const.CR;
                    text+= "-- "+Const.CR;
                    text+= "-------------------------------------------------------------------------- "+Const.CR;
                    text+= Const.CR;
                    text+= sql;
                    
                    EnterTextDialog showSQL = new EnterTextDialog(shell, "The generated SQL", "Here is the generated SQL", text, true);
                    showSQL.setReadOnly();
                    showSQL.open();
                    
                    if (!Const.isEmpty(sql))
                    {
                        // Now execute the query:
                        Database database = null;
                        List rows = null;
                        try
                        {
                            String path = "";
                            try {
                                File file = new File( "simple-jndi" );
                                path= file.getCanonicalPath();
                            } catch (Exception e) {
                            	e.printStackTrace();
                            }
                            
                            System.setProperty("java.naming.factory.initial", "org.osjava.sj.SimpleContextFactory"); //$NON-NLS-1$ //$NON-NLS-2$
                            System.setProperty("org.osjava.sj.root", path ); //$NON-NLS-1$ //$NON-NLS-2$
                            System.setProperty("org.osjava.sj.delimiter", "/"); //$NON-NLS-1$ //$NON-NLS-2$
                            database = new Database(databaseMeta);
                            database.connect();
                            rows = database.getRows(sql, 1000); // get the first 1000 rows from the query for demo-purposes.
                        }
                        catch(Exception e)
                        {
                            new ErrorDialog(shell, "Error executing query", "There was an error executing the query", e);
                        }
                        finally
                        {
                            if (database!=null) database.disconnect();
                        }
    
                        // Show the rows in a dialog.
                        if (rows!=null)
                        {
                            PreviewRowsDialog previewRowsDialog = new PreviewRowsDialog(shell, SWT.NONE, "The first 1000 rows from the generated query", rows);
                            previewRowsDialog.open();
                        }
                    }
                    
                    previousQuery = query2; 
                }
            }
        }
        
        Props.getInstance().saveProps();
        
        if (shutdown) CWM.quitAndSync();
        
        return previousQuery;
    }

}
