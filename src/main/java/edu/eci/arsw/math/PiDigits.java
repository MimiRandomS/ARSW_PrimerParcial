package edu.eci.arsw.math;

import java.util.ArrayList;
import java.util.Arrays;

///  <summary>
///  An implementation of the Bailey-Borwein-Plouffe formula for calculating hexadecimal
///  digits of pi.
///  https://en.wikipedia.org/wiki/Bailey%E2%80%93Borwein%E2%80%93Plouffe_formula
///  *** Translated from C# code: https://github.com/mmoroney/DigitsOfPi ***
///  </summary>
public class PiDigits {
    private static ArrayList<Worker> workers = new ArrayList<>();

    /**
     * Returns a range of hexadecimal digits of pi.
     *
     * @param start The starting location of the range.
     * @param count The number of digits to return
     * @return An array containing the hexadecimal digits.
     */
    public static String getDigits(int start, int count, int numThreads) {
        if (start < 0) {
            throw new RuntimeException("Invalid Interval");
        }

        if (count < 0) {
            throw new RuntimeException("Invalid Interval");
        }

        int increment = count / numThreads;
        int intervalA = start;
        int intervalB = increment;
        for (int i = 0; i < numThreads; i++) {
            System.out.println(intervalA + "   " + intervalB + "   " + increment);
            workers.add(new Worker(intervalA, intervalB, increment + 1));
            intervalA = intervalB;
            if (i == numThreads - 1) intervalB = count;
            else intervalB += increment;
        }

        for (Worker worker : workers) {
            worker.start();
        }

        for (Worker worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        StringBuilder b = new StringBuilder("");
        for (int i = 0; i < workers.size(); i++) {
            byte[] byteWorker = workers.get(i).getDigitsWorker();
            b.append(bytesToHex(byteWorker).substring(0, byteWorker.length - 1));
        }
        return b.toString();
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<hexChars.length;i=i+2){
            sb.append(hexChars[i+1]);
        }
        return sb.toString();
    }


}
