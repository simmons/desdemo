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
public class DESorig {
	
	public static String byteAsBitstring(byte b) {
		StringBuilder sb = new StringBuilder();
		for (int i=7; i>=0; i--) {
			byte bit = (byte) (b>>i & 0x01);
			sb.append(bit);
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

	public static void testCase(byte[] message, byte[] expected, String password) {
		testCase(message, expected, passwordToKey(password));
	}
	public static void testCase(byte[] message, byte[] expected, byte[] key) {
		System.out.println("message:  "+hex(message));
		System.out.println("key:      "+hex(key));
		System.out.println("expected: "+hex(expected));
		byte[] received = encrypt(message, key);
		System.out.println("received: "+hex(received)+" "+((Arrays.equals(expected, received))?"PASS":"FAIL"));
	}
	
	public static void test() {

		testCase(
			parseBytes("a4b2 c9ef 0876 c1ce 438d e282 3820 dbde"),
			parseBytes("fa60 69b9 85fa 1cf7 0bea a041 9137 a6d3"),
			"mypass"
		);

		testCase(
			parseBytes("f3ed a6dc f8b7 9dd6 5be0 db8b 1e7b a551"),
			parseBytes("b669 d033 6c3f 42b7 68e8 e937 b4a5 7546"),
			"mypass"
		);

		// http://orlingrabbe.com/des.htm
		testCase(
			parseBytes("0123456789ABCDEF"),
			parseBytes("85E813540F0AB405"),
			parseBytes("133457799BBCDFF1")
		);

		
/*
		// http://orlingrabbe.com/des.htm
		
		byte[] message = new byte[] {
			(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
			(byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF
		};
		byte[] key = new byte[] {
			// K = 133457799BBCDFF1
			(byte)0x13, (byte)0x34, (byte)0x57, (byte)0x79,
			(byte)0x9B, (byte)0xBC, (byte)0xDF, (byte)0xF1
		};
		byte[] expect = new byte[] {
			(byte)0x85, (byte)0xE8, (byte)0x13, (byte)0x54,
			(byte)0x0F, (byte)0x0A, (byte)0xB4, (byte)0x05
		};

		long r = encryptBlock(getLongFromBytes(message, 0), getLongFromBytes(key, 0));
		byte[] cipher = new byte[8];
		getBytesFromLong(cipher, 0, r);
		
		if ((cipher == null) || (! Arrays.equals(cipher, expect))) {
			System.out.println("***** DES TEST: FAIL *****");
		} else {
			System.out.println("***** DES TEST: PASS *****");
		}
		System.out.println("message:\n"+Util.hexDump(message));
		System.out.println("key:\n"+Util.hexDump(key));
		System.out.println("expected:\n"+Util.hexDump(expect));
		System.out.println("cipher:\n"+Util.hexDump(cipher));
*/
	}
	
	private static final byte[] IP = { 
		58,	50,	42,	34,	26,	18,	10,	2,
		60,	52,	44,	36,	28,	20,	12,	4,
		62,	54,	46,	38,	30,	22,	14,	6,
		64,	56,	48,	40,	32,	24,	16,	8,
		57,	49,	41,	33,	25,	17,	9,	1,
		59,	51,	43,	35,	27,	19,	11,	3,
		61,	53,	45,	37,	29,	21,	13,	5,
		63,	55,	47,	39,	31,	23,	15,	7
	};
	
	private static final byte[] FP = {
		40, 8, 48, 16, 56, 24, 64, 32,
		39, 7, 47, 15, 55, 23, 63, 31,
		38, 6, 46, 14, 54, 22, 62, 30,
		37, 5, 45, 13, 53, 21, 61, 29,
		36, 4, 44, 12, 52, 20, 60, 28,
		35, 3, 43, 11, 51, 19, 59, 27,
		34, 2, 42, 10, 50, 18, 58, 26,
		33, 1, 41, 9, 49, 17, 57, 25
	};
	
	private static final byte[] E = {
		32,	1,	2,	3,	4,	5,
		4,	5,	6,	7,	8,	9,
		8,	9,	10,	11,	12,	13,
		12,	13,	14,	15,	16,	17,
		16,	17,	18,	19,	20,	21,
		20,	21,	22,	23,	24,	25,
		24,	25,	26,	27,	28,	29,
		28,	29,	30,	31,	32,	1
	};
	
	private static final byte[] P = {
		16,	7,	20,	21,
		29,	12,	28,	17,
		1,	15,	23,	26,
		5,	18,	31,	10,
		2,	8,	24,	14,
		32,	27,	3,	9,
		19,	13,	30,	6,
		22,	11,	4,	25
	};
	
	private static final byte[] PC1 = {
		57,	49,	41,	33,	25,	17,	9,
		1,	58,	50,	42,	34,	26,	18,
		10,	2,	59,	51,	43,	35,	27,
		19,	11,	3,	60,	52,	44,	36,
		63,	55,	47,	39,	31,	23,	15,
		7,	62,	54,	46,	38,	30,	22,
		14,	6,	61,	53,	45,	37,	29,
		21,	13,	5,	28,	20,	12,	4
	};
	
	private static final byte[] PC2 = {
		14,	17,	11,	24,	1,	5,
		3,	28,	15,	6,	21,	10,
		23,	19,	12,	4,	26,	8,
		16,	7,	27,	20,	13,	2,
		41,	52,	31,	37,	47,	55,
		30,	40,	51,	45,	33,	48,
		44,	49,	39,	56,	34,	53,
		46,	42,	50,	36,	29,	32
	};
	
	private static final byte[][] S = { {
		14,	4,	13,	1,	2,	15,	11,	8,	3,	10,	6,	12,	5,	9,	0,	7,
		0,	15,	7,	4,	14,	2,	13,	1,	10,	6,	12,	11,	9,	5,	3,	8,
		4,	1,	14,	8,	13,	6,	2,	11,	15,	12,	9,	7,	3,	10,	5,	0,
		15,	12,	8,	2,	4,	9,	1,	7,	5,	11,	3,	14,	10,	0,	6,	13
	}, {
		15,	1,	8,	14,	6,	11,	3,	4,	9,	7,	2,	13,	12,	0,	5,	10,
		3,	13,	4,	7,	15,	2,	8,	14,	12,	0,	1,	10,	6,	9,	11,	5,
		0,	14,	7,	11,	10,	4,	13,	1,	5,	8,	12,	6,	9,	3,	2,	15,
		13,	8,	10,	1,	3,	15,	4,	2,	11,	6,	7,	12,	0,	5,	14,	9
	}, {
		10,	0,	9,	14,	6,	3,	15,	5,	1,	13,	12,	7,	11,	4,	2,	8,
		13,	7,	0,	9,	3,	4,	6,	10,	2,	8,	5,	14,	12,	11,	15,	1,
		13,	6,	4,	9,	8,	15,	3,	0,	11,	1,	2,	12,	5,	10,	14,	7,
		1,	10,	13,	0,	6,	9,	8,	7,	4,	15,	14,	3,	11,	5,	2,	12
	}, {
		7,	13,	14,	3,	0,	6,	9,	10,	1,	2,	8,	5,	11,	12,	4,	15,
		13,	8,	11,	5,	6,	15,	0,	3,	4,	7,	2,	12,	1,	10,	14,	9,
		10,	6,	9,	0,	12,	11,	7,	13,	15,	1,	3,	14,	5,	2,	8,	4,
		3,	15,	0,	6,	10,	1,	13,	8,	9,	4,	5,	11,	12,	7,	2,	14
	}, {
		2,	12,	4,	1,	7,	10,	11,	6,	8,	5,	3,	15,	13,	0,	14,	9,
		14,	11,	2,	12,	4,	7,	13,	1,	5,	0,	15,	10,	3,	9,	8,	6,
		4,	2,	1,	11,	10,	13,	7,	8,	15,	9,	12,	5,	6,	3,	0,	14,
		11,	8,	12,	7,	1,	14,	2,	13,	6,	15,	0,	9,	10,	4,	5,	3
	}, {
		12,	1,	10,	15,	9,	2,	6,	8,	0,	13,	3,	4,	14,	7,	5,	11,
		10,	15,	4,	2,	7,	12,	9,	5,	6,	1,	13,	14,	0,	11,	3,	8,
		9,	14,	15,	5,	2,	8,	12,	3,	7,	0,	4,	10,	1,	13,	11,	6,
		4,	3,	2,	12,	9,	5,	15,	10,	11,	14,	1,	7,	6,	0,	8,	13
	}, {
		4,	11,	2,	14,	15,	0,	8,	13,	3,	12,	9,	7,	5,	10,	6,	1,
		13,	0,	11,	7,	4,	9,	1,	10,	14,	3,	5,	12,	2,	15,	8,	6,
		1,	4,	11,	13,	12,	3,	7,	14,	10,	15,	6,	8,	0,	5,	9,	2,
		6,	11,	13,	8,	1,	4,	10,	7,	9,	5,	0,	15,	14,	2,	3,	12
	}, {
		13,	2,	8,	4,	6,	15,	11,	1,	10,	9,	3,	14,	5,	0,	12,	7,
		1,	15,	13,	8,	10,	3,	7,	4,	12,	5,	6,	11,	0,	14,	9,	2,
		7,	11,	4,	1,	9,	12,	14,	2,	0,	6,	10,	13,	15,	3,	5,	8,
		2,	1,	14,	7,	4,	10,	8,	13,	15,	12,	9,	0,	3,	5,	6,	11
	} };
	
	private static final byte[] rotations = {
		1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 
	};

	private static long permute(byte[] table, int srcWidth, long src) {
		long dst = 0;
		//System.out.println("src="+longAsBitstring(src));
		for (int i=0; i<table.length; i++) {
			int srcPos = srcWidth - table[i];
			//System.out.println("["+i+"] table[i]="+table[i]+" srcPos="+srcPos+"  srcBit="+(src>>srcPos & 0x01));
			dst = (dst<<1) | (src>>srcPos & 0x01);
		}
		return dst;
	}
	private static long IP(long src) { return permute(IP, 64, src); } // 64
	private static long FP(long src) { return permute(FP, 64, src); } // 64
	private static long E(int src) { return permute(E, 32, src&0xFFFFFFFFL); } // 48
	private static int P(int src) { return (int)permute(P, 32, src&0xFFFFFFFFL); } // 32
	private static long PC1(long src) { return permute(PC1, 64, src); } // 56
	private static long PC2(long src) { return permute(PC2, 56, src); } // 48
	private static byte S(int index, byte src) {
		// abcdef => afbcde
		src = (byte) (src&0x20 | ((src&0x01)<<4) | ((src&0x1E)>>1));
		return S[index-1][src];
	}
	
	private static long getLongFromBytes(byte[] ba, int offset) {
		System.out.println("getLongFromBytes ba.length="+ba.length+" offset="+offset);
		long l = 0;
		for (int i=0; i<8; i++) {
			l = l<<8 | (ba[offset+i] & 0xFFL);
		}
		return l;
	}
	
	private static void getBytesFromLong(byte[] ba, int offset, long l) {
		for (int i=7; i>=0; i--) {
			ba[offset+i] = (byte) (l & 0xFF);
			l = l >> 8;
		}
	}
	
	private static int feistel(int r, /* 48 bits */ long subkey) {
		// 1. expansion
		long e = E(r);
		// 2. key mixing
		long x = e ^ subkey;
		// 3. substitution
		int dst = 0;
		for (int i=0; i<8; i++) {
			dst>>>=4;
			int s = S(8-i, (byte)(x&0x3F));
			dst |= s << 28;
			x>>=6;
		}
		// 4. permutation
		return P(dst);
	}
	
	private static long[] createSubkeys(/* 64 bits */ long key) {
		long subkeys[] = new long[16];
		
		key = PC1(key);
		
		// split into 28-bit left and right (c and d) pairs.
		int c = (int) (key>>28);
		int d = (int) (key&0x0FFFFFFF);
		
		for (int i=0; i<16; i++) {
			// rotate the 28-bit values
			if (rotations[i] == 1) {
				// rotate by 1 bit
				c = ((c<<1) & 0x0FFFFFFF) | (c>>27);
				d = ((d<<1) & 0x0FFFFFFF) | (d>>27);
			} else {
				// rotate by 2 bits
				c = ((c<<2) & 0x0FFFFFFF) | (c>>26);
				d = ((d<<2) & 0x0FFFFFFF) | (d>>26);
			}
			
			long cd = (c&0xFFFFFFFFL)<<28 | (d&0xFFFFFFFFL);
			subkeys[i] = PC2(cd);
		}
		
		return subkeys; /* 48-bit values */ 
	}
	
	public static long encryptBlock(long m, /* 64 bits */ long key) {
		System.out.printf("encryptBlock() src=%016X key=%016X\n",m,key);
		
		long subkeys[] = createSubkeys(key);

		// initial permutation
		long ip = IP(m);
		int l = (int) (ip>>32);
		int r = (int) (ip&0xFFFFFFFFL);
		System.out.printf("M  = %016X = %s\n", m, longAsBitstring(m));
		System.out.printf("IP = %016X = %s\n",ip,longAsBitstring(ip));
		
		// perform 16 rounds
		for (int i=0; i<16; i++) {
			int previous_l = l;
			l = r;
			
			StringBuilder sb = new StringBuilder();
			long ski = subkeys[i];
			for (int j=0; j<8; j++) {
				long l1 = ski >> (42-(j*6));
				sb.append(String.format("%02x ",l1&0x3F));
			}
			System.out.printf("Rnd%d f(r%d=%08X, SK%d=%s) = %08X\n", i+1, i, r, i+1, sb, feistel(r, subkeys[i])); 
			
			r = previous_l ^ feistel(r, subkeys[i]);
		}
		
		System.out.printf("final_l=%08X final_r=%08X\n", l, r);
		
		// reverse the 32-bit segments (left to right; right to left)
		long rl = (r&0xFFFFFFFFL)<<32 | (l&0xFFFFFFFFL);
		
		System.out.printf("reversed: %016X\n", rl);
		
		// apply the final permutation
		long fp = FP(rl);

		System.out.printf("block cipher = %016X\n",fp);
		return fp;
	}
	
	public static byte[] encrypt(byte[] challenge, byte[] key) {
		if (challenge.length == 8) {
			long k = getLongFromBytes(key, 0);
			long r1 = encryptBlock(getLongFromBytes(challenge, 0), k);
			byte[] response = new byte[8];
			getBytesFromLong(response, 0, r1);
			return response;			
		} else if (challenge.length == 16) {
			long k = getLongFromBytes(key, 0);
			long r1 = encryptBlock(getLongFromBytes(challenge, 0), k);
			long r2 = encryptBlock(getLongFromBytes(challenge, 8), k);
			System.out.printf("r1 = %016X\n", r1);
			System.out.printf("r2 = %016X\n", r2);
			byte[] response = new byte[16];
			getBytesFromLong(response, 0, r1);
			getBytesFromLong(response, 8, r2);
			return response;
		} else {
			throw new RuntimeException("DES challenge must be 8 or 16 bytes.");
		}
	}

	/**
	 * 
	 * http://www.vidarholen.net/contents/junk/vnc.html
	 * 
	 * "The RFB specification says that VNC authentication is done by
	 * receiving a 16 byte challenge, encrypting it with DES using the
	 * user specified password, and sending back the resulting 16 bytes.
	 * The actual software encrypts the challenge with all the bit fields
	 * in each byte of the password mirrored."
	 * 
	 * @param password
	 * @return
	 */
	private static byte[] passwordToKey(String password) {
		byte[] pwbytes = password.getBytes();
		byte[] key = new byte[8];
		for (int i=0; i<8; i++) {
			if (i < pwbytes.length) {
				byte b = pwbytes[i];
				// flip the byte
				byte b2 = 0;
				for (int j=0; j<8; j++) {
					b2<<=1;
					b2 |= (b&0x01);
					b>>>=1;
				}
				key[i] = b2;
			} else {
				key[i] = 0;
			}
		}
		return key;
	}
	
	public static byte[] encrypt(byte[] challenge, String password) {
		return encrypt(challenge, passwordToKey(password));
	}
}
