/**
 * Copyright (C) 2014 OpenTravel Alliance (info@opentravel.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opentravel.schemacompiler.diff;

import org.opentravel.schemacompiler.model.NamedEntity;

/**
 * Container for all change items identified during the comparison of two entities, as
 * well as the field change sets for the fields that existed in both versions of the
 * entity.
 */
public class EntityChangeSet extends ChangeSet<NamedEntity,EntityChangeItem> {
	
	/**
	 * Constructor that assigns the old and new version of an entity that was modified.
	 * 
	 * @param oldEntity  the old version of the entity
	 * @param newEntity  the new version of the entity
	 */
	public EntityChangeSet(NamedEntity oldEntity, NamedEntity newEntity) {
		super( oldEntity, newEntity );
	}
	
	/**
	 * @see org.opentravel.schemacompiler.diff.ChangeSet#getBookmarkId()
	 */
	public String getBookmarkId() {
		return getBookmarkId( (getNewVersion() != null) ? getNewVersion() : getOldVersion() );
	}

}
