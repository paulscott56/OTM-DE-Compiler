
package org.opentravel.schemacompiler.transform.tl2jaxb;

import org.opentravel.ns.ota2.librarymodel_v01_04.Documentation;
import org.opentravel.ns.ota2.librarymodel_v01_04.Extension;
import org.opentravel.schemacompiler.model.NamedEntity;
import org.opentravel.schemacompiler.model.TLDocumentation;
import org.opentravel.schemacompiler.model.TLExtension;
import org.opentravel.schemacompiler.transform.ObjectTransformer;
import org.opentravel.schemacompiler.transform.symbols.SymbolResolverTransformerContext;
import org.opentravel.schemacompiler.transform.util.BaseTransformer;

/**
 * Handles the transformation of objects from the <code>TLExtension</code> type to the
 * <code>Extension</code> type.
 *
 * @author S. Livezey
 */
public class TLExtensionTransformer extends BaseTransformer<TLExtension,Extension,SymbolResolverTransformerContext> {

	/**
	 * @see org.opentravel.schemacompiler.transform.ObjectTransformer#transform(java.lang.Object)
	 */
	@Override
	public Extension transform(TLExtension source) {
		Extension extension = new Extension();
		
		if (source.getExtendsEntity() != null) {
			NamedEntity extendsEntity = source.getExtendsEntity();
			
			extension.setExtends( context.getSymbolResolver().buildEntityName(
					extendsEntity.getNamespace(), extendsEntity.getLocalName()) );
			
		} else {
			extension.setExtends( source.getExtendsEntityName() );
		}
		
		if ((source.getDocumentation() != null) && !source.getDocumentation().isEmpty()) {
			ObjectTransformer<TLDocumentation,Documentation,SymbolResolverTransformerContext> docTransformer =
					getTransformerFactory().getTransformer(TLDocumentation.class, Documentation.class);
			
			extension.setDocumentation( docTransformer.transform(source.getDocumentation()) );
		}
		return extension;
	}
	
}