package com.ramostear.captcha

import com.ramostear.captcha.service.HttpServletSessionHolder
import groovy.transform.CompileStatic

import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

import static com.ramostear.captcha.HappyCaptcha.SESSION_KEY
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.doAnswer
import static org.mockito.Mockito.when

@CompileStatic
abstract class CaptchaTestBase {

    protected static final boolean DEBUG = Boolean.valueOf(System.getenv("DEBUG") ?: 'false')

    protected List saveImage(TestServletOutputStream testOutputStream, HttpServletSessionHolder holder, String extension) {
        File tempFile = File.createTempFile("test_", extension)
        if(DEBUG) {
            println "File: $tempFile.absolutePath"
            try (FileOutputStream stream = new FileOutputStream(tempFile)) {
                ((TestServletOutputStream) testOutputStream).baos.writeTo(stream)
            }
        } else {
            tempFile.deleteOnExit()
        }

        String captchaText = holder.get(SESSION_KEY)
        [tempFile, captchaText]
    }

    protected List prepare(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, String inputText) {
        HttpServletSessionHolder holder = new HttpServletSessionHolder(request, response)
        ServletOutputStream testOutputStream = new TestServletOutputStream()
        Map<String, Object> map = new HashMap<>()

        when(request.getSession())
                .thenReturn(httpSession)
        when(response.getOutputStream())
                .thenReturn(testOutputStream)

        doAnswer({ invocationOnMock ->
            String key = invocationOnMock.getArgument(0)
            String value = invocationOnMock.getArgument(1)
            map.put(key, value)

            return null
        })
                .when(httpSession)
                .setAttribute(anyString(), any())

        doAnswer({ invocationOnMock ->
            String key = invocationOnMock.getArgument(0)
            return map.get(key)
        })
                .when(httpSession)
                .getAttribute(anyString())

        doAnswer({ invocationOnMock ->
            String key = invocationOnMock.getArgument(0)
            map.remove(key)

            return null
        })
                .when(httpSession)
                .removeAttribute(anyString())



        [holder, inputText, testOutputStream, map]
    }

    class TestServletOutputStream extends ServletOutputStream {

        private final ByteArrayOutputStream baos = new ByteArrayOutputStream()

        @Override
        boolean isReady() {
            return false
        }

        @Override
        void setWriteListener(WriteListener writeListener) {

        }

        @Override
        void write(int b) throws IOException {
            baos.write(b)
        }

    }

}
