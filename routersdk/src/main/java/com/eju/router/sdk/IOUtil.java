package com.eju.router.sdk;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.zip.ZipInputStream;


/*package*/ class IOUtil {

    private IOUtil() {
    }

    static boolean deleteFile(final File file) {
        return file.exists() && file.delete();
    }

    static void recursiveDelete(final File file) {
        if (file.isDirectory() && file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    recursiveDelete(f);
                }
            }
        }
        deleteFile(file);
    }

    public static void deleteChildren(final File root) {
        if (root.isDirectory() && root.exists()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File f : files) {
                    recursiveDelete(f);
                }
            }
        }
    }

    public static void writeText(String text, final OutputStream out) throws IOException {
        writeText(text, "UTF-8", out);
    }

    public static void writeText(String text, String encode, final OutputStream out) throws IOException {
        try {
            out.write(text.getBytes(encode));
        } finally {
            close(out);
        }
    }

    public static void tryWriteText(String text, String encode, final OutputStream out) {
        try {
            writeText(text, encode, out);
        } catch (IOException e) {
            EjuLog.e("Error occurs when process io.");
        }
    }

    public static void tryWriteText(String text, final OutputStream out) {
        tryWriteText(text, "UTF-8", out);
    }

    public static String readText(String encode, final InputStream in) throws IOException {
        byte[] bytes = readBytes(in);
        return new String(bytes, encode);
    }

    public static String readText(final InputStream in) throws IOException {
        return readText("UTF-8", in);
    }

    public static String tryReadText(String encode, final InputStream in) {
        try {
            return readText(encode, in);
        } catch (IOException e) {
            EjuLog.e("Error occurs when process io.");
        }
        return null;
    }

    public static String tryReadText(final InputStream in) {
        return tryReadText("UTF-8", in);
    }

    public static void writeBytes(byte[] bytes, final OutputStream out) throws IOException {
        try {
            out.write(bytes);
        } finally {
            close(out);
        }
    }

    public static byte[] readBytes(final InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4098];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        return out.toByteArray();
    }

    public static byte[] tryReadBytes(final InputStream in) {
        try {
            return readBytes(in);
        } catch (IOException e) {
            EjuLog.e("Error occurs when process io.");
        }
        return null;
    }

    public static void tryWriteBytes(byte[] bytes, final OutputStream out) {
        try {
            writeBytes(bytes, out);
        } catch (IOException e) {
            EjuLog.e("Error occurs when process io.");
        }
    }

    public static void pipe(final InputStream in, final OutputStream out,
                            final int bufferSize)
            throws IOException {
        byte[] buffer = new byte[bufferSize];
        int len;
        try {
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } finally {
            close(out, in);
        }
    }

    public static void close(Closeable... closeable) {
        for (Closeable cls : closeable) {
            if (cls != null && !(cls instanceof ZipInputStream)) {
                try {
                    cls.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static void tryPipe(final InputStream in, final OutputStream out,
                               final int bufferSize) {
        try {
            pipe(in, out, bufferSize);
        } catch (IOException e) {
            EjuLog.e("Error occurs when process io.");
        }
    }

    public static void pipeLarge(final FileInputStream in, final FileOutputStream out)
            throws IOException {
        FileChannel readChannel = null;
        FileChannel writeChannel = null;
        try {
            readChannel = in.getChannel();
            writeChannel = out.getChannel();
            readChannel.transferTo(0, readChannel.size(), writeChannel);
        } finally {
            close(writeChannel, readChannel, out, in);
        }
    }

    /**
     * 获取单个文件的MD5值
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return "";
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
//        BigInteger bigInt = new BigInteger(1, digest.digest());
        String result = toHexString(digest.digest());
        if (result == null) {
            result = "";
        }
        return result;
//        return bigInt.toString(16);
    }

    private static final char[] hexArray = "0123456789abcdef".toCharArray();

    private static final String toHexString(byte[] bs) {
        if (bs == null) return null;
        char[] hexChars = new char[bs.length * 2];
        for (int i = 0; i < bs.length; i++) {
            int v = bs[i] & 0xff;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0f];
        }
        return new String(hexChars);
    }

    //复制文件
    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        copy(in, dst);
    }

    //复制文件
    public static void copy(InputStream in, File dst) throws IOException {
        if (!dst.getParentFile().exists()) {
            dst.getParentFile().mkdirs();
        }
        OutputStream out = new FileOutputStream(dst);
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
