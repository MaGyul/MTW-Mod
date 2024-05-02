package dev.magyul.util;

public class MUtil {
    private static final int PUNYCODE_TMIN = 1;
    private static final int PUNYCODE_TMAX = 26;
    private static final int PUNYCODE_SKEW = 38;
    private static final int PUNYCODE_DAMP = 700;
    private static final int PUNYCODE_INITIAL_BIAS = 72;
    private static final int PUNYCODE_INITIAL_N = 128;

    private static int punycodeBiasAdapt(int delta, int numPoints, boolean firstTime) {
        if(firstTime)
            delta /= PUNYCODE_DAMP;
        else
            delta /= 2;

        int k = 0;
        delta = delta + delta / numPoints;

        while(delta > ((36 - PUNYCODE_TMIN) * PUNYCODE_TMAX) / 2) {
            delta /= 36 - PUNYCODE_TMIN;
            k += 36;
        }

        return k + ((36 - PUNYCODE_TMIN + 1) * delta) / (delta + PUNYCODE_SKEW);
    }

    private static void punycodeEncodeNumber(StringBuilder dst, int q, int bias) {
        boolean keepGoing = true;

        for(int k = 36; keepGoing; k += 36) {
            //Compute & clamp threshold
            int t = k - bias;
            if(t < PUNYCODE_TMIN)
                t = PUNYCODE_TMIN;
            else if(t > PUNYCODE_TMAX)
                t = PUNYCODE_TMAX;

            //Compute digit
            int digit;
            if(q < t) {
                digit = q;
                keepGoing = false;
            } else {
                digit = t + (q - t) % (36 - t);
                q = (q - t) / (36 - t);
            }

            //Encode digit
            if(digit < 26)
                dst.append((char) ('a' + digit));
            else
                dst.append((char) ('0' + digit - 26));
        }
    }

    private static String punycodeEncodeString(int[] input) {
        StringBuilder output = new StringBuilder();

        for (int k : input) {
            if (k < 128)
                output.append((char) k);
        }

        int n = PUNYCODE_INITIAL_N;
        int delta = 0;
        int bias = PUNYCODE_INITIAL_BIAS;
        int h = output.length();
        int b = h;

        if(b > 0)
            output.append('-');

        while(h < input.length) {
            int m = Integer.MAX_VALUE;
            for (int j : input) {
                if (j >= n && j < m)
                    m = j;
            }

            delta = delta + (m - n) * (h + 1);
            n = m;

            for (int c : input) {
                if (c < n)
                    delta++;

                if (c == n) {
                    punycodeEncodeNumber(output, delta, bias);
                    bias = punycodeBiasAdapt(delta, h + 1, h == b);
                    delta = 0;
                    h++;
                }
            }

            delta++;
            n++;
        }

        return "xn--" + output;
    }

    public static String punycode(String url) {
        int protoEnd = url.indexOf("://");

        if(protoEnd < 0)
            protoEnd = 0;
        else
            protoEnd += 3;

        int hostEnd = url.indexOf('/', protoEnd);
        if(hostEnd < 0)
            hostEnd = url.length();

        String hostname = url.substring(protoEnd, hostEnd);
        boolean doTransform = false;

        for(int i = 0; i < hostname.length(); i++) {
            if(hostname.charAt(i) >= 128) {
                doTransform = true;
                break;
            }
        }

        if(!doTransform)
            return url;

        String[] parts = hostname.split("\\.");
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        sb.append(url, 0, protoEnd);

        for(String p: parts) {
            doTransform = false;

            for(int i = 0; i < p.length(); i++) {
                if(p.charAt(i) >= 128) {
                    doTransform = true;
                    break;
                }
            }

            if(first)
                first = false;
            else
                sb.append('.');

            if(doTransform)
                sb.append(punycodeEncodeString(p.codePoints().toArray()));
            else
                sb.append(p);
        }

        sb.append(url, hostEnd, url.length());
        return sb.toString();
    }
}
