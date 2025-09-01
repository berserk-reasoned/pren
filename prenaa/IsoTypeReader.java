package com.coremedia.iso;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/* loaded from: classes4.dex */
public final class IsoTypeReader {
    public static int byte2int(byte b) {
        return b < 0 ? b + 256 : b;
    }

    public static long readUInt32BE(ByteBuffer byteBuffer) {
        return (readUInt8(byteBuffer) << 24) + (readUInt8(byteBuffer) << 16) + (readUInt8(byteBuffer) << 8) + readUInt8(byteBuffer);
    }

    public static long readUInt32(ByteBuffer byteBuffer) {
        long j = byteBuffer.getInt();
        return j < 0 ? j + 4294967296L : j;
    }

    public static int readUInt24(ByteBuffer byteBuffer) {
        return (readUInt16(byteBuffer) << 8) + byte2int(byteBuffer.get());
    }

    public static int readUInt16(ByteBuffer byteBuffer) {
        return (byte2int(byteBuffer.get()) << 8) + byte2int(byteBuffer.get());
    }

    public static int readUInt16BE(ByteBuffer byteBuffer) {
        return byte2int(byteBuffer.get()) + (byte2int(byteBuffer.get()) << 8);
    }

    public static int readUInt8(ByteBuffer byteBuffer) {
        return byte2int(byteBuffer.get());
    }

    public static String readString(ByteBuffer byteBuffer) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            byte b = byteBuffer.get();
            if (b != 0) {
                byteArrayOutputStream.write(b);
            } else {
                return Utf8.convert(byteArrayOutputStream.toByteArray());
            }
        }
    }

    public static String readString(ByteBuffer byteBuffer, int i) {
        byte[] bArr = new byte[i];
        byteBuffer.get(bArr);
        return Utf8.convert(bArr);
    }

    public static long readUInt64(ByteBuffer byteBuffer) {
        long uInt32 = readUInt32(byteBuffer) << 32;
        if (uInt32 < 0) {
            throw new RuntimeException("I don't know how to deal with UInt64! long is not sufficient and I don't want to use BigInt");
        }
        return uInt32 + readUInt32(byteBuffer);
    }

    public static double readFixedPoint1616(ByteBuffer byteBuffer) {
        byteBuffer.get(new byte[4]);
        return (((((r0[0] << 24) & ViewCompat.MEASURED_STATE_MASK) | ((r0[1] << 16) & 16711680)) | ((r0[2] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)) | (r0[3] & 255)) / 65536.0d;
    }

    public static double readFixedPoint0230(ByteBuffer byteBuffer) {
        byteBuffer.get(new byte[4]);
        return (((((r0[0] << 24) & ViewCompat.MEASURED_STATE_MASK) | ((r0[1] << 16) & 16711680)) | ((r0[2] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)) | (r0[3] & 255)) / 1.073741824E9d;
    }

    public static float readFixedPoint88(ByteBuffer byteBuffer) {
        byteBuffer.get(new byte[2]);
        return ((short) (((short) ((r0[0] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)) | (r0[1] & 255))) / 256.0f;
    }

    public static String readIso639(ByteBuffer byteBuffer) {
        int uInt16 = readUInt16(byteBuffer);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append((char) (((uInt16 >> ((2 - i) * 5)) & 31) + 96));
        }
        return sb.toString();
    }

    public static String read4cc(ByteBuffer byteBuffer) {
        byte[] bArr = new byte[4];
        byteBuffer.get(bArr);
        try {
            return new String(bArr, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static long readUInt48(ByteBuffer byteBuffer) {
        long uInt16 = readUInt16(byteBuffer) << 32;
        if (uInt16 < 0) {
            throw new RuntimeException("I don't know how to deal with UInt64! long is not sufficient and I don't want to use BigInt");
        }
        return uInt16 + readUInt32(byteBuffer);
    }
}
