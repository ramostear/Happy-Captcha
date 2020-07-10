package com.ramostear.captcha

import com.ramostear.captcha.service.HttpServletSessionHolder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

import static com.ramostear.captcha.HappyCaptcha.SESSION_KEY
import static org.assertj.core.api.Assertions.assertThat

@RunWith(JUnitPlatform.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)//Added due strange mockito bug
class HappySimpleCaptchaImageTest extends CaptchaTestBase {

    @Test
    void 'check captcha is generated with given text'(
            @Mock HttpServletRequest request,
            @Mock HttpServletResponse response,
            @Mock HttpSession httpSession) throws IOException {

        //given:
        def text = "Hello"
        def (HttpServletSessionHolder holder, String inputText, TestServletOutputStream testOutputStream) = prepare(request, response, httpSession, text)

        //when:
        HappyCaptcha.create(holder)
                .text(inputText)
                .build()
                .generate()
                .render()

        //and:
        def (File tempFile, String captchaText) = saveImage(testOutputStream, holder, ".png")

        //then:
        assertThat(tempFile)
                .isFile()
                .exists()

        assertThat(captchaText)
                .isNotEmpty()
                .isEqualTo(inputText)
    }

    @Test
    void 'verify generated captcha is generated with given text'(
            @Mock HttpServletRequest request,
            @Mock HttpServletResponse response,
            @Mock HttpSession httpSession) throws IOException {

        //given:
        def text = "World"
        def (HttpServletSessionHolder holder, String inputText) = prepare(request, response, httpSession, text)

        //when:
        HappyCaptcha.create(holder)
                .text(inputText)
                .build()
                .generate()
                .render()

        def result = HappyCaptcha.verification(holder, text, true)

        //then:
        assertThat(result)
                .isTrue()
    }

    @Test
    void 'remove generated captcha from session'(
            @Mock HttpServletRequest request,
            @Mock HttpServletResponse response,
            @Mock HttpSession httpSession) throws IOException {

        //given:
        def text = "Test"
        def (HttpServletSessionHolder holder, String inputText, TestServletOutputStream testOutputStream, Map<String, Object> map) = prepare(request, response, httpSession, text)

        //when:
        HappyCaptcha.create(holder)
                .text(inputText)
                .build()
                .generate()
                .render()

        HappyCaptcha.remove(holder)

        //and:
        assertThat(map)
            .doesNotContainKey(SESSION_KEY)
    }


    @Test
    void 'check generated captcha as base64 string'(
            @Mock HttpServletRequest request,
            @Mock HttpServletResponse response,
            @Mock HttpSession httpSession) throws IOException {

        //given:
        def text = ""//will generate random text
        def (HttpServletSessionHolder holder, String inputText) = prepare(request, response, httpSession, text)

        //when:
        String result = HappyCaptcha.create(holder)
                .text(inputText)
                .build()
                .generate()
                .renderToBase64()

        //then:
        assertThat(result.replace("data:image/png;base64,", ""))
                .isNotEmpty()
                .containsPattern(/^([A-Za-z0-9+\/]{4})*([A-Za-z0-9+\/]{3}=|[A-Za-z0-9+\/]{2}==)?$/)
    }

}
