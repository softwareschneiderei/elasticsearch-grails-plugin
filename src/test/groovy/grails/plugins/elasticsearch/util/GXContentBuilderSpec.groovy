package grails.plugins.elasticsearch.util

import spock.lang.Specification

class GXContentBuilderSpec extends Specification {

    void "test build aggregationQuery"() {
        given:
        GXContentBuilder builder = new GXContentBuilder()
        Closure query = {
            "types" {
                filters {
                    "filters" {
                        "jpg" { match(type: 'jpg') }
                        "png" { match(type: 'png') }
                    }
                }
            }

            "names" {
                terms(field: 'name')
            }

            "avg_size" {
                avg(field: 'size')
            }
        }

        expect:
        builder.build(query).toString() == "[types:[filters:[filters:[jpg:[match:[type:jpg]], png:[match:[type:png]]]]], names:[terms:[field:name]], avg_size:[avg:[field:size]]]"
    }
}
