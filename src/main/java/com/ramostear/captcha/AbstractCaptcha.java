package com.ramostear.captcha;

import com.ramostear.captcha.common.ConstArray;
import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.service.OutputStreamRenderTarget;
import com.ramostear.captcha.service.RenderTarget;
import com.ramostear.captcha.service.SessionHolder;
import com.ramostear.captcha.support.CaptchaType;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import static com.ramostear.captcha.HappyCaptcha.SESSION_KEY;

/**
 * @author : ramostear/树下魅狐
 * @version : 1.0
 * <p> 抽象的验证码类</p>
 */
public abstract class AbstractCaptcha implements Captcha {

    public static final SecureRandom RANDOM = new SecureRandom();
    //验证码字体
    private Font font;
    //验证码字符默认长度
    protected int length = 5;
    //验证码图片默认宽度
    protected int width = 160;
    //验证码图片默认高度
    protected int height = 50;
    //验证码类型
    protected CaptchaType type = CaptchaType.DEFAULT;
    //验证码字符串
    protected String code = null;
    //表达式
    protected char[] expr = new char[length];

    private final SessionHolder sessionHolder;

    protected AbstractCaptcha(SessionHolder sessionHolder) {
        this.sessionHolder = sessionHolder;
    }

    /**
     * 渲染验证码图片并通过输出流输出图片
     *
     * @param renderTarget 渲染目标
     */
    public abstract void render(RenderTarget renderTarget) throws IOException;

    /**
     * 生成验证码字符数组
     *
     * @return 验证码字符数组
     */
    protected char[] captcha() {
        char[] codes = new char[length];
        for (int i = 0; i < codes.length; i++) {
            switch (type) {
                case ARITHMETIC:
                    codes[i] = arithmeticExp(i, false);
                    break;
                case ARITHMETIC_ZH:
                    codes[i] = arithmeticExp(i, true);
                    break;
                case WORD:
                    codes[i] = nextChar(1);
                    break;
                case WORD_LOWER:
                    codes[i] = nextChar(2);
                    break;
                case WORD_UPPER:
                    codes[i] = nextChar(3);
                    break;
                case WORD_NUMBER_LOWER:
                    codes[i] = nextChar(4);
                    break;
                case WORD_NUMBER_UPPER:
                    codes[i] = nextChar(5);
                    break;
                case NUMBER:
                    codes[i] = ConstArray.NUM_ARABIC[nextInt(ConstArray.NUM_ARABIC.length)];
                    break;
                case NUMBER_ZH_CN:
                    codes[i] = ConstArray.NUM_ZH_CN[nextInt(ConstArray.NUM_ZH_CN.length)];
                    break;
                case NUMBER_ZH_HK:
                    codes[i] = ConstArray.NUM_ZH_HK[nextInt(ConstArray.NUM_ZH_HK.length)];
                    break;
                case CHINESE:
                    codes[i] = ConstArray.MODERN_CHINESE[nextInt(ConstArray.MODERN_CHINESE.length)];
                    break;
                default:
                    codes[i] = nextChar();
            }
        }
        if (CaptchaType.ARITHMETIC.equals(type) || CaptchaType.ARITHMETIC_ZH.equals(type)) {
            code = arithmeticResult(codes);
        } else {
            code = new String(codes);
        }
        expr = codes;
        return codes;
    }

    /**
     * 获取两个数之间的随机数
     *
     * @param start 起始值
     * @param end   结束值
     * @return 随机数
     */
    public static int nextInt(int start, int end) {
        return Math.min(start, end) + nextInt(Math.abs(start - end));
    }

    /**
     * 获取0～end之间的随机数
     *
     * @param end 结束值
     * @return 随机数
     */
    public static int nextInt(int end) {
        return RANDOM.nextInt(end);
    }

    /**
     * 获取默认的随机字符
     *
     * @return 随机字符
     */
    public static char nextChar() {
        return ConstArray.NUM_EN_MIX[nextInt(ConstArray.NUM_EN_MIX.length)];
    }

    /**
     * 获取随机字符
     *
     * @param type 字符类型
     * @return 随机字符
     */
    public static char nextChar(int type) {
        switch (type) {
            case 1:
                return ConstArray.EN_MIX[nextInt(ConstArray.EN_MIX.length)];
            case 2:
                return ConstArray.EN_LOWER[nextInt(ConstArray.EN_LOWER.length)];
            case 3:
                return ConstArray.EN_UPPER[nextInt(ConstArray.EN_UPPER.length)];
            case 4:
                return ConstArray.NUM_EN_MIX_LOWER[nextInt(ConstArray.NUM_EN_MIX_LOWER.length)];
            case 5:
                return ConstArray.NUM_EN_MIX_UPPER[nextInt(ConstArray.NUM_EN_MIX_UPPER.length)];
            default:
                return nextChar();
        }
    }


    /**
     * 在指定的颜色范围内随机选择一种颜色
     *
     * @param start 开始值
     * @param end   结束值
     * @return RGB颜色
     */
    protected Color color(int start, int end) {
        start = Math.min(start, 255);
        end = Math.min(end, 255);
        int r = nextInt(start, end), g = nextInt(start, end), b = nextInt(start, end);
        return new Color(r, g, b);
    }

    /**
     * 随机从Web安全色中选取一种颜色
     *
     * @return RGB颜色
     */
    protected Color color() {
        int[] rgb = ConstArray.COLORS[nextInt(ConstArray.COLORS.length)];
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * 将图片转换为Base64格式字符菜
     *
     * @param type 图片类型
     * @return 二进制图片数据
     */
    public String toBase64(String type) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamRenderTarget renderTarget = new OutputStreamRenderTarget(outputStream)) {
            render(renderTarget);
            return type + Base64.getEncoder().encodeToString(outputStream.toByteArray());
        }
    }

    /**
     * 获取验证码
     *
     * @return 验证码字符串
     */
    public String getCaptchaCode() {
        checkCode();
        return code;
    }

    /**
     * 获取验证码字符数组
     *
     * @return 验证码字符数组
     */
    public char[] text() {
        checkCode();
        return expr;
    }

    /**
     * 绘制干扰线条
     *
     * @param g Graphics2D
     */
    public void drawLine(Graphics2D g) {
        drawLine(nextInt(3, 5), g);
    }

    /**
     * 绘制干扰线条
     *
     * @param size 线条数量
     * @param g    Graphics2D
     */
    public void drawLine(int size, Graphics2D g) {
        size = Math.max(size, 3);
        for (int i = 0; i < size; i++) {
            g.setColor(color());
            int x = nextInt(5);
            int y = nextInt(height);
            int _width = nextInt(width - 5, width);
            int _height = nextInt(height);
            g.drawLine(x, y, _width, _height);
        }
    }

    /**
     * 绘制圆圈
     *
     * @param g Graphics2D
     */
    public void drawOval(Graphics2D g) {
        drawOval(nextInt(3, 8), g);
    }

    /**
     * 绘制圆圈
     *
     * @param size 数量
     * @param g    Graphics2D
     */
    public void drawOval(int size, Graphics2D g) {
        size = Math.max(size, 3);
        for (int i = 0; i < size; i++) {
            g.setColor(color());
            g.drawOval(nextInt(width - 20), nextInt(height - 10), nextInt(5, 15), nextInt(5, 15));
        }
    }

    /**
     * 绘制贝塞尔曲线
     *
     * @param g Graphics2D
     */
    public void drawBezierLine(Graphics2D g) {
        drawBezierLine(nextInt(2, 5), g);
    }

    /**
     * 绘制贝塞尔曲线
     *
     * @param size 曲线数量
     * @param g    Graphics2D
     */
    public void drawBezierLine(int size, Graphics2D g) {
        size = Math.max(size, 2);
        for (int i = 0; i < size; i++) {
            g.setColor(color());
            int x1 = 5, y1 = nextInt(5, height / 2);
            int x2 = width - 5, y2 = nextInt(height / 2, height - 5);
            int ctrlX = nextInt(width / 4, width / 4 * 3), ctrlY = nextInt(5, height - 5);
            if (nextInt(2) == 0) {
                int ty = y1;
                y1 = y2;
                y2 = ty;
            }
            if (nextInt(2) == 0) {
                //绘制二阶贝塞尔曲线
                QuadCurve2D shape = new QuadCurve2D.Double();
                shape.setCurve(x1, y1, ctrlX, ctrlY, x2, y2);
                g.draw(shape);
            } else {
                //绘制三阶贝塞尔曲线
                int ctrlX1 = nextInt(width / 4, width / 4 * 3), ctrlY1 = nextInt(5, height - 5);
                CubicCurve2D shape =
                        new CubicCurve2D.Double(x1, y1, ctrlX, ctrlY, ctrlX1, ctrlY1, x2, y2);
                g.draw(shape);
            }
        }
    }

    public Font getFont() {
        if (font == null) {
            font = Fonts.getInstance().defaultFont();
        }
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public CaptchaType getType() {
        return type;
    }

    public void setType(CaptchaType type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public char[] getExpr() {
        return expr;
    }


    /**
     * 检查验证码是否生成
     */
    public void checkCode() {
        if (code == null || "".equals(code.trim())) {
            captcha();
        }
    }

    /**
     * 生成十位数以内的运算表达式的字符（不包含十位数）
     *
     * @param i     字符位置
     * @param zh_cn 是否为中文简体形式
     * @return 下标为i的字符
     */
    private static char arithmeticExp(int i, boolean zh_cn) {
        if (zh_cn) {
            switch (i) {
                case 0:
                case 2:
                    return ConstArray.NUM_ZH_CN[nextInt(ConstArray.NUM_ZH_CN.length)];
                case 1:
                    return ConstArray.OPERATOR_ZH[nextInt(ConstArray.OPERATOR_ZH.length)];
                case 3:
                    return '=';
                default:
                    return '?';
            }
        } else {
            switch (i) {
                case 0:
                case 2:
                    return ConstArray.NUM_ARABIC[nextInt(ConstArray.NUM_ARABIC.length)];
                case 1:
                    return ConstArray.OPERATOR_EN[nextInt(ConstArray.OPERATOR_EN.length)];
                case 3:
                    return '=';
                default:
                    return '?';
            }
        }
    }

    /**
     * 获取运算表达式的计算值
     *
     * @param codes 运算表达式字符
     * @return 运算结果
     */
    private static String arithmeticResult(char[] codes) {
        char operator = codes[1];
        int num_1 = analysisChar(codes[0]);
        int num_2 = analysisChar(codes[2]);
        if (operator == '+' || operator == '\u52a0') {
            return String.valueOf((num_1 + num_2));
        } else if (operator == '-' || operator == '\u51cf') {
            return String.valueOf((num_1 - num_2));
        } else if (operator == '*' || operator == 'x' || operator == 'X' || operator == '\u4e58') {
            return String.valueOf((num_1 * num_2));
        } else {
            return "0";
        }
    }

    /**
     * 解析运算表达式字符
     *
     * @param character 表达式字符
     * @return 罗马数字字符
     */
    private static int analysisChar(char character) {
        if (character == '0' || character == '\u96f6') {
            return 0;
        } else if (character == '1' || character == '\u58f9' || character == '\u4e00') {
            return 1;
        } else if (character == '2' || character == '\u8d30' || character == '\u4e8c') {
            return 2;
        } else if (character == '3' || character == '\u53c1' || character == '\u4e09') {
            return 3;
        } else if (character == '4' || character == '\u8086' || character == '\u56db') {
            return 4;
        } else if (character == '5' || character == '\u4f0d' || character == '\u4e94') {
            return 5;
        } else if (character == '6' || character == '\u9646' || character == '\u516d') {
            return 6;
        } else if (character == '7' || character == '\u67d2' || character == '\u4e03') {
            return 7;
        } else if (character == '8' || character == '\u634c' || character == '\u516b') {
            return 8;
        } else if (character == '9' || character == '\u7396' || character == '\u4e5d') {
            return 9;
        } else {
            return 0;
        }
    }

    protected BufferedImage createImage() {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setBackground(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        return img;
    }

    /**
     * 设置验证码
     */
    public void setCaptchaCode(String code) {
        this.code = code;
        this.expr = code.toCharArray();
        this.length = code.length();
    }

    @Override
    public void render() throws IOException {
        sessionHolder.set(SESSION_KEY, getCaptchaCode());
        try(RenderTarget renderTarget = sessionHolder.createRenderTarget()) {
            render(renderTarget);
        }
    }

}
