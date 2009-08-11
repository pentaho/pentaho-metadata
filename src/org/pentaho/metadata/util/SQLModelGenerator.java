package org.pentaho.metadata.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.pentaho.commons.connection.IPentahoDataTypes;
import org.pentaho.commons.connection.marshal.MarshallableColumnNames;
import org.pentaho.commons.connection.marshal.MarshallableColumnTypes;
import org.pentaho.commons.connection.marshal.MarshallableResultSet;
import org.pentaho.di.core.Props;
import org.pentaho.metadata.messages.LocaleHelper;
import org.pentaho.metadata.messages.Messages;
import org.pentaho.metadata.model.Category;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalColumn;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.model.LogicalTable;
import org.pentaho.metadata.model.SqlDataSource;
import org.pentaho.metadata.model.SqlPhysicalColumn;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.SqlPhysicalTable;
import org.pentaho.metadata.model.SqlDataSource.DataSourceType;
import org.pentaho.metadata.model.concept.Concept;
import org.pentaho.metadata.model.concept.security.Security;
import org.pentaho.metadata.model.concept.security.SecurityOwner;
import org.pentaho.metadata.model.concept.security.SecurityOwner.OwnerType;
import org.pentaho.metadata.model.concept.types.DataType;
import org.pentaho.metadata.model.concept.types.LocaleType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.model.concept.types.TargetTableType;
import org.pentaho.pms.util.Settings;

public class SQLModelGenerator {
  String modelName;

  MarshallableResultSet resultSet;

  String query;

  String connectionName;

  Boolean securityEnabled;

  List<String> users;

  List<String> roles;

  int defaultAcls;

  String createdBy;

  public SQLModelGenerator() {
    super();
    if (!Props.isInitialized()) {
      Props.init(Props.TYPE_PROPERTIES_EMPTY);
    }
  }

  public SQLModelGenerator(String modelName, String connectionName, MarshallableResultSet resultSet, String query,
      Boolean securityEnabled, List<String> users, List<String> roles, int defaultAcls, String createdBy) {
    if (!Props.isInitialized()) {
      Props.init(Props.TYPE_PROPERTIES_EMPTY);
    }
    this.query = query;
    this.connectionName = connectionName;
    this.resultSet = resultSet;
    this.modelName = modelName;
    this.securityEnabled = securityEnabled;
    this.users = users;
    this.roles = roles;
    this.defaultAcls = defaultAcls;
    this.createdBy = createdBy;
  }

  public MarshallableResultSet getConnection() {
    return resultSet;
  }

  public void setConnection(MarshallableResultSet resultSet) {
    this.resultSet = resultSet;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  private boolean validate() {
    return !StringUtils.isEmpty(this.modelName) && !StringUtils.isEmpty(this.query) && this.resultSet != null;
  }

  public Domain generate() throws SQLModelGeneratorException {
    return generate(this.modelName, this.connectionName, this.resultSet, this.query, this.securityEnabled, this.users,
        this.roles, this.defaultAcls, this.createdBy);
  }

  public Domain generate(String modelName, String connectionName, MarshallableResultSet resultSet, String query,
      Boolean securityEnabled, List<String> users, List<String> roles, int defaultAcls, String createdBy)
      throws SQLModelGeneratorException {

    LocaleType locale = new LocaleType(LocaleHelper.getLocale().toString(), LocaleHelper.getLocale().getDisplayName());
      if (validate()) {
        SqlPhysicalModel model = new SqlPhysicalModel();
        String modelID = Settings.getBusinessModelIDPrefix() + modelName;
        model.setId(modelID);
        model.setName(new LocalizedString(locale.getCode(), modelName));
        SqlDataSource dataSource = new SqlDataSource();
        dataSource.setType(DataSourceType.JNDI);
        dataSource.setDatabaseName(connectionName);
        model.setDatasource(dataSource);
        SqlPhysicalTable table = new SqlPhysicalTable(model);
        table.setId("INLINE_SQL_1"); //$NON-NLS-1$
        model.getPhysicalTables().add(table);
        table.setTargetTableType(TargetTableType.INLINE_SQL);
        table.setTargetTable(query);

        MarshallableColumnNames marshallableColumnNames = null;
        MarshallableColumnTypes marshallableColumnTypes = null;
        String[] columnHeader = null;
        String[] columnType = null;
        try {
          marshallableColumnNames = resultSet.getColumnNames();
          if(marshallableColumnNames != null) {
            columnHeader = marshallableColumnNames.getColumnName();
          }
          marshallableColumnTypes = resultSet.getColumnTypes();
          if(marshallableColumnTypes != null) {
            columnType = marshallableColumnTypes.getColumnType();
          }
          LogicalModel logicalModel = new LogicalModel();
          logicalModel.setPhysicalModel(model);
          logicalModel.setId("MODEL_1"); //$NON-NLS-1$
          logicalModel.setName(new LocalizedString(locale.getCode(), modelName));

          Category mainCategory = new Category(logicalModel);
          String categoryID = Settings.getBusinessCategoryIDPrefix() + modelName;
          mainCategory.setId(categoryID);
          mainCategory.setName(new LocalizedString(locale.getCode(), modelName));

          LogicalTable logicalTable = new LogicalTable(logicalModel, table);
          logicalTable.setId("LOGICAL_TABLE_1"); //$NON-NLS-1$

          logicalModel.getLogicalTables().add(logicalTable);
          for (int i = 0; i < columnHeader.length; i++) {
            SqlPhysicalColumn column = new SqlPhysicalColumn(table);

            // should get unique id here

            column.setId(columnHeader[i]);
            column.setTargetColumn(columnHeader[i]);
            // Get the localized string
            column.setName(new LocalizedString(locale.getCode(), columnHeader[i]));
            // Map the SQL Column Type to Metadata Column Type
            column.setDataType(converDataType(columnType[i]));
            String physicalColumnID = Settings.getPhysicalColumnIDPrefix() + "_" + columnHeader[i]; //$NON-NLS-1$
            column.setId(physicalColumnID);
            table.getPhysicalColumns().add(column);

            LogicalColumn logicalColumn = new LogicalColumn();
            String columnID = Settings.getBusinessColumnIDPrefix();
            logicalColumn.setId(columnID + columnHeader[i]);

            // the default name of the logical column.
            // this inherits from the physical column.
            // logicalColumn.setName(new LocalizedString(columnHeader[i]));

            logicalColumn.setPhysicalColumn(column);
            logicalColumn.setLogicalTable(logicalTable);

            logicalTable.addLogicalColumn(logicalColumn);

            mainCategory.addLogicalColumn(logicalColumn);
          }

          logicalModel.getCategories().add(mainCategory);

          Domain domain = new Domain();
          domain.addPhysicalModel(model);

          if (getCreatedBy() != null) {
            domain.setProperty("created_by", createdBy); //$NON-NLS-1$
          }

          if (isSecurityEnabled()) {
            Security security = new Security();
            for (String user : users) {
              SecurityOwner owner = new SecurityOwner(OwnerType.USER, user);
              security.putOwnerRights(owner, defaultAcls);
            }
            for (String role : roles) {
              SecurityOwner owner = new SecurityOwner(OwnerType.ROLE, role);
              security.putOwnerRights(owner, defaultAcls);
            }
            logicalModel.setProperty(Concept.SECURITY_PROPERTY, security);
          }

          List<LocaleType> locales = new ArrayList<LocaleType>();
          locales.add(locale);
          domain.setLocales(locales);
          domain.addLogicalModel(logicalModel);
          domain.setId(modelName);
          return domain;

        } catch (Exception e) {
          throw new SQLModelGeneratorException(e);
        }
      } else {
        throw new SQLModelGeneratorException(Messages
            .getErrorString("SQLModelGenerator.ERROR_0001_INPUT_VALIDATION_FAILED")); //$NON-NLS-1$
      }
  }

  private static DataType converDataType(String type) {
      if(type.equals(IPentahoDataTypes.TYPE_DECIMAL) || type.equals(IPentahoDataTypes.TYPE_DOUBLE) ||
          type.equals(IPentahoDataTypes.TYPE_INT) || type.equals(IPentahoDataTypes.TYPE_FLOAT) ||type.equals(IPentahoDataTypes.TYPE_LONG)) {
        return DataType.NUMERIC;
      } else if(type.equals(IPentahoDataTypes.TYPE_BOOLEAN)) {
        return DataType.BOOLEAN;
      } else if(type.equals(IPentahoDataTypes.TYPE_DATE)) {
        return DataType.DATE;
      } else if(type.equals(IPentahoDataTypes.TYPE_STRING)) {
        return DataType.STRING;
      } else {
        return DataType.UNKNOWN;
      }
  }

  public void setSecurityEnabled(Boolean securityEnabled) {
    this.securityEnabled = securityEnabled;
  }

  public Boolean isSecurityEnabled() {
    return securityEnabled;
  }

  public void setUsers(List<String> users) {
    this.users = users;
  }

  public List<String> getUsers() {
    return users;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setDefaultAcls(int defaultAcls) {
    this.defaultAcls = defaultAcls;
  }

  public int getDefaultAcls() {
    return defaultAcls;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getConnectionName() {
    return connectionName;
  }

  public void setConnectionName(String connectionName) {
    this.connectionName = connectionName;
  }

  public Boolean getSecurityEnabled() {
    return securityEnabled;
  }
}
