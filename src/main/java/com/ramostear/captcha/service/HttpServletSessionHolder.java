package com.ramostear.captcha.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

import static com.ramostear.captcha.HappyCaptcha.SESSION_KEY;

public final class HttpServletSessionHolder implements SessionHolder {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public HttpServletSessionHolder(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public void set(String key, String value) {
        HttpSession session = Objects.requireNonNull(request.getSession(), "Can't acquire session");
        session.setAttribute(key, value);
    }

    @Override
    public String get(String key) {
        HttpSession session = Objects.requireNonNull(request.getSession(), "Can't acquire session");
        return String.valueOf(session.getAttribute(SESSION_KEY));
    }

    @Override
    public RenderTarget createRenderTarget() {
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        try {
            return new OutputStreamRenderTarget(response.getOutputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void remove(String key) {
        HttpSession session = Objects.requireNonNull(request.getSession(), "Can't acquire session");
        session.removeAttribute(SESSION_KEY);
    }

}
