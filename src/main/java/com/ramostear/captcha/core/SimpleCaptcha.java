package com.ramostear.captcha.core;

import com.ramostear.captcha.AbstractCaptcha;
import com.ramostear.captcha.service.RenderTarget;
import com.ramostear.captcha.service.SessionHolder;
import com.ramostear.captcha.support.CaptchaType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author : ramostear/树下魅狐
 * @version : 1.0
 * <p>通用的验证码</p>
 */
public class SimpleCaptcha extends AbstractCaptcha {

    @Override
    public void render(RenderTarget renderTarget) throws IOException {
        drawImage(text(), renderTarget);
    }

    @Override
    public String renderToBase64() throws IOException {
        return toBase64("data:image/png;base64,");
    }

    private void drawImage(char[] chars, RenderTarget target) throws IOException {
        BufferedImage img = createImage();
        Graphics2D g = (Graphics2D) img.getGraphics();
        drawLine(g);
        drawOval(g);
        drawBezierLine(g);
        g.setFont(getFont());
        FontMetrics fontMetrics = g.getFontMetrics();
        int fw = (width / chars.length) - 2;
        int fm = (fw - (int) fontMetrics.getStringBounds("8", g).getWidth()) / 2;
        for (int i = 0; i < chars.length; i++) {
            g.setColor(color());
            int fh = height - ((height - (int) fontMetrics.getStringBounds(String.valueOf(chars[i]), g).getHeight()) >> 1);
            g.drawString(String.valueOf(chars[i]), i * fw + fm, fh);
        }
        g.dispose();
        target.write(img);
    }

    public SimpleCaptcha(SessionHolder sessionHolder) {
        super(sessionHolder);
    }

    public SimpleCaptcha(SessionHolder sessionHolder, int width, int height) {
        this(sessionHolder);
        setWidth(width);
        setHeight(height);
    }

    public SimpleCaptcha(SessionHolder sessionHolder, int width, int height, int length) {
        this(sessionHolder, width, height);
        setLength(length);
    }

    public SimpleCaptcha(SessionHolder sessionHolder, int width, int height, CaptchaType type) {
        this(sessionHolder, width, height);
        setType(type);
    }

    public SimpleCaptcha(SessionHolder sessionHolder, int width, int height, CaptchaType type, int length) {
        this(sessionHolder, width, height, type);
        setLength(length);
    }

    public SimpleCaptcha(SessionHolder sessionHolder, int width, int height, CaptchaType type, Font font) {
        this(sessionHolder, width, height, type);
        setFont(font);
    }

    public SimpleCaptcha(SessionHolder sessionHolder, int width, int height, CaptchaType type, Font font, int length) {
        this(sessionHolder, width, height, type, font);
        setLength(length);
    }

}
