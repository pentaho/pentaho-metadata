package org.pentaho.pms.core.event;

import org.pentaho.pms.util.ObjectAlreadyExistsException;


public interface IDChangedListener
{
    public void IDChanged(IDChangedEvent event) throws ObjectAlreadyExistsException;
}
