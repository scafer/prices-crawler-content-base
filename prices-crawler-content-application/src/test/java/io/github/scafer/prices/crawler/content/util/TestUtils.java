package io.github.scafer.prices.crawler.content.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtils {
    public static String getExpectationBody(String filename) throws IOException {
        var expectationPrefix = "expectations://";
        var expectationLocation = "src/test/resources/expectations/";

        if (filename.startsWith(expectationPrefix)) {
            filename = expectationLocation + filename.substring(expectationPrefix.length());
        }

        return new String(Files.readAllBytes(Paths.get(filename)));
    }
}
