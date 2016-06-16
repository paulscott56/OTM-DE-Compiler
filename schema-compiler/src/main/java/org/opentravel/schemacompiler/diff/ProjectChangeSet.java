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

import java.util.ArrayList;
import java.util.List;

import org.opentravel.schemacompiler.repository.Project;

/**
 * Container for all change items identified during the comparison of two projects, as
 * well as the library change sets for the libraries that existed in both versions of the
 * project.
 */
public class ProjectChangeSet extends ChangeSet {
	
	private Project oldProject;
	private Project newProject;
	private List<ProjectChangeItem> projectChangeItems = new ArrayList<>();
	
	/**
	 * Constructor that assigns the old and new version of a project that was modified.
	 * 
	 * @param oldProject  the old version of the project
	 * @param newProject  the new version of the project
	 */
	public ProjectChangeSet(Project oldProject, Project newProject) {
		this.oldProject = oldProject;
		this.newProject = newProject;
	}

	/**
	 * Returns the old version of the project.
	 *
	 * @return Project
	 */
	public Project getOldProject() {
		return oldProject;
	}

	/**
	 * Returns the new version of the project.
	 *
	 * @return Project
	 */
	public Project getNewProject() {
		return newProject;
	}

	/**
	 * Returns the list of changes between the old and new version of the project.
	 *
	 * @return List<ProjectChangeItem>
	 */
	public List<ProjectChangeItem> getProjectChangeItems() {
		return projectChangeItems;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.diff.ChangeSet#getBookmarkId()
	 */
	public String getBookmarkId() {
		Project project = (newProject != null) ? newProject : oldProject;
		return (project == null) ? "UNKNOWN_PROJECT" : ("prj$" + project.getName());
	}

}
