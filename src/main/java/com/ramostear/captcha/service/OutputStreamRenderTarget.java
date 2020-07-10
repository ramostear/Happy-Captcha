package com.ramostear.captcha.service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;

public final class OutputStreamRenderTarget implements RenderTarget {

    private final SoftReference<OutputStream> holder;

    public OutputStreamRenderTarget(OutputStream holder) {
        this.holder = new SoftReference<>(holder);
    }

    @Override
    public void write(BufferedImage img) throws IOException {
        try (OutputStream output = holder.get()) {
            ImageIO.write(img, "png", output);
            output.flush();
        }
    }

    @Override
    public Object getTarget() {
        return holder.get();
    }

    @Override
    public void write(byte char_) throws IOException {
        holder.get().write(char_);
    }

    @Override
    public void write(int char_) throws IOException {
        holder.get().write(char_);
    }

    @Override
    public void write(byte[] colorTab, int offset, int length) throws IOException {
        holder.get().write(colorTab, offset, length);
    }

    @Override
    public void flush() throws IOException {
        holder.get().flush();
    }

    @Override
    public void close() {
        holder.clear();
    }

}
