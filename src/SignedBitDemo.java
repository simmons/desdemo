/*
 * Copyright 2011 David Simmons
 * http://cafbit.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.Arrays;

/**
 * Super-slow DES implementation for the overly patient.
 * 
 * http://www.unsw.adfa.edu.au/~lpb/src/DEScalc/DEScalc.html
 * 
 * @author simmons
 */
public class SignedBitDemo {
    
    public static void testPromotion() {
        byte b = (byte)0xFF;
        short s = (short)0xFFFF;
        int i = 0xFFFFFFFF;
        
        show(
            "promoting byte 0xFF by simple cast.  FAIL.",
            b, (short)b, (int)b, (long)b
        );
        show(
            "promoting short 0xFFFF by simple cast.  FAIL.",
            s, (int)s, (long)s
        );
        show(
            "promoting int 0xFFFFFFFF by simple cast.  FAIL.",
            i, (long)i
        );

        
        show(
            "promoting byte 0xFF with mask.  PASS.",
            b, (short)(b & 0xFF), (int)(b & 0xFF), (long)(b & 0xFF)
        );
        show(
            "promoting short 0xFFFF with mask.  PASS.",
            s, (int)(s & 0xFFFF), (long)(s & 0xFFFF)
        );
        show(
            "promoting int 0xFFFFFFFF with mask.  PASS.",
            i, (long)(i & 0xFFFFFFFFL) // the L is important!
        );
    }

    public static void testDemotion() {
        short s = (short)0xFF;
        int i = 0xFFFF;
        long l = 0xFFFFFFFFL;
        
        show(
            "demoting short 0xFF by simple cast.  PASS.",
            (byte)s, s
        );
        show(
            "demoting int 0xFFFF by simple cast.  PASS.",
            (byte)i, (short)i
        );
        show(
            "demoting long 0xFFFFFFFF by simple cast.  PASS.",
            (byte)l, (short)l, (int)l
        );
    }
    
    public static void main(String[] args) {
        testPromotion();
        testDemotion();
        
        {
            byte a = (byte)0x01;
            byte b = (byte)0x80;
            short s = (short) (a<<8 | b);
            System.out.printf("((byte)0x%02X)<<8 | ((byte)0x%02X) = %04X  FAIL.\n",a,b,s);
        }
        {
            byte a = (byte)0x81;
            byte b = (byte)0x80;
            short s = (short) (a<<8 | (b&0xFF));
            System.out.printf("(((byte)0x%02X)&0xFF)<<8 | ((byte)0x%02X) = %04X  FAIL.\n",a,b,s);
        }
    }

    //// utility methods
    
    public static void show(String msg, byte b, short s, int i, long l) {
        System.out.println(msg);
        System.out.println("byte:  "+byteAsBitstring(b));
        System.out.println("short: "+shortAsBitstring(s));
        System.out.println("int:   "+intAsBitstring(i));
        System.out.println("long:  "+longAsBitstring(l));       
    }
    public static void show(String msg, short s, int i, long l) {
        System.out.println(msg);
        System.out.println("short: "+shortAsBitstring(s));
        System.out.println("int:   "+intAsBitstring(i));
        System.out.println("long:  "+longAsBitstring(l));       
    }
    public static void show(String msg, int i, long l) {
        System.out.println(msg);
        System.out.println("int:   "+intAsBitstring(i));
        System.out.println("long:  "+longAsBitstring(l));       
    }
    public static void show(String msg, byte b, short s, int i) {
        System.out.println(msg);
        System.out.println("byte:  "+byteAsBitstring(b));
        System.out.println("short: "+shortAsBitstring(s));
        System.out.println("int:   "+intAsBitstring(i));
    }
    public static void show(String msg, byte b, short s) {
        System.out.println(msg);
        System.out.println("byte:  "+byteAsBitstring(b));
        System.out.println("short: "+shortAsBitstring(s));
    }
    
    public static String byteAsBitstring(byte b) {
        StringBuilder sb = new StringBuilder();
        for (int i=7; i>=0; i--) {
            byte bit = (byte) (b>>i & 0x01);
            sb.append(bit);
        }
        return sb.toString();
    }

    public static String shortAsBitstring(short s) {
        StringBuilder sb = new StringBuilder();
        for (int i=15; i>=0; i--) {
            byte bit = (byte) (s>>i & 0x01);
            sb.append(bit);
            if ((i%8)==0) { sb.append(' '); }
        }
        return sb.toString();
    }

    public static String intAsBitstring(int b) {
        StringBuilder sb = new StringBuilder();
        for (int i=31; i>=0; i--) {
            byte bit = (byte) (b>>i & 0x01);
            sb.append(bit);
            if ((i%8)==0) { sb.append(' '); }
        }
        return sb.toString();
    }
    
    public static String longAsBitstring(long b) {
        StringBuilder sb = new StringBuilder();
        for (int i=63; i>=0; i--) {
            byte bit = (byte) (b>>i & 0x01);
            sb.append(bit);
            if ((i%8)==0) { sb.append(' '); }
        }
        return sb.toString();
    }
    
    public static void testByte(byte b) {
        System.out.printf("byte 0x%02X = %d = %s\n", b, b, byteAsBitstring(b));     
    }
    public static void testInt(int b) {
        System.out.printf("int 0x%08X = %d = %s\n", b, b, intAsBitstring(b));
    }
    public static void testLong(long b) {
        System.out.printf("long 0x%016X = %s\n", b, longAsBitstring(b));
    }

    public static int charToNibble(char c) {
        if (c>='0' && c<='9') {
            return (c-'0');
        } else if (c>='a' && c<='f') {
            return (10+c-'a');
        } else if (c>='A' && c<='F') {
            return (10+c-'A');
        } else {
            return 0;
        }
    }
    public static byte[] parseBytes(String s) {
        s = s.replace(" ", "");
        byte[] ba = new byte[s.length()/2];
        if (s.length()%2 > 0) { s = s+'0'; }
        for (int i=0; i<s.length(); i+=2) {
            ba[i/2] = (byte) (charToNibble(s.charAt(i))<<4 | charToNibble(s.charAt(i+1)));
        }
        return ba;
    }
    
    private static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<bytes.length; i++) {
            sb.append(String.format("%02X ",bytes[i]));
        }
        return sb.toString();
    }

}