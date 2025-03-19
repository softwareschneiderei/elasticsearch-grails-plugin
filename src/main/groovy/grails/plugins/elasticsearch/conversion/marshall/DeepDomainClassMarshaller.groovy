package grails.plugins.elasticsearch.conversion.marshall

import grails.plugins.elasticsearch.mapping.DomainEntity
import grails.plugins.elasticsearch.mapping.DomainProperty

class DeepDomainClassMarshaller extends DefaultMarshaller {
    protected doMarshall(instance) {
        def domainClass = getDomainClass(instance)
        // don't use instance class directly, instead unwrap from javaassist
        def marshallResult = [id: instance.id, 'class': domainClass.type.name]
        def scm = elasticSearchContextHolder.getMappingContext(domainClass)
        if (!scm) {
            throw new IllegalStateException("Domain class ${domainClass} is not searchable.")
        }
        for (DomainProperty prop in domainClass.getProperties()) {
            def propertyMapping = scm.getPropertyMapping(prop.name)
            if (!propertyMapping) {
                continue
            }
            def propertyClassName = instance."${prop.name}"?.class?.name
            def propertyClass = instance."${prop.name}"?.class
            def propertyValue = instance."${prop.name}"

            // Domain marshalling
            if (isDomainClass(propertyClass)) {
                String searchablePropertyName = getSearchablePropertyName()
                if (propertyValue.class."$searchablePropertyName") {
                    // todo fixme - will throw exception when no searchable field.
                    marshallingContext.lastParentPropertyName = prop.name
                    // Converters build a string value out of a class so we can just add it
                    if (propertyMapping?.isGeoPoint() || propertyMapping.converter != null) {
                        marshallResult += [(prop.name): (marshallingContext.delegateMarshalling(propertyValue, propertyMapping.maxDepth))]
                    } else {
                        marshallResult += [(prop.name): ([id: propertyValue.ident(), 'class': propertyClassName] + marshallingContext.delegateMarshalling(propertyValue, propertyMapping.maxDepth))]
                    }
                } else {
                    marshallResult += [(prop.name): [id: propertyValue.ident(), 'class': propertyClassName]]
                }

                // Non-domain marshalling
            } else {
                marshallingContext.lastParentPropertyName = prop.name
                def marshalledValue = marshallingContext.delegateMarshalling(propertyValue)
                // Ugly XContentBuilder bug: it only checks for EXACT class match with java.util.Date
                // (sometimes it appears to be java.sql.Timestamp for persistent objects)
                if (marshalledValue instanceof Date) {
                    marshalledValue = new Date(marshalledValue.getTime())
                }
                marshallResult += [(prop.name): marshalledValue]
            }
        }
        marshallResult
    }

    protected nullValue() {
        return []
    }

    // TODO: Fix this smell
    private DomainEntity getDomainClass(Object instance) {
        marshallingContext.parentFactory.getUnwrappedInstanceDomainClass(instance)
    }

    // TODO: Fix this smell
    boolean isDomainClass(Class<?> clazz) {
        marshallingContext.parentFactory.isDomainClass(clazz)
    }

    private String getSearchablePropertyName() {
        def searchablePropertyName = elasticSearchContextHolder.config.searchableProperty.name

        //Maintain backwards compatibility. Searchable property name may not be defined
        if (!searchablePropertyName) {
            return 'searchable'
        }
        searchablePropertyName
    }
}
