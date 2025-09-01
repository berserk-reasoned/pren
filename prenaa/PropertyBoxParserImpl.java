package com.coremedia.iso;

import com.bumptech.glide.load.engine.bitmap_recycle.vCoN.evld;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.UserBox;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes5.dex */
public class PropertyBoxParserImpl extends AbstractBoxParser {
    static String[] EMPTY_STRING_ARRAY = new String[0];
    String clazzName;
    Properties mapping;
    String[] param;
    Pattern constuctorPattern = Pattern.compile("(.*)\\((.*?)\\)");
    StringBuilder buildLookupStrings = new StringBuilder();

    public PropertyBoxParserImpl(String... strArr) throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream("/isoparser-default.properties");
        try {
            Properties properties = new Properties();
            this.mapping = properties;
            try {
                properties.load(resourceAsStream);
                ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                Enumeration<URL> resources = (contextClassLoader == null ? ClassLoader.getSystemClassLoader() : contextClassLoader).getResources("isoparser-custom.properties");
                while (resources.hasMoreElements()) {
                    InputStream inputStreamOpenStream = resources.nextElement().openStream();
                    try {
                        this.mapping.load(inputStreamOpenStream);
                        inputStreamOpenStream.close();
                    } catch (Throwable th) {
                        inputStreamOpenStream.close();
                        throw th;
                    }
                }
                for (String str : strArr) {
                    this.mapping.load(getClass().getResourceAsStream(str));
                }
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        } catch (Throwable th2) {
            try {
                resourceAsStream.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            throw th2;
        }
    }

    public PropertyBoxParserImpl(Properties properties) {
        this.mapping = properties;
    }

    @Override // com.coremedia.iso.AbstractBoxParser
    public Box createBox(String str, byte[] bArr, String str2) throws ClassNotFoundException {
        invoke(str, bArr, str2);
        try {
            Class<?> cls = Class.forName(this.clazzName);
            String[] strArr = this.param;
            if (strArr.length > 0) {
                Class<?>[] clsArr = new Class[strArr.length];
                Object[] objArr = new Object[strArr.length];
                int i = 0;
                while (true) {
                    String[] strArr2 = this.param;
                    if (i < strArr2.length) {
                        if ("userType".equals(strArr2[i])) {
                            objArr[i] = bArr;
                            clsArr[i] = byte[].class;
                        } else if ("type".equals(this.param[i])) {
                            objArr[i] = str;
                            clsArr[i] = String.class;
                        } else if ("parent".equals(this.param[i])) {
                            objArr[i] = str2;
                            clsArr[i] = String.class;
                        } else {
                            throw new InternalError("No such param: " + this.param[i]);
                        }
                        i++;
                    } else {
                        return (Box) cls.getConstructor(clsArr).newInstance(objArr);
                    }
                }
            } else {
                return (Box) cls.newInstance();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e2) {
            throw new RuntimeException(e2);
        } catch (InstantiationException e3) {
            throw new RuntimeException(e3);
        } catch (NoSuchMethodException e4) {
            throw new RuntimeException(e4);
        } catch (InvocationTargetException e5) {
            throw new RuntimeException(e5);
        }
    }

    public void invoke(String str, byte[] bArr, String str2) {
        String property;
        if (bArr != null) {
            if (!UserBox.TYPE.equals(str)) {
                throw new RuntimeException("we have a userType but no uuid box type. Something's wrong");
            }
            property = this.mapping.getProperty(evld.iopAGbW + Hex.encodeHex(bArr).toUpperCase() + "]");
            if (property == null) {
                property = this.mapping.getProperty(String.valueOf(str2) + "-uuid[" + Hex.encodeHex(bArr).toUpperCase() + "]");
            }
            if (property == null) {
                property = this.mapping.getProperty(UserBox.TYPE);
            }
        } else {
            property = this.mapping.getProperty(str);
            if (property == null) {
                String string = this.buildLookupStrings.append(str2).append('-').append(str).toString();
                this.buildLookupStrings.setLength(0);
                property = this.mapping.getProperty(string);
            }
        }
        if (property == null) {
            property = this.mapping.getProperty("default");
        }
        if (property == null) {
            throw new RuntimeException("No box object found for " + str);
        }
        if (!property.endsWith(")")) {
            this.param = EMPTY_STRING_ARRAY;
            this.clazzName = property;
            return;
        }
        Matcher matcher = this.constuctorPattern.matcher(property);
        if (!matcher.matches()) {
            throw new RuntimeException("Cannot work with that constructor: " + property);
        }
        this.clazzName = matcher.group(1);
        if (matcher.group(2).length() == 0) {
            this.param = EMPTY_STRING_ARRAY;
        } else {
            this.param = matcher.group(2).length() > 0 ? matcher.group(2).split(",") : new String[0];
        }
    }
}
