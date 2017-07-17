package com.individual.thinking.traitorstown;


import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.Charset;

public class TestUtils {
    public static String readFileFromResource(String filename) throws IOException {
        return FileUtils.readFileToString(new ClassPathResource(filename).getFile(), Charset.defaultCharset());
    }
}
