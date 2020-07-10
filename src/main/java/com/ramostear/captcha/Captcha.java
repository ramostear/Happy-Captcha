package com.ramostear.captcha;

import java.io.IOException;

public interface Captcha {

    /**
     * 转换图片以渲染目标
     *
     * @throws IOException
     */
    void render() throws IOException;

    /**
     * 将图片转换为Base64格式字符串
     *
     * @return Base64格式字符串
     */
    String renderToBase64() throws IOException;

    /**
     * 获取验证码
     *
     * @return 验证码字符串
     */
    String getCaptchaCode();

}
