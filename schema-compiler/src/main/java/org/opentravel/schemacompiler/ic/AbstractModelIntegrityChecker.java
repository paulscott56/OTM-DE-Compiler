
package org.opentravel.schemacompiler.ic;

import java.util.ArrayList;
import java.util.List;

import org.opentravel.schemacompiler.event.ModelEvent;
import org.opentravel.schemacompiler.event.ModelEventListener;

/**
 * Event listener that encapsulates a number of nested listeners responsible for maintaining
 * the integrity of a <code>TLModel</code> as changes are performed by an editor or owning
 * applicaiton.
 * 
 * @author S. Livezey
 */
public abstract class AbstractModelIntegrityChecker implements ModelEventListener<ModelEvent<Object>,Object> {

	private List<ModelEventListener<?,?>> listeners;
	
	/**
	 * Default constructor.
	 */
	public AbstractModelIntegrityChecker() {
		listeners = getListeners();
	}
	
	/**
	 * Returns the collection of listeners that should be invoked to ensure the integrity of a
	 * <code>TLModel</code> instance.  By default, this method returns an empty list.  Sub-classes
	 * should override and add required listeners to the list.
	 * 
	 * @return ModelEventListener<?,?>
	 */
	protected List<ModelEventListener<?,?>> getListeners() {
		return new ArrayList<ModelEventListener<?,?>>();
	}
	
	/**
	 * @see org.opentravel.schemacompiler.event.ModelEventListener#processModelEvent(org.opentravel.schemacompiler.event.ModelEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void processModelEvent(ModelEvent<Object> event) {
		for (ModelEventListener<?,?> listener : listeners) {
			if (event.canBeProcessedBy(listener)) {
				((ModelEventListener<ModelEvent<Object>,?>) listener).processModelEvent(event);
			}
		}
	}

	/**
	 * @see org.opentravel.schemacompiler.event.ModelEventListener#getEventClass()
	 */
	@Override
	public Class<ModelEvent<Object>> getEventClass() {
		return null;
	}

	/**
	 * @see org.opentravel.schemacompiler.event.ModelEventListener#getSourceObjectClass()
	 */
	@Override
	public Class<Object> getSourceObjectClass() {
		return null;
	}
	
}