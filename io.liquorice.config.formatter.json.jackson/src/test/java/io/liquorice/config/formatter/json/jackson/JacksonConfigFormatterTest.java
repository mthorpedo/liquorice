package io.liquorice.config.formatter.json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by mthorpe on 12/11/16.
 */
public class JacksonConfigFormatterTest {

    private static final Map<String, String> TEST_MAP = ImmutableMap.of("test", "json");
    private static final String TEST_JSON = "{\"test\":\"json\"}";
    private static final String TEST_NOT_JSON = "{ hello bob }";

    private JacksonConfigFormatter configFormatter;

    @BeforeTest
    public void setup() {
        this.configFormatter = new JacksonConfigFormatter.Builder().build();
    }

    @Test
    public void testModuleRegistration() {
        final SimpleModule simpleModule = new SimpleModule();
        final ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        final JacksonConfigFormatter jacksonConfigFormatter = new JacksonConfigFormatter.Builder() //
                .withObjectMapper(mockObjectMapper) //
                .withRegisteredModule(simpleModule) //
                .build();
        assertNotNull(jacksonConfigFormatter);

        verify(mockObjectMapper).registerModule(simpleModule);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReadObjectIsInputStream() throws Exception {
        final Object downcastedInputStream = new ByteArrayInputStream(TEST_JSON.getBytes(Charsets.UTF_8));
        final Map<String, String> read = (Map<String, String>) configFormatter.read(downcastedInputStream, Map.class)
                .get();
        assertEquals(read, TEST_MAP);
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void testReadObjectIsMalformedInputStreamContent() throws Exception {
        final Object downcastedInputStream = new ByteArrayInputStream(TEST_NOT_JSON.getBytes(Charsets.UTF_8));
        configFormatter.read(downcastedInputStream, Map.class).get();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReadObjectIsReader() throws Exception {
        final Object downcastedInputStream = new InputStreamReader(new ByteArrayInputStream(
                TEST_JSON.getBytes(Charsets.UTF_8)), Charsets.UTF_8);
        final Map<String, String> read = (Map<String, String>) configFormatter.read(downcastedInputStream, Map.class)
                .get();
        assertEquals(read, TEST_MAP);
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void testReadObjectIsMalformedReaderContent() throws Exception {
        final Object downcastedInputStream = new InputStreamReader(new ByteArrayInputStream(
                TEST_NOT_JSON.getBytes(Charsets.UTF_8)), Charsets.UTF_8);
        configFormatter.read(downcastedInputStream, Map.class).get();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReadObjectIsString() throws Exception {
        final Object downcastedString = TEST_JSON;
        final Map<String, String> read = (Map<String, String>) configFormatter.read(downcastedString, Map.class).get();
        assertEquals(read, TEST_MAP);
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void testReadObjectIsMalformedStringContent() throws Exception {
        final Object downcastedString = TEST_NOT_JSON;
        configFormatter.read(downcastedString, Map.class).get();
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = NoSuchElementException.class)
    public void testUnsupportedInputType() throws Exception {
        final Object downcastedDouble = 2.0;
        configFormatter.read(downcastedDouble, Map.class).get();
    }

    @Test
    public void testWrite() throws Exception {
        final Object written = configFormatter.write(TEST_MAP);
        assertTrue(written instanceof String);

        final String writtenJson = (String) written;
        assertEquals(writtenJson, TEST_JSON);
    }
}