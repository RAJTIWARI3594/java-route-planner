import java.nio.file.*;import java.util.*;
public class RoutePlannerTest {
 static int count=0;static void check(boolean b){if(!b)throw new AssertionError("check "+(count+1));count++;}
 static void rejects(Runnable r){boolean bad=false;try{r.run();}catch(IllegalArgumentException e){bad=true;}check(bad);}
 public static void main(String[] a)throws Exception{
  var p=new RoutePlanner();p.add("A","B",2);p.add("B","C",3);p.add("A","C",10);p.add("X","Y",1);
  var r=p.route("A","C",Set.of()).orElseThrow();check(r.minutes()==5);check(r.nodes().equals(List.of("A","B","C")));check(p.route("C","A",Set.of()).orElseThrow().minutes()==5);check(p.route("A","C",Set.of("B")).orElseThrow().minutes()==10);check(p.route("A","A",Set.of()).orElseThrow().minutes()==0);check(p.route("A","X",Set.of()).isEmpty());
  rejects(()->p.route("Z","A",Set.of()));rejects(()->p.route("A","C",Set.of("A")));rejects(()->p.route("A","C",Set.of("Z")));rejects(()->p.add("A","B",4));rejects(()->p.add("A","D",-1));rejects(()->p.add("A","D",Double.NaN));rejects(()->p.add("A","A",1));rejects(()->p.add("bad name","D",1));
  var c=RoutePlanner.load(Path.of("sample/campus.csv"));check(c.route("GATE","LIBRARY",Set.of()).orElseThrow().minutes()==5);check(c.route("GATE","LIBRARY",Set.of("CAFETERIA")).orElseThrow().minutes()==7);
  check(RoutePlanner.run(new String[]{"sample/campus.csv","GATE","LAB"})==0);
  Path bad=Files.createTempFile("route", ".csv");Files.writeString(bad,"from,to,minutes\nA,B,Infinity");boolean b=false;try{RoutePlanner.load(bad);}catch(IllegalArgumentException e){b=true;}check(b);Files.delete(bad);
  System.out.println(count+" Java assertions passed");
 }
}
