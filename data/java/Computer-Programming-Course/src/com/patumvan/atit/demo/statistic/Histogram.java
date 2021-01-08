/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patumvan.atit.demo.statistic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;

public class Histogram {

    public static void main(String[] args) {
        int[] data = {3, 10, 2, 8, 10, 16, 3, 12, 18, 11, 10, 3, 4, 6, 10};
        HashMap<Integer, Integer> histData = new HashMap<Integer, Integer>();
        for (int index = 0; index < data.length; index++) {
            System.out.println();
            if (histData.get(data[index]) == null) {
                histData.put(data[index], 1);
            } else {
                histData.put(data[index], histData.get(data[index]) + 1);
            }

        }

        Iterator<Integer> it = histData.keySet().iterator();
        while (it.hasNext()) {
            int key = it.next();
            int value = histData.get(key);
            System.out.println("Key : " + key + "   Value: " + value);
        }


    }
}
