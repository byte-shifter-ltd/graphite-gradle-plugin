/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Sion Williams
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.byteshifter.gradle.graphite

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject
import spock.util.mop.ConfineMetaClassChanges

/**
 * @author Sion Williams
 */
class GraphiteLoggerSpec extends Specification {

    @Subject
    GraphiteLogger gL

    Socket mockSocket = Mock()

    void setup() {
        gL = new GraphiteLogger(mockSocket)
        gL.with {
            graphiteHost = '192.168.99.100'
            graphitePort = 1010
        }
    }

    void cleanup() {
        mockSocket.close()
    }

    @ConfineMetaClassChanges([System])
    def "Socket message is correctly formatted"() {
        given:
        def identifier = "myIdentifier"
        def metrics = [buildTime: 900L]
        //Mock currentTimeMillis using Groovy metaprogramming
        System.metaClass.static.currentTimeMillis = {return 1000L}

        when:
        def result = gL.formatMetrics(identifier, metrics)

        then:
        // result == "myIdentifier.buildTime 900 1000/1000\n"
        result == "myIdentifier.buildTime 900 1\n"
    }

    @ConfineMetaClassChanges([System])
    def "Complex message is correctly formatted"() {
        given:
        def identifier = "myIdentifier"
        def metrics = [buildTime: 900L,
                        anotherMetric: 192L,
                        andAnother: 3563L]
        //Mock currentTimeMillis using Groovy metaprogramming
        System.metaClass.static.currentTimeMillis = {return 1000L}

        when:
        def result = gL.formatMetrics(identifier, metrics)

        then:
        // result == "myIdentifier.buildTime 900 1000/1000\n"
        result == "myIdentifier.buildTime 900 1\nmyIdentifier.anotherMetric 192 1\nmyIdentifier.andAnother 3563 1\n"
    }

    @Ignore("Currently not able to mock the socket")
    def "GraphiteLogger correctly logs to Graphite"(){
        given:
        def payload = "mock payload"

        when:
        gL.log(payload)

        then:
        payload == mockSocket.outputStream.toString()
    }
}