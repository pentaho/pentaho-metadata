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
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.mql.MQLQuery;
import org.pentaho.pms.mql.MappedQuery;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessColumnString;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.OrderBy;
import org.pentaho.pms.schema.SchemaMeta;
import org.pentaho.pms.schema.WhereCondition;
import org.pentaho.pms.schema.dialog.OrderByDialog;
import org.pentaho.pms.schema.dialog.WhereConditionsDialog;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.Settings;

import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.Row;
import be.ibridge.kettle.core.database.Database;
import be.ibridge.kettle.core.database.DatabaseMeta;
import be.ibridge.kettle.core.dialog.EnterSelectionDialog;
import be.ibridge.kettle.core.dialog.EnterTextDialog;
import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.core.dialog.PreviewRowsDialog;
import be.ibridge.kettle.core.util.EnvUtil;
import be.ibridge.kettle.core.value.Value;

public class MakeSelectionDemo
{
    public static void main(String[] args) throws Exception
    {
        // Initialisation stuff for SWT and Kettle, ignore it for Web-based reporting.
        //
        CWM.getDomainNames();
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
            System.out.println(Messages.getString("MakeSelectionDemo.ERROR_0001_CANT_LOAD_PREVIOUS_QUERY", Const.getQueryFile(), e.toString())); //$NON-NLS-1$
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
                System.out.println(Messages.getString("MakeSelectionDemo.ERROR_0002_CANT_SAVE_QUERY", Const.getQueryFile(),e.toString())); //$NON-NLS-1$ 
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

        // What domains do we have in the repository?
        String[] domains = CWM.getDomainNames();

        // Let the user select which domain to use.
        String domainName = null;
        if( domains != null && domains.length == 1 ) {
        		domainName = domains[0];
        } else {
            EnterSelectionDialog modelSelectionDialog = new EnterSelectionDialog(shell, domains, Messages.getString("MakeSelectionDemo.USER_SELECT_DOMAIN"), Messages.getString("MakeSelectionDemo.USER_SELECT_DOMAIN")); //$NON-NLS-1$ //$NON-NLS-2$
            if (previous!=null) {
                // What was the previous domain name?
                String previousDomain = previous.getSchemaMeta().getDomainName();
                int idx = Const.indexOfString(previousDomain, domains);
                if (idx>=0) modelSelectionDialog.setSelectedNrs(new int[] { idx }); // Select this
            }
            
            domainName = modelSelectionDialog.open();
        }
        if (domainName!=null)
        {
            // Select/pre-load the domain from the meta-data repository
            CWM cwm = CWM.getInstance(domainName);
            
            // Load it all into memory because the CWM is waaaay to complex to deal with directly ;-)
            // If you are interested in the gory details, look at the CwmSchemaFactory class.
            // It is not overly complex, but it is boring stuff.
            //
            CwmSchemaFactoryInterface cwmSchemaFactory = Settings.getCwmSchemaFactory();
            SchemaMeta schemaMeta = cwmSchemaFactory.getSchemaMeta(cwm);
            
            //String[] usedLocale = schemaMeta.getUsedLocale();
            
            // Select a locale...
            String[] locales = new String[] { "en_US" }; //$NON-NLS-1$
            String selectedLocale = null;
            if( locales != null && locales.length == 1 ) {
            		selectedLocale = locales[0];
            } else {
            		EnterSelectionDialog localeSelectionDialog = new EnterSelectionDialog(shell, locales, Messages.getString("MakeSelectionDemo.USER_SELECT_LOCALE"), Messages.getString("MakeSelectionDemo.USER_SELECT_LOCALE_TO_USE")); //$NON-NLS-1$ //$NON-NLS-2$
            		selectedLocale = localeSelectionDialog.open();            	
            }
            
            if (selectedLocale!=null)
            {
                schemaMeta.setActiveLocale(selectedLocale);
            }
            
//            QueryDialog queryDialog = new QueryDialog(shell, schemaMeta, previous);
//            previousQuery = queryDialog.open();
            
            
            // Now present the user with a choice of business models...
            //
            String[] businessModelNames = schemaMeta.getBusinessModelIDs();
            String modelName = null;
            if( businessModelNames != null && businessModelNames.length == 1 ) {
            		modelName = businessModelNames[0];
            } else {
                EnterSelectionDialog modelSelectionDialog = new EnterSelectionDialog(shell, businessModelNames, Messages.getString("MakeSelectionDemo.USER_SELECT_BUSINESS_MODEL"), Messages.getString("MakeSelectionDemo.USER_SELECT_BUSINESS_MODEL")); //$NON-NLS-1$ //$NON-NLS-2$
                if (previous!=null)
                {
                    // What was the previous business model?
                    String previousModelName = previous.getModel().getDisplayName(selectedLocale);
                    int idx = Const.indexOfString(previousModelName, businessModelNames);
                    if (idx>=0)
                    {
                        modelSelectionDialog.setSelectedNrs(new int[] { idx } ); // Select it
                    }
                }
                modelName = modelSelectionDialog.open();
            }
            if (modelName!=null)
            {
                BusinessModel businessModel = schemaMeta.findModel(modelName); // This is the business model that was selected.
                
                System.out.println(Messages.getString("MakeSelectionDemo.INFO_FOUND_BUSINESS_MODEL", businessModel.toString())); //$NON-NLS-1$
                System.out.println(Messages.getString("MakeSelectionDemo.INFO_FOUND_CATEGORIES", Integer.toString(businessModel.getRootCategory().nrBusinessCategories()))); //$NON-NLS-1$ 
                System.out.println(Messages.getString("MakeSelectionDemo.INFO_HAS_BUSINESS_TABLES", Integer.toString(businessModel.nrBusinessTables()))); //$NON-NLS-1$ 
                System.out.println(Messages.getString("MakeSelectionDemo.INFO_DESCRIBES_RELATIONSHIPS", Integer.toString(businessModel.nrRelationships()))); //$NON-NLS-1$ 
                
                // Show the "flat" view of categories
                List strings = businessModel.getFlatCategoriesView(schemaMeta.getActiveLocale());
                String[] flatView = BusinessColumnString.getFlatRepresentations(strings);
                
                // Select the columns
                EnterSelectionDialog columnSelectionDialog = new EnterSelectionDialog(shell, flatView, Messages.getString("MakeSelectionDemo.USER_TITLE_SELECT_COLUMNS"), Messages.getString("MakeSelectionDemo.USER_SELECT_COLUMNS")); //$NON-NLS-1$ //$NON-NLS-2$
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
                	MQLQuery query = new MQLQuery( schemaMeta, businessModel, selectedLocale );
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
                    
                    StringBuffer text = new StringBuffer(); 
                    
                    // here is a sample constraint ('where' clause)
//                    query.addConstraint( "CUSTOMERS", "Customer #", "<", "120" );
//                    query.addConstraint(null, "BT_MOVEMENTFACT", "PC_MOVEMENTFACT_SPLIT_COUNT", ">= 100");
                    
                    WhereCondition[] conditions = new WhereCondition[] 
                        { 
                            // new WhereCondition(null, businessModel.findBusinessColumn("PC_MOVEMENTFACT_SPLIT_COUNT"), ">= 100") 
                        };
                    if (previous!=null)
                    {
                        // Perhaps we can recover a couple of constraints?
                        List constraints = previous.getConstraints();
                        conditions = (WhereCondition[]) constraints.toArray(new WhereCondition[constraints.size()]);
                    }
                    WhereConditionsDialog conditionsDialog = new WhereConditionsDialog(shell, businessModel, conditions, schemaMeta.getActiveLocale());
                    conditions = conditionsDialog.open();
                    if (conditions!=null)
                    {
                        query.getConstraints().addAll(Arrays.asList(conditions));
                    }
                    
                    String queryXML = query.getXML();
                    System.out.println( query.getXML() );  
                    
                    MQLQuery query2 = new MQLQuery( queryXML, selectedLocale, cwmSchemaFactory );
                    
                    //String sql = query.getQuery();
                    MappedQuery mappedQuery = query2.getQuery();
                    
                    // This will be the modified query - should it be the "display" query? 
                    String sql = mappedQuery.getQuery();
                    
                    System.out.println( query.getXML() );  
//                    System.out.println( sql );  
//                    System.out.println( sql2 );  
                    
                    // What is the connection information?
                    // We might need that to launch the transformation.
                    //
                    DatabaseMeta databaseMeta = selection[0].getPhysicalColumn().getTable().getDatabaseMeta();

                    text.append(Messages.getString("MakeSelectionDemo.USER_NAME", databaseMeta.getName())).append(Const.CR); //name //$NON-NLS-1$
                    text.append(Messages.getString("MakeSelectionDemo.USER_URL", databaseMeta.getURL())).append(Const.CR);   //url //$NON-NLS-1$
                    text.append(Messages.getString("MakeSelectionDemo.USER_DRIVER", databaseMeta.getDriverClass())).append(Const.CR);   //JDBC driver classname //$NON-NLS-1$
                    text.append("-- ").append(Const.CR);   //$NON-NLS-1$
                    text.append("-------------------------------------------------------------------------- ").append(Const.CR); //$NON-NLS-1$   
                    text.append(Const.CR).append(sql);   
                    
                    EnterTextDialog showSQL = new EnterTextDialog(shell, Messages.getString("MakeSelectionDemo.USER_TITLE_GENERATED_SQL"), Messages.getString("MakeSelectionDemo.USER_GENERATED_SQL"), text.toString(), true); //$NON-NLS-1$ //$NON-NLS-2$
                    showSQL.setReadOnly();
                    showSQL.open();
                    
                    if (!Const.isEmpty(sql))
                    {
                        // Now execute the query:
                        Database database = null;
                        List rows = null;
                        try
                        {
                            String path = ""; //$NON-NLS-1$
                            try {
                                File file = new File( "simple-jndi" ); //$NON-NLS-1$
                                path= file.getCanonicalPath();
                            } catch (Exception e) {
                            	e.printStackTrace();
                            }
                            
                            System.setProperty("java.naming.factory.initial", "org.osjava.sj.SimpleContextFactory"); //$NON-NLS-1$ //$NON-NLS-2$
                            System.setProperty("org.osjava.sj.root", path ); //$NON-NLS-1$ 
                            System.setProperty("org.osjava.sj.delimiter", "/"); //$NON-NLS-1$ //$NON-NLS-2$
                            database = new Database(databaseMeta);
                            database.connect();
                            rows = database.getRows(sql, 1000); // get the first 1000 rows from the query for demo-purposes.
                        }
                        catch(Exception e)
                        {
                            new ErrorDialog(shell, Messages.getString("MakeSelectionDemo.USER_TITLE_ERROR_EXECUTING_QUERY"), Messages.getString("MakeSelectionDemo.USER_ERROR_EXECUTING_QUERY"), e); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        finally
                        {
                            if (database!=null) database.disconnect();
                        }
    
                        // Show the rows in a dialog.
                        if (rows!=null)
                        {
                          //Reinstate the actual "as" column identifiers here, before preview. 
                          if (mappedQuery.getMap() != null){
                            Row row = (Row)rows.get(0);
                            for (int i = 0; i < row.size(); i++){
                              Value value = row.getValue(i);
                              value.setName((String)mappedQuery.getMap().get(row.getValue(i).getName()));
                            }        
                          }
                          PreviewRowsDialog previewRowsDialog = new PreviewRowsDialog(shell, SWT.NONE, Messages.getString("MakeSelectionDemo.USER_FIRST_1000_ROWS"), rows); //$NON-NLS-1$
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
