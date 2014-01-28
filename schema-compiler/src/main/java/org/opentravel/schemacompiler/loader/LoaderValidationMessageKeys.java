
package org.opentravel.schemacompiler.loader;

/**
 * Defines the message keys for all error/warning messages that can be generated by
 * the model loader.
 * 
 * @author S. Livezey
 */
public interface LoaderValidationMessageKeys {
	
	public static final String LOADER_VALIDATION_PREFIX = "schemacompiler.loader.";
	
	public static final String ERROR_MISSING_NAMESPACE_URI                   = LOADER_VALIDATION_PREFIX + "MISSING_NAMESPACE_URI";
	public static final String ERROR_INVALID_NAMESPACE_URI                   = LOADER_VALIDATION_PREFIX + "INVALID_NAMESPACE_URI";
	public static final String ERROR_MISSING_NAMESPACE_URI_ON_IMPORT         = LOADER_VALIDATION_PREFIX + "MISSING_NAMESPACE_URI_ON_IMPORT";
	public static final String ERROR_INVALID_NAMESPACE_URI_ON_IMPORT         = LOADER_VALIDATION_PREFIX + "INVALID_NAMESPACE_URI_ON_IMPORT";
	public static final String ERROR_NAMESPACE_MISMATCH_ON_INCLUDE           = LOADER_VALIDATION_PREFIX + "NAMESPACE_MISMATCH_ON_INCLUDE";
	public static final String ERROR_NAMESPACE_MISMATCH_ON_IMPORT            = LOADER_VALIDATION_PREFIX + "NAMESPACE_MISMATCH_ON_IMPORT";
	public static final String ERROR_ILLEGAL_CHAMELEON_IMPORT                = LOADER_VALIDATION_PREFIX + "ILLEGAL_CHAMELEON_IMPORT";
	public static final String ERROR_INVALID_URL_ON_INCLUDE                  = LOADER_VALIDATION_PREFIX + "INVALID_URL_ON_INCLUDE";
	public static final String ERROR_INVALID_PROJECT_ITEM_LOCATION           = LOADER_VALIDATION_PREFIX + "INVALID_PROJECT_ITEM_LOCATION";
	public static final String ERROR_PROJECT_ITEM_FILE_NOT_FOUND             = LOADER_VALIDATION_PREFIX + "PROJECT_ITEM_FILE_NOT_FOUND";
	public static final String ERROR_UNREADABLE_LIBRARY_CONTENT              = LOADER_VALIDATION_PREFIX + "UNREADABLE_LIBRARY_CONTENT";
	public static final String ERROR_UNREADABLE_SCHEMA_CONTENT               = LOADER_VALIDATION_PREFIX + "UNREADABLE_SCHEMA_CONTENT";
	public static final String ERROR_UNREADABLE_PROJECT_CONTENT              = LOADER_VALIDATION_PREFIX + "UNREADABLE_PROJECT_CONTENT";
	public static final String ERROR_INVALID_REPOSITORY_ITEM                 = LOADER_VALIDATION_PREFIX + "INVALID_REPOSITORY_ITEM";
	public static final String ERROR_MISSING_CRC                             = LOADER_VALIDATION_PREFIX + "MISSING_CRC";
	public static final String ERROR_INVALID_CRC                             = LOADER_VALIDATION_PREFIX + "INVALID_CRC";
	public static final String ERROR_LOADING_FROM_REPOSITORY                 = LOADER_VALIDATION_PREFIX + "ERROR_LOADING_FROM_REPOSITORY";
	public static final String ERROR_REPOSITORY_UNAVAILABLE                  = LOADER_VALIDATION_PREFIX + "REPOSITORY_UNAVAILABLE";
	public static final String ERROR_MANAGED_LIBRARY_NAMESPACE_ERROR         = LOADER_VALIDATION_PREFIX + "MANAGED_LIBRARY_NAMESPACE_ERROR";
	public static final String ERROR_MISSING_REPOSITORY                      = LOADER_VALIDATION_PREFIX + "ERROR_MISSING_REPOSITORY";
	public static final String ERROR_UNKNOWN_EXCEPTION_DURING_PROJECT_LOAD   = LOADER_VALIDATION_PREFIX + "UNKNOWN_EXCEPTION_DURING_PROJECT_LOAD";
	public static final String ERROR_UNKNOWN_EXCEPTION_DURING_MODULE_LOAD    = LOADER_VALIDATION_PREFIX + "UNKNOWN_EXCEPTION_DURING_MODULE_LOAD";
	public static final String ERROR_UNKNOWN_EXCEPTION_DURING_TRANSFORMATION = LOADER_VALIDATION_PREFIX + "UNKNOWN_EXCEPTION_DURING_TRANSFORMATION";
	public static final String ERROR_UNKNOWN_EXCEPTION_DURING_VALIDATION     = LOADER_VALIDATION_PREFIX + "UNKNOWN_EXCEPTION_DURING_VALIDATION";
	public static final String WARNING_UNRESOLVED_LIBRARY_NAMESPACE          = LOADER_VALIDATION_PREFIX + "UNRESOLVED_LIBRARY_NAMESPACE";
	public static final String WARNING_CORRUPT_LIBRARY_CONTENT               = LOADER_VALIDATION_PREFIX + "CORRUPT_LIBRARY_CONTENT";
	public static final String WARNING_LIBRARY_NOT_FOUND                     = LOADER_VALIDATION_PREFIX + "WARNING_LIBRARY_NOT_FOUND";
	public static final String WARNING_SCHEMA_NOT_FOUND                      = LOADER_VALIDATION_PREFIX + "WARNING_SCHEMA_NOT_FOUND";
	public static final String WARNING_PROJECT_NOT_FOUND                     = LOADER_VALIDATION_PREFIX + "WARNING_PROJECT_NOT_FOUND";
	public static final String WARNING_DUPLICATE_LIBRARY                     = LOADER_VALIDATION_PREFIX + "DUPLICATE_LIBRARY";
	public static final String WARNING_DUPLICATE_SCHEMA                      = LOADER_VALIDATION_PREFIX + "DUPLICATE_SCHEMA";
	public static final String WARNING_MANAGED_LIBRARY_NAMESPACE_MISMATCH    = LOADER_VALIDATION_PREFIX + "MANAGED_LIBRARY_NAMESPACE_MISMATCH";
	public static final String WARNING_INVALID_REPOSITORY_URL                = LOADER_VALIDATION_PREFIX + "INVALID_REPOSITORY_URL";
	
}