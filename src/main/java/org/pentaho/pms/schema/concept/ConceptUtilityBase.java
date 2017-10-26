/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.pms.schema.concept;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.metadata.util.validation.IdValidationUtil;
import org.pentaho.metadata.util.validation.ValidationStatus;
import org.pentaho.pms.core.event.AllowsIDChangeListenersInterface;
import org.pentaho.pms.core.event.IDChangedEvent;
import org.pentaho.pms.core.event.IDChangedListener;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.DefaultProperty;
import org.pentaho.pms.schema.RequiredProperties;
import org.pentaho.pms.schema.concept.types.aggregation.AggregationSettings;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregation;
import org.pentaho.pms.schema.concept.types.aggregation.ConceptPropertyAggregationList;
import org.pentaho.pms.schema.concept.types.bool.ConceptPropertyBoolean;
import org.pentaho.pms.schema.concept.types.datatype.ConceptPropertyDataType;
import org.pentaho.pms.schema.concept.types.datatype.DataTypeSettings;
import org.pentaho.pms.schema.concept.types.fieldtype.ConceptPropertyFieldType;
import org.pentaho.pms.schema.concept.types.fieldtype.FieldTypeSettings;
import org.pentaho.pms.schema.concept.types.number.ConceptPropertyNumber;
import org.pentaho.pms.schema.concept.types.rowlevelsecurity.ConceptPropertyRowLevelSecurity;
import org.pentaho.pms.schema.concept.types.security.ConceptPropertySecurity;
import org.pentaho.pms.schema.concept.types.string.ConceptPropertyString;
import org.pentaho.pms.schema.concept.types.tabletype.ConceptPropertyTableType;
import org.pentaho.pms.schema.concept.types.tabletype.TableTypeSettings;
import org.pentaho.pms.schema.security.RowLevelSecurity;
import org.pentaho.pms.schema.security.Security;
import org.pentaho.pms.util.Const;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.UniqueList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated as of metadata 3.0.
 */
public class ConceptUtilityBase extends ChangedFlag implements AllowsIDChangeListenersInterface {

  // TODO: Create a default locale in the metadata instead of just saying
  // English is the default!
  // NOTE: please see http://jira.pentaho.org/browse/PMD-166 for more
  // information
  private static final String DEFAULT_LOCALE = "en_US"; //$NON-NLS-1$

  private String id;

  private ConceptInterface concept;

  protected transient List<IDChangedListener> idChangedListeners;

  public ConceptUtilityBase() {
    this( null );
  }

  public ConceptUtilityBase( String id ) {
    super();
    this.concept = new Concept();
    this.idChangedListeners = new ArrayList<IDChangedListener>();
    this.id = id;
    addDefaultProperties();
  }

  public boolean equals( Object obj ) {
    if ( obj instanceof ConceptUtilityBase == false ) {
      return false;
    }
    if ( this == obj ) {
      return true;
    }

    if ( !obj.getClass().equals( this.getClass() ) ) {
      return false;
    }

    ConceptUtilityBase rhs = (ConceptUtilityBase) obj;

    String lhsId = null != id ? id.toUpperCase() : null;
    String rhsId = null != rhs.id ? rhs.id.toUpperCase() : null;

    return new EqualsBuilder().append( lhsId, rhsId ).isEquals();
  }

  public int hashCode() {
    String idToHash = null != id ? id.toUpperCase() : null;
    return new HashCodeBuilder( 11, 113 ).append( idToHash ).toHashCode();
  }

  public String toString() {
    return id;
  }

  public boolean hasChanged() {
    if ( concept.hasChanged() ) {
      return true;
    }
    return super.hasChanged();
  }

  public void clearChanged() {
    setChanged( false );
    concept.clearChanged();
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   * @throws ObjectAlreadyExistsException in case the ID already exists in a parent list (UniqueList)
   */
  public void setId( String id ) throws ObjectAlreadyExistsException {
    // Verify uniqueness BEFORE we change the ID!
    // We do this by calling the changed listener.
    // The verifyer needs to be somewhere else.
    // That is because we don't want this class to "know" about the parent
    // list in which it is contained.
    //
    for ( int i = 0; i < idChangedListeners.size(); i++ ) {
      IDChangedListener listener = (IDChangedListener) idChangedListeners.get( i );
      try {
        listener.IDChanged( new IDChangedEvent( this.id, id, this ) );
      } catch ( ObjectAlreadyExistsException e ) {
        throw new ObjectAlreadyExistsException( Messages.getString(
          "ConceptUtilityBase.ERROR_0001_OBJECT_ID_EXISTS", id ), e ); //$NON-NLS-1$
      }
    }
    this.id = id;
    setChanged();
  }

  /**
   * @return The concept
   */
  public ConceptInterface getConcept() {
    return concept;
  }

  /**
   * @param concept The concept to set
   */
  public void setConcept( ConceptInterface concept ) {
    this.concept = concept;
    setChanged();
  }

  /**
   * Returns the display name for the concept using the specified locale. If the concept doesn't have a display name
   * given the specified locale, the default locale will be used
   *
   * @param locale The prefered locale to go for
   * @return The localized name or the id if nothing was found for that or the default locale
   */
  public String getDisplayName( String locale ) {
    // The display name is currently the same as the regular name
    return getName( locale );
  }

  /**
   * Set the localized name of the object
   *
   * @param locale The prefered locale to go for
   * @param name   the name to set
   */
  public void setName( String locale, String name ) {
    concept.setName( locale, name );
  }

  /**
   * Returns the name for the concept using the specified locale. If the concept doesn't have a name given the specified
   * locale, the default locale will be used
   *
   * @param locale The prefered locale to go for
   * @return The localized name or the id if nothing was found for that or the default locale
   */
  public String getName( String locale ) {
    String name = internalGetName( locale );
    // If the name is empty and the default locale isn't virtually the same
    // as the locale...
    if ( Const.isEmpty( name ) && !DEFAULT_LOCALE.startsWith( locale ) ) {
      name = internalGetName( DEFAULT_LOCALE );
    }
    return ( Const.isEmpty( name ) ? id : name );
  }

  /**
   * Used to check for the concept's name by using recursively less specific locales
   */
  private String internalGetName( String locale ) {
    String name = concept.getName( locale );
    if ( Const.isEmpty( name ) && locale != null && locale.indexOf( '_' ) > 0 ) {
      name = internalGetName( locale.substring( 0, locale.lastIndexOf( '_' ) ) );
    }
    return name;
  }

  /**
   * Set the localized description of the object
   *
   * @param locale      The prefered locale to go for
   * @param description the description to set
   */
  public void setDescription( String locale, String description ) {
    concept.setDescription( locale, description );
  }

  /**
   * @param locale The prefered locale to go for
   * @return The localized description or null if nothing was found for that locale
   */
  public String getDescription( String locale ) {
    String description = concept.getDescription( locale );
    // If the description is empty and the default locale isn't virtually
    // the same as the locale...
    if ( Const.isEmpty( description ) && DEFAULT_LOCALE.indexOf( locale ) != 0 ) {
      description = internalGetDescription( DEFAULT_LOCALE );
    }
    return ( Const.isEmpty( description ) ? id : description );
  }

  /**
   * Used to look for the description using recursively less specific locale information
   */
  private String internalGetDescription( String locale ) {
    String description = concept.getDescription( locale );
    if ( Const.isEmpty( description ) && locale != null && locale.indexOf( '_' ) > 0 ) {
      description = internalGetDescription( locale.substring( 0, locale.lastIndexOf( '_' ) ) );
    }
    return description;
  }

  public String getTargetSchema() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.TARGET_SCHEMA.getId() );
    if ( property != null ) {
      return property.toString(); // It's a String, nothing fancy here...
    }
    return null;
  }

  public void setTargetSchema( String targetSchema ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.TARGET_SCHEMA.getId() );
    if ( null != property ) {
      property.setValue( targetSchema );
    } else {
      concept.addProperty( new ConceptPropertyString( DefaultPropertyID.TARGET_SCHEMA.getId(), targetSchema ) );
    }
  }

  public String getTargetTable() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.TARGET_TABLE.getId() );
    if ( property != null ) {
      return property.toString(); // It's a String, nothing fancy here...
    }
    return null;
  }

  public void setTargetTable( String targetTable ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.TARGET_TABLE.getId() );
    if ( null != property ) {
      property.setValue( targetTable );
    } else {
      concept.addProperty( new ConceptPropertyString( DefaultPropertyID.TARGET_TABLE.getId(), targetTable ) );
    }
  }

  public String getMask() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.MASK.getId() );
    if ( property != null ) {
      return property.toString(); // It's a String, nothing fancy here...
    }
    return null;
  }

  public void setMask( String mask ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.MASK.getId() );
    if ( null != property ) {
      property.setValue( mask );
    } else {
      concept.addProperty( new ConceptPropertyString( DefaultPropertyID.MASK.getId(), mask ) );
    }
  }

  public TableTypeSettings getTableType() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.TABLE_TYPE.getId() );
    if ( property == null ) {
      return TableTypeSettings.OTHER;
    } else {
      return (TableTypeSettings) property.getValue();
    }
  }

  public void setTableType( TableTypeSettings type ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.TABLE_TYPE.getId() );
    if ( null != property ) {
      property.setValue( type );
    } else {
      concept.addProperty( new ConceptPropertyTableType( DefaultPropertyID.TABLE_TYPE.getId(), type ) );
    }
  }

  // ConceptPropertyInterface property =
  // concept.getChildProperty(DefaultPropertyID..getId());
  // if (null != property) {
  // property.setValue(type);
  // } else {
  // concept.addProperty(new (DefaultPropertyID..getId(), type));
  // }

  public boolean isDimensionTable() {
    return getTableType().isDimension();
  }

  public boolean isFactTable() {
    return getTableType().isFact();
  }

  public int getRelativeSize() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.RELATIVE_SIZE.getId() );
    if ( property != null ) {
      BigDecimal value = (BigDecimal) property.getValue();
      if ( value != null ) {
        return value.intValue();
      }
    }
    return -1;
  }

  public void setRelativeSize( int size ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.RELATIVE_SIZE.getId() );
    if ( null != property ) {
      property.setValue( new BigDecimal( size ) );
    } else {
      concept.addProperty( new ConceptPropertyNumber( DefaultPropertyID.RELATIVE_SIZE.getId(), size ) );
    }
  }

  public String getFormula() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.FORMULA.getId() );
    if ( property == null ) {
      return null;
    }
    return (String) property.getValue();
  }

  public void setFormula( String formula ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.FORMULA.getId() );
    if ( null != property ) {
      property.setValue( formula );
    } else {
      concept.addProperty( new ConceptPropertyString( DefaultPropertyID.FORMULA.getId(), formula ) );
    }
  }

  public FieldTypeSettings getFieldType() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.FIELD_TYPE.getId() );
    if ( property == null || property.getValue() == null ) {
      return FieldTypeSettings.OTHER;
    }
    return (FieldTypeSettings) property.getValue();
  }

  /**
   * @param fieldType the column type to set
   */
  public void setFieldType( FieldTypeSettings fieldType ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.FIELD_TYPE.getId() );
    if ( null != property ) {
      property.setValue( fieldType );
    } else {
      concept.addProperty( new ConceptPropertyFieldType( DefaultPropertyID.FIELD_TYPE.getId(), fieldType ) );
    }
  }

  public String getFieldTypeDesc() {
    return getFieldType().getDescription();
  }

  public void setAggregationType( AggregationSettings aggregationType ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.AGGREGATION.getId() );
    if ( null != property ) {
      property.setValue( aggregationType );
    } else {
      concept.addProperty( new ConceptPropertyAggregation( DefaultPropertyID.AGGREGATION.getId(), aggregationType ) );
    }
  }

  public AggregationSettings getAggregationType() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.AGGREGATION.getId() );
    if ( property == null || property.getValue() == null ) {
      return AggregationSettings.NONE;
    }
    return (AggregationSettings) property.getValue();
  }

  public String getAggregationTypeDesc() {
    return getAggregationType().getDescription();
  }

  public void setAggregationList( List<AggregationSettings> aggregationList ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.AGGREGATION_LIST.getId() );
    if ( null != property ) {
      property.setValue( aggregationList );
    } else {
      concept.addProperty( new ConceptPropertyAggregationList( DefaultPropertyID.AGGREGATION_LIST.getId(),
        aggregationList ) );
    }
  }

  @SuppressWarnings( "unchecked" )
  public List<AggregationSettings> getAggregationList() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.AGGREGATION_LIST.getId() );
    if ( property == null || property.getValue() == null ) {
      return null;
    }
    return (List<AggregationSettings>) property.getValue();
  }

  public void setHidden( boolean hidden ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.HIDDEN.getId() );
    if ( null != property ) {
      property.setValue( new Boolean( hidden ) );
    } else {
      concept.addProperty( new ConceptPropertyBoolean( DefaultPropertyID.HIDDEN.getId(), hidden ) );
    }
  }

  public void flipHidden() {
    setHidden( !isHidden() );
  }

  public boolean isHidden() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.HIDDEN.getId() );
    if ( property == null || property.getValue() == null ) {
      return false;
    }
    return ( (Boolean) property.getValue() ).booleanValue();
  }

  public boolean isExact() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.EXACT.getId() );
    if ( property == null || property.getValue() == null ) {
      return false;
    }
    return ( (Boolean) property.getValue() ).booleanValue();
  }

  public void setExact( boolean exact ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.EXACT.getId() );
    if ( null != property ) {
      property.setValue( new Boolean( exact ) );
    } else {
      concept.addProperty( new ConceptPropertyBoolean( DefaultPropertyID.EXACT.getId(), exact ) );
    }
  }

  public void flipExact() {
    setExact( !isExact() );
  }

  public boolean isFactField() {
    return getFieldType().isFact();
  }

  public boolean isDimensionField() {
    return getFieldType().isDimension();
  }

  public boolean isAttributeField() {
    return getFieldType().isDimension();
  }

  public boolean hasAggregate() {
    return !getAggregationType().equals( AggregationSettings.NONE );
  }

  public DataTypeSettings getDataType() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.DATA_TYPE.getId() );
    if ( property == null || property.getValue() == null ) {
      return DataTypeSettings.UNKNOWN;
    }
    return (DataTypeSettings) property.getValue();
  }

  public void setDataType( DataTypeSettings dataType ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.DATA_TYPE.getId() );
    if ( null != property ) {
      property.setValue( dataType );
    } else {
      concept.addProperty( new ConceptPropertyDataType( DefaultPropertyID.DATA_TYPE.getId(), dataType ) );
    }
  }

  public Security getSecurity() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.SECURITY.getId() );
    if ( property == null || property.getValue() == null ) {
      return new Security();
    }
    return (Security) property.getValue();
  }

  public void setSecurity( Security security ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.SECURITY.getId() );
    if ( null != property ) {
      property.setValue( security );
    } else {
      concept.addProperty( new ConceptPropertySecurity( DefaultPropertyID.SECURITY.getId(), security ) );
    }
  }

  public RowLevelSecurity getRowLevelSecurity() {
    ConceptPropertyInterface property = concept.getProperty( DefaultPropertyID.ROW_LEVEL_SECURITY.getId() );
    if ( property == null || property.getValue() == null ) {
      return new RowLevelSecurity();
    }
    return (RowLevelSecurity) property.getValue();
  }

  public void setRowLevelSecurity( RowLevelSecurity rowLevelSecurity ) {
    ConceptPropertyInterface property = concept.getChildProperty( DefaultPropertyID.ROW_LEVEL_SECURITY.getId() );
    if ( null != property ) {
      property.setValue( rowLevelSecurity );
    } else {
      concept.addProperty( new ConceptPropertyRowLevelSecurity( DefaultPropertyID.ROW_LEVEL_SECURITY.getId(),
        rowLevelSecurity ) );
    }
  }

  public void addIDChangedListener( IDChangedListener listener ) {
    idChangedListeners.add( listener );
  }

  public static IDChangedListener createIDChangedListener( final UniqueList uniqueList ) {
    return new IDChangedListener() {
      public void IDChanged( IDChangedEvent event ) throws ObjectAlreadyExistsException {
        if ( event.newID == null && event.oldID == null ) {
          return;
        }
        if ( event.newID == null ) {
          return;
        }

        ValidationStatus validationStatus = IdValidationUtil.validateId( event.newID );
        if ( validationStatus.statusEnum == ValidationStatus.StatusEnum.INVALID ) {
          throw new IllegalArgumentException( validationStatus.getLocalizedMessage() );
        }

        if ( event.newID.equals( event.oldID ) ) {
          // Id is valid and has not changed
          return;
        }

        // The ID has changed
        // See if the new ID conflicts with one in the list...
        //
        if ( event.object instanceof ConceptUtilityBase ) {
          // Look for the new ID
          for ( int i = 0; i < uniqueList.size(); i++ ) {
            ConceptUtilityBase base = (ConceptUtilityBase) uniqueList.get( i );
            if ( base.getId().equals( event.newID ) ) {
              // This is a problem...
              throw new ObjectAlreadyExistsException( Messages.getString(
                "ConceptUtilityBase.ERROR_0001_OBJECT_ID_EXISTS", event.newID ) ); //$NON-NLS-1$
            }
          }
        }
      }
    };
  }

  protected void addDefaultProperties() {
    java.util.List list = new RequiredProperties().getDefaultProperties( getClass() );
    if ( null != list ) {
      for ( int i = 0; i < list.size(); i++ ) {
        DefaultProperty defaultProperty = (DefaultProperty) list.get( i );
        ConceptPropertyInterface prop =
          DefaultPropertyID.getDefaultEmptyProperty( defaultProperty.getConceptPropertyType(), defaultProperty
            .getName() );
        prop.setRequired( true );
        concept.addProperty( prop );
      }
    }
  }

}
