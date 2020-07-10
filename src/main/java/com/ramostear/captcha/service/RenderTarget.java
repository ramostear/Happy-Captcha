package com.ramostear.captcha.service;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public interface RenderTarget extends Closeable {

    void write(BufferedImage img) throws IOException;

    Object getTarget();

    void write(byte char_) throws IOException;

    void write(int char_) throws IOException;

    void write(byte[] colorTab, int offset, int length) throws IOException;

    void flush() throws IOException;

    static RenderTarget of(OutputStream os) {
        return new OutputStreamRenderTarget(os);
    }

}
