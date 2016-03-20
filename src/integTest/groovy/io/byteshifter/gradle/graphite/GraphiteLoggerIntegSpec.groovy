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

import spock.lang.Specification

/**
 * @author Sion Williams
 */
class GraphiteLoggerIntegSpec extends Specification {
    void setup() {

    }

    void cleanup() {

    }

    def "can send data to graphite"() {
        given:
        String host = '192.168.99.100'
        int port = 2003
        String nodeIdentifier ="tomcat.UI.planck_8080"
        Map stats = new HashMap()
        stats.put("memcache_calls", 900L)
        stats.put("num_threads", 50L)
        GraphiteLogger graphiteLogger = new GraphiteLogger()
        graphiteLogger.setGraphiteHost(host)
        graphiteLogger.setGraphitePort(port)

        expect:
        graphiteLogger.logToGraphiteWithIdentifier(nodeIdentifier, stats)
    }

    def "can log data to graphite"() {
        given:
        GraphiteLogger graphiteLogger = new GraphiteLogger()
        graphiteLogger.with {
            graphiteHost = '192.168.99.100'
            graphitePort = 2003
        }
        String identifier = "GL.integtest"
        def metrics = [buildTime: 1000L]
        def payload = graphiteLogger.formatMetrics(identifier, metrics)

        expect:
        graphiteLogger.log(payload)
    }
}
