package com.ramostear.captcha;

import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.core.AnimCaptcha;
import com.ramostear.captcha.core.SimpleCaptcha;
import com.ramostear.captcha.service.HttpServletSessionHolder;
import com.ramostear.captcha.service.SessionHolder;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;

/**
 * @author : ramostear/树下魅狐
 * @version : 1.0
 * <p>Happy Captcha Application Class</p>
 */
public final class HappyCaptcha {

    /**
     * Happy Captcha code Session attribute key:happy-captcha
     */
    public static final String SESSION_KEY = "happy-captcha";

    private final CaptchaType type;
    private final CaptchaStyle style;
    private final Font font;
    private final int width;
    private final int height;
    private final int length;
    private final SessionHolder sessionHolder;
    private final String text;

    private HappyCaptcha(Builder builder) {
        this.type = builder.type;
        this.style = builder.style;
        this.font = builder.font;
        this.width = builder.width;
        this.height = builder.height;
        this.length = builder.length;
        this.sessionHolder = builder.sessionHolder;
        this.text = builder.text;
    }

    public static Builder require(HttpServletRequest request,
                                 HttpServletResponse response) {
        return create(new HttpServletSessionHolder(request, response));
    }

    public static Builder create(SessionHolder sessionHolder) {
        return new Builder(sessionHolder);
    }

    public Captcha generate() {
        if (CaptchaStyle.IMG.equals(this.style)) {
            return onImage();
        } else if (CaptchaStyle.ANIM.equals(this.style)) {
            return onAnimatedImage();
        }

        throw new IllegalArgumentException(String.format("Unknown style[%s]", this.style));
    }

    private SimpleCaptcha onImage() {
        SimpleCaptcha captcha = new SimpleCaptcha(sessionHolder);
        if(this.text != null && !"".equals(this.text.trim())){
            captcha.setCaptchaCode(this.text);
        }
        captcha.setType(this.type);
        captcha.setWidth(this.width);
        captcha.setHeight(this.height);
        captcha.setLength(this.length);
        captcha.setFont(this.font);

        return captcha;
    }

    private AnimCaptcha onAnimatedImage() {
        AnimCaptcha captcha = new AnimCaptcha(sessionHolder);
        if(this.text != null && !"".equals(this.text.trim())){
            captcha.setCaptchaCode(this.text);
        }
        captcha.setType(this.type);
        captcha.setWidth(this.width);
        captcha.setHeight(this.height);
        captcha.setLength(this.length);
        captcha.setFont(this.font);

        return captcha;
    }

    /**
     * 校验验证码
     *
     * @param sessionHolder    SessionHolder
     * @param code       captcha code
     * @param ignoreCase Ignore Case
     * @return boolean
     */
    public static boolean verification(SessionHolder sessionHolder, String code, boolean ignoreCase) {
        if (code == null || code.trim().equals("")) {
            return false;
        }
        String captcha = sessionHolder.get(SESSION_KEY);
        return ignoreCase ? code.equalsIgnoreCase(captcha) : code.equals(captcha);
    }

    public static boolean verification(HttpServletRequest request, String code, boolean ignoreCase) {
        return verification(new HttpServletSessionHolder(request, null), code, ignoreCase);
    }

    /**
     * remove captcha from session
     *
     * @param sessionHolder SessionHolder
     */
    public static void remove(SessionHolder sessionHolder) {
        sessionHolder.remove(SESSION_KEY);
    }

    public static void remove(HttpServletRequest request) {
        remove(new HttpServletSessionHolder(request, null));
    }

    public static class Builder {
        private CaptchaType type = CaptchaType.DEFAULT;
        private CaptchaStyle style = CaptchaStyle.IMG;
        private Font font = Fonts.getInstance().defaultFont();
        private int width = 160;
        private int height = 50;
        private int length = 5;
        private SessionHolder sessionHolder;
        private String text;


        public Builder(SessionHolder sessionHolder) {
            this.sessionHolder = sessionHolder;
        }

        public HappyCaptcha build() {
            return new HappyCaptcha(this);
        }

        public Builder type(CaptchaType type) {
            this.type = type;
            return this;
        }

        public Builder style(CaptchaStyle style) {
            this.style = style;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder length(int length) {
            this.length = length;
            return this;
        }

        public Builder font(Font font) {
            this.font = font;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

    }

    public boolean finish() {
        try {
            this.generate().render();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
