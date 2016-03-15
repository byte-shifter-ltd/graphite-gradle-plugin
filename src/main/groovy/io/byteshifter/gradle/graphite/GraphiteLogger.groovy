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

import groovy.util.logging.Slf4j

/**
 * GraphiteLogger
 * allows posting of key/value pairs to a Graphite instance
 *
 * @author Sion Williams
 */
@Slf4j
class GraphiteLogger {
    String graphiteHost
    int graphitePort

    void logToGraphite(String key, long value) {
        Map stats = new HashMap()
        stats.put(key, value)
        logToGraphiteWithMap(stats)
    }

    void logToGraphiteWithMap(Map stats) {
        if (stats.isEmpty()) {
            return
        }

        try {
            String nodeIdentifier = InetAddress.getLocalHost().getHostName()
            logToGraphiteWithIdentifier(nodeIdentifier, stats)
        } catch (Throwable t) {
            log.error("Can't log to graphite", t)
        }
    }

    void logToGraphiteWithIdentifier(String nodeIdentifier, Map stats) throws Exception {
        BigDecimal curTimeInSec = getTimeInSeconds()
        StringBuffer lines = new StringBuffer()
        for (Map.Entry entry : stats.entrySet()) {
            String key = nodeIdentifier + "." + entry.getKey()
            lines.append(key).append(" ").append(entry.getValue()).append(" ").append(curTimeInSec).append("\n")
            //even the last line in graphite
        }
        logToGraphiteWithLines(lines)
    }

    private void logToGraphiteWithLines(StringBuffer lines) throws Exception {
        String msg = lines.toString()
        log.info("Writing [${msg}] to graphite")
        Socket socket = new Socket(graphiteHost, graphitePort)
        try {
            Writer writer = new OutputStreamWriter(socket.getOutputStream())
            writer.write(msg)
            writer.flush()
            writer.close()
        } finally {
            socket.close()
        }
    }

    /**
     * formatMetrics
     * @param identifier is a unique string to help name the source of metrics in Graphite
     * @param metrics is a key value pair of metrics to be sent
     * @return String is a correctly formatted output which can be sent to Graphite
     */
    static String formatMetrics(String identifier, LinkedHashMap<String, Long> metrics) {
        def curTimeInSec = getTimeInSeconds()
        String output = ''

        metrics.each { key, value ->
            output += "${identifier}.${key} ${value} ${curTimeInSec}\n"
        }
        return output
    }

    /**
     * getTimeInSeconds
     * @return time in Seconds
     */
    private static BigDecimal getTimeInSeconds() {
        System.currentTimeMillis() / 1000
    }
}
