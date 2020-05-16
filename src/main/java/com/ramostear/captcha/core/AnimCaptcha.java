package com.ramostear.captcha.core;

import com.ramostear.captcha.AbstractCaptcha;
import com.ramostear.captcha.support.CaptchaType;
import com.ramostear.captcha.thirdparty.AnimationEncoder;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

/**
 * @author : ramostear/树下魅狐
 * @version : 1.0
 * <p>基于GIF的动画验证码</p>
 */
public class AnimCaptcha extends AbstractCaptcha {

    public void render(OutputStream out) {
        char[] codes = text();
        Color[] colors = new Color[length];
        for(int i=0;i<length;i++){
            colors[i] = color();
        }
        // 随机生成贝塞尔曲线参数
        int x1 = 5, y1 = nextInt(5, height / 2);
        int x2 = width - 5, y2 = nextInt(height / 2, height - 5);
        int ctrlX = nextInt(width / 4, width / 4 * 3), ctrlY = nextInt(5, height - 5);
        if (nextInt(2) == 0) {
            int ty = y1;
            y1 = y2;
            y2 = ty;
        }
        int ctrlX1 = nextInt(width / 4, width / 4 * 3), ctrlY1 = nextInt(5, height - 5);
        int[][] bezier = new int[][]{{x1, y1}, {ctrlX, ctrlY}, {ctrlX1, ctrlY1}, {x2, y2}};
        AnimationEncoder animation = new AnimationEncoder();
        animation.setQuality(180);
        animation.setDelay(100);
        animation.setRepeat(0);
        animation.start(out);
        for(int i=0;i<length;i++){
            BufferedImage img = drawImage(colors,codes,i,bezier);
            animation.addFrame(img);
            img.flush();
        }
        animation.finish();

    }

    public String toBase64() {
        return toBase64("data:image/png;base64,");
    }

    public BufferedImage drawImage(Color[] colors,char[] chars,int index,int[][] bezier) {
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,width,height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f * nextInt(10)));
        drawOval(g);
        drawLine(g);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f * nextInt(10)));
        g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        g.setColor(colors[0]);
        CubicCurve2D shape = new CubicCurve2D.Double(bezier[0][0], bezier[0][1], bezier[1][0], bezier[1][1], bezier[2][0], bezier[2][1], bezier[3][0], bezier[3][1]);
        g.draw(shape);

        g.setFont(getFont());
        FontMetrics fontMetrics = g.getFontMetrics();
        int fw = (width/chars.length) - 2;
        int fm = (fw - (int) fontMetrics.getStringBounds("W", g).getWidth()) / 2;
        for(int i=0;i< chars.length;i++){
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,getAlpha(index,i));
            g.setComposite(alpha);
            g.setColor(colors[i]);
            int fY = height - ((height - (int) fontMetrics.getStringBounds(String.valueOf(chars[i]), g).getHeight()) >> 1);  // 文字的纵坐标
            g.drawString(String.valueOf(chars[i]), i * fw + fm + 3, fY - 3);
        }
        g.dispose();
        return image;
    }

    private float getAlpha(int i, int j) {
        int num = i + j;
        float r = (float) 1 / (length - 1);
        float s = length * r;
        return num >= length ? (num * r - s) : num * r;
    }

    public AnimCaptcha(){}

    public AnimCaptcha(int width,int height){
        this();
        setWidth(width);
        setHeight(height);
    }

    public AnimCaptcha(int width,int height,int length){
        this(width, height);
        setLength(length);
    }

    public AnimCaptcha(int width, int height, CaptchaType type){
        this(width, height);
        setType(type);
    }

    public AnimCaptcha(int width,int height,CaptchaType type,int length){
        this(width, height, type);
        setLength(length);
    }

    public AnimCaptcha(int width,int height,CaptchaType type,Font font){
        this(width, height, type);
        setFont(font);
    }

    public AnimCaptcha(int width, int height, CaptchaType type, Font font,int length){
        this(width, height, type, font);
        setLength(length);
    }
}
