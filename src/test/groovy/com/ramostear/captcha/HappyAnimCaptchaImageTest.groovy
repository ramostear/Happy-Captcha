package com.ramostear.captcha

import com.ramostear.captcha.service.HttpServletSessionHolder
import com.ramostear.captcha.support.CaptchaStyle
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
class HappyAnimCaptchaImageTest extends CaptchaTestBase {

    @Test
    void 'check animated captcha is generated with given text'(
            @Mock HttpServletRequest request,
            @Mock HttpServletResponse response,
            @Mock HttpSession httpSession) throws IOException {

        //given:
        def text = "Hello"
        def (HttpServletSessionHolder holder, String inputText, TestServletOutputStream testOutputStream) = prepare(request, response, httpSession, text)

        //when:
        HappyCaptcha.create(holder)
                .style(CaptchaStyle.ANIM)
                .text(inputText)
                .build()
                .generate()
                .render()

        //and:
        def (File tempFile, String captchaText) = saveImage(testOutputStream, holder, ".gif")

        //then:
        assertThat(tempFile)
                .isFile()
                .exists()

        assertThat(captchaText)
                .isNotEmpty()
                .isEqualTo(inputText)
    }

    @Test
    void 'verify generated animated captcha is generated with given text'(
            @Mock HttpServletRequest request,
            @Mock HttpServletResponse response,
            @Mock HttpSession httpSession) throws IOException {

        //given:
        def text = "World"
        def (HttpServletSessionHolder holder, String inputText) = prepare(request, response, httpSession, text)

        //when:
        HappyCaptcha.create(holder)
                .style(CaptchaStyle.ANIM)
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
    void 'remove generated animated captcha from session'(
            @Mock HttpServletRequest request,
            @Mock HttpServletResponse response,
            @Mock HttpSession httpSession) throws IOException {

        //given:
        def text = "Test"
        def (HttpServletSessionHolder holder, String inputText, TestServletOutputStream testOutputStream, Map<String, Object> map) = prepare(request, response, httpSession, text)

        //when:
        HappyCaptcha.create(holder)
                .style(CaptchaStyle.ANIM)
                .text(inputText)
                .build()
                .generate()
                .render()

        HappyCaptcha.remove(holder)

        //and:
        assertThat(map)
            .doesNotContainKey(SESSION_KEY)
    }

}
