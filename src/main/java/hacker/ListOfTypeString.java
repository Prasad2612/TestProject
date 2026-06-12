package hacker;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ListOfTypeString {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();

        list.add("java");
        list.add("kafka");
        list.add("java");
        list.add("kafka");
        list.add("java");
        list.add("aws");

        List<String> dupString=findDuplicates(list);
        List<String> dupStringStream=findDuplicatesByS(list);

        System.out.println(dupString);

    }

    private static List<String> findDuplicatesByS(List<String> list) {
        return list.stream().filter(s->s!=null).filter(s-> Collections.frequency(list,s)>1).distinct()
                .collect(Collectors.toList());

       /* return list.stream().filter(s->s!=null)
                .collect(Collectors.groupingBy(Function.identity(), Collector.countig())).entrySet().stream.;*/
    }

    private static List<String> findDuplicates(List<String> list) {
        List<String> duplicates=new ArrayList<String>();
        Set<String> uniqueSet=new HashSet<String>();
        for(String str: list){
           if(!uniqueSet.add(str)) {
               duplicates.add(str);
           }
        }

        return duplicates;
    }
}
