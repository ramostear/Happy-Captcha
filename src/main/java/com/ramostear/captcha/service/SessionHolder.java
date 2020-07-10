package com.ramostear.captcha.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SessionHolder {

    void set(String key, String value);

    String get(String key);

    void remove(String key);

    RenderTarget createRenderTarget();

    static SessionHolder of(HttpServletRequest request, HttpServletResponse response) {
        return new HttpServletSessionHolder(request, response);
    }

}
