package com.ramostear.captcha;

import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.core.AnimCaptcha;
import com.ramostear.captcha.core.Captcha;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

/**
 * @author : ramostear/树下魅狐
 * @version : 1.0
 * @contact : ramostear@163.com/www.ramostear.com
 * @datetime : 2020/5/15/下午4:33
 * @dscription : Happy Captcha
 */
public class HappyCaptcha {

    /**
     * Happy Captcha code Session attribute key:happy-captcha
     */
    public static final String SESSION_KEY = "happy-captcha";

    private CaptchaType type;
    private CaptchaStyle style;
    private Font font;
    private int width;
    private int height;
    private int length;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public static Builder require(HttpServletRequest request,HttpServletResponse response){
        return new Builder(request,response);
    }

    public boolean finish() {
        try {
            boolean flag = false;
            if(this.style.equals(CaptchaStyle.IMG)){
                Captcha captcha = new Captcha();
                captcha.setType(this.type);
                captcha.setWidth(this.width);
                captcha.setHeight(this.height);
                captcha.setLength(this.length);
                captcha.setFont(this.font);
                setHeader(this.response);
                this.request.getSession().setAttribute(SESSION_KEY,captcha.getCaptchaCode());
                captcha.render(this.response.getOutputStream());
                return true;
            }else if(this.style.equals(CaptchaStyle.ANIM)){
                AnimCaptcha captcha = new AnimCaptcha();
                captcha.setType(this.type);
                captcha.setWidth(this.width);
                captcha.setHeight(this.height);
                captcha.setLength(this.length);
                captcha.setFont(this.font);
                setHeader(this.response);
                this.request.getSession().setAttribute(SESSION_KEY,captcha.getCaptchaCode());
                captcha.render(this.response.getOutputStream());
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public void setHeader(HttpServletResponse response) {
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
    }


    private HappyCaptcha(Builder builder){
        this.type = builder.type;
        this.style = builder.style;
        this.font = builder.font;
        this.width = builder.width;
        this.height = builder.height;
        this.length = builder.length;
        this.request = builder.request;
        this.response = builder.response;
    }

    public static class Builder{
        private CaptchaType type = CaptchaType.DEFAULT;
        private CaptchaStyle style = CaptchaStyle.IMG;
        private Font font = Fonts.getInstance().defaultFont();
        private int width = 160;
        private int height = 50;
        private int length = 5;
        private final HttpServletRequest request;
        private final HttpServletResponse response;

        public Builder(HttpServletRequest request,HttpServletResponse response){
            this.request = request;
            this.response = response;
        }

        public HappyCaptcha build(){
            return new HappyCaptcha(this);
        }

        public Builder type(CaptchaType type){
            this.type = type;
            return this;
        }

        public Builder style(CaptchaStyle style){
            this.style = style;
            return this;
        }

        public Builder width(int width){
            this.width = width;
            return this;
        }

        public Builder height(int height){
            this.height = height;
            return this;
        }

        public Builder length(int length){
            this.length = length;
            return this;
        }
        public Builder font(Font font){
            this.font = font;
            return this;
        }
    }

}
