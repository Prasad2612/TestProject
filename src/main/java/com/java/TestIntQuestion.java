package com.java;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestIntQuestion {

    // create two aray lists



    public static void main(String[] args) {
        ArrayList aOne=new ArrayList<>();
        ArrayList aTwo=new ArrayList<>();

        aOne.add("aa");
        aOne.add("bb");
        aOne.add("cc");
        aTwo.add("cc");
        aTwo.add("dd");
        aTwo.add("aa");

        String findAa="aa";

        System.out.println( "Original List One" +aOne);
        System.out.println( "Original List Two" +aTwo);

        List<String> resultlist1= (List<String>) Stream.concat(aOne.stream(), aTwo.stream()).filter(letter->letter.equals(findAa)).collect(Collectors.toList());
        System.out.println( "Original List One" +resultlist1);


      /*  for(String lettet: aOne) {
            if(lettet.equals(findAa)){
                resultlist.add(lettet);
            }
        }
        for(String lettet: aTwo) {
            if(lettet.equals(findAa)){
                resultlist.add(lettet);
            }
        }
        resultlist.add("aa");
        resultlist.add("bb");*/
    }
}
