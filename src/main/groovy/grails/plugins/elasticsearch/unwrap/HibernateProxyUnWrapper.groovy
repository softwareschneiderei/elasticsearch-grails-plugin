package grails.plugins.elasticsearch.unwrap

import org.grails.orm.hibernate.cfg.GrailsHibernateUtil

/**
 * @author Noam Y. Tenne.
 */
class HibernateProxyUnWrapper implements DomainClassUnWrapper {

    @Override
    def unWrap(Object object) {
        return GrailsHibernateUtil.unwrapIfProxy(object)
    }
}
