package grails.plugins.elasticsearch.mapping

import grails.plugins.elasticsearch.util.IndexNamingUtils
import grails.testing.gorm.DataTest
import grails.testing.spring.AutowiredTest
import org.grails.datastore.mapping.model.PersistentEntity
import spock.lang.Specification
import test.Photo
import test.all.Post
import test.custom.id.Toy
import test.upperCase.UpperCase

class SearchableClassMappingSpec extends Specification implements DataTest, AutowiredTest {

    Closure doWithSpring() { { ->
            domainReflectionService DomainReflectionService
        } }

    DomainReflectionService domainReflectionService

    void setupSpec() {
        mockDomains(Photo, UpperCase, Post, Toy)
    }

    def "indexing and querying index are calculated based on the index name"() {
        given:
        PersistentEntity persistentEntity = dataStore.mappingContext.getPersistentEntity(className)

        when:
        SearchableClassMapping scm = new SearchableClassMapping(grailsApplication, new DomainEntity(domainReflectionService, persistentEntity), [])

        then:
        scm.indexName == indexName
        scm.queryingIndex == IndexNamingUtils.queryingIndexFor(indexName)
        scm.indexingIndex == IndexNamingUtils.indexingIndexFor(indexName)
        scm.queryingIndex != scm.indexingIndex
        scm.indexName != scm.queryingIndex
        scm.indexName != scm.indexingIndex

        where:
        className       || indexName
        Post.class.name || Post.class.getName().toLowerCase()
        Toy.class.name  || Toy.class.getName().toLowerCase()
    }

    void testGetIndexName() {
        when:
        PersistentEntity persistentEntity = dataStore.mappingContext.getPersistentEntity(Photo.class.name)
        SearchableClassMapping mapping = new SearchableClassMapping(grailsApplication, new DomainEntity(domainReflectionService, persistentEntity), null)

        then:
        Photo.class.getName().toLowerCase() == mapping.getIndexName()
    }

    void testManuallyConfiguredIndexName() {

        when:
        DomainEntity dc = domainReflectionService.getAbstractDomainEntity(Photo.class)
        grailsApplication.config.elasticSearch.index.name = 'index-name'
        SearchableClassMapping mapping = new SearchableClassMapping(grailsApplication, dc, null)

        then:
        'index-name' == mapping.getIndexName()
    }

    void testIndexNameIsLowercaseWhenPackageNameIsLowercase() {
        when:
        PersistentEntity persistentEntity = dataStore.mappingContext.getPersistentEntity(UpperCase.class.name)
        SearchableClassMapping mapping = new SearchableClassMapping(grailsApplication, new DomainEntity(domainReflectionService, persistentEntity), null)
        String indexName = mapping.getIndexName()

        then:
        UpperCase.class.name.toLowerCase() == indexName
    }

    void cleanup() {
        grailsApplication.config.elasticSearch.index.name = null
    }
}
