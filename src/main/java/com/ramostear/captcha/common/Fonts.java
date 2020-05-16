package com.ramostear.captcha.common;

import java.awt.*;

/**
 * @author : ramostear/树下魅狐
 * @version : 1.0
 * <p>内置的字体库</p>
 */
public class Fonts {

    private Font defaultFont;
    private Font zhFont;
    private Font enFont1;
    private Font enFont2;
    private static Fonts instance = null;

    public static Fonts getInstance(){
        if(instance == null){
            instance = new Fonts();
        }
        return instance;
    }

    public Font defaultFont(){
        return this.defaultFont;
    }
    public Font zhFont(){
        return this.zhFont;
    }
    public Font enFont1(){
        return this.enFont1;
    }
    public Font enFont2(){
        return this.enFont2;
    }

    private static final String[] FONT_FAMILY = {
            "microsoft_yahei.ttf",
            "Hanybq_simple.ttf",
            "Comfortaa-Regular.ttf",
            "PressStart2P-Regular.ttf"
    };

    private Fonts(){
        try {
            this.defaultFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/"+FONT_FAMILY[0])).deriveFont(Font.ITALIC,32F);
            this.zhFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/"+FONT_FAMILY[1])).deriveFont(Font.ITALIC,32F);
            this.enFont1 = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/"+FONT_FAMILY[2])).deriveFont(Font.ITALIC,32F);
            this.enFont2 = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/"+FONT_FAMILY[3])).deriveFont(Font.ITALIC,32F);
        } catch (Exception e) {
            e.printStackTrace();
            this.defaultFont = _default();
            this.zhFont = _default();
            this.enFont1 = _default();
            this.enFont2 = _default();
        }

    }

    private static Font _default(){
        return new Font("Arial",Font.ITALIC,32);
    }
}
