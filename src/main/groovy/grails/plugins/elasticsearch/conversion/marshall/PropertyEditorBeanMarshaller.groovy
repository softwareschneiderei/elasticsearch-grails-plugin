package grails.plugins.elasticsearch.conversion.marshall

class PropertyEditorBeanMarshaller extends DefaultMarshaller {

    def propertyEditor

    @Override
    protected doMarshall(Object object) {
        assert propertyEditor != null
        propertyEditor.setValue(object)
        propertyEditor.getAsText()
    }
}
