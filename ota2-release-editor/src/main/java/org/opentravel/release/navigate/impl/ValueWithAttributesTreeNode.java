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

package org.opentravel.release.navigate.impl;

import java.util.ArrayList;
import java.util.List;

import org.opentravel.application.common.Images;
import org.opentravel.release.NodeProperty;
import org.opentravel.release.navigate.TreeNode;
import org.opentravel.release.navigate.TreeNodeFactory;
import org.opentravel.schemacompiler.model.TLAttribute;
import org.opentravel.schemacompiler.model.TLIndicator;
import org.opentravel.schemacompiler.model.TLValueWithAttributes;

import javafx.scene.image.Image;

/**
 * Tree node that represents a <code>TLValueWithAttributes</code> instance.
 */
public class ValueWithAttributesTreeNode extends TreeNode<TLValueWithAttributes> {
	
	/**
	 * Constructor that specifies the OTM entity for this node.
	 * 
	 * @param entity  the OTM entity represented by this node
	 * @param factory  the factory that created this node
	 */
	public ValueWithAttributesTreeNode(TLValueWithAttributes entity, TreeNodeFactory factory) {
		super(entity, factory);
	}

	/**
	 * @see org.opentravel.release.navigate.TreeNode#getLabel()
	 */
	@Override
	public String getLabel() {
		return getEntity().getName();
	}

	/**
	 * @see org.opentravel.release.navigate.TreeNode#getIcon()
	 */
	@Override
	public Image getIcon() {
		return Images.vwaIcon;
	}

	/**
	 * @see org.opentravel.release.navigate.TreeNode#getProperties()
	 */
	@Override
	public List<NodeProperty> getProperties() {
		List<NodeProperty> props = new ArrayList<>();
		TLValueWithAttributes vwa = getEntity();
		
		props.add( new NodeProperty( "name", () -> { return vwa.getName(); } ) );
		props.add( new NodeProperty( "description", () -> { return getDescription( vwa ); } ) );
		props.add( new NodeProperty( "parentType", () -> { return getEntityDisplayName( vwa.getParentType() ); } ) );
		return props;
	}

	/**
	 * @see org.opentravel.release.navigate.TreeNode#initializeChildren()
	 */
	@Override
	protected List<TreeNode<?>> initializeChildren() {
		List<TreeNode<?>> children = new ArrayList<>();
		
		for (TLAttribute attribute : getEntity().getAttributes()) {
			children.add( treeNodeFactory.newTreeNode( attribute ) );
		}
		for (TLIndicator indicator : getEntity().getIndicators()) {
			children.add( treeNodeFactory.newTreeNode( indicator ) );
		}
		return children;
	}

}
