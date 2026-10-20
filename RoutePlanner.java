import java.nio.file.*;
import java.util.*;
import java.io.*;
public class RoutePlanner {
 record Edge(String to,double minutes){}
 record Entry(String node,double distance){}
 record Route(List<String> nodes,double minutes){}
 final Map<String,List<Edge>> graph=new TreeMap<>();
 static String id(String s){if(s==null||!s.matches("[A-Za-z0-9][A-Za-z0-9_-]{0,63}"))throw new IllegalArgumentException("Locations must be 1–64 letters/digits/_-");return s;}
 void add(String a,String b,double n){id(a);id(b);if(a.equals(b)||!Double.isFinite(n)||n<=0||n>1_000_000)throw new IllegalArgumentException("Edge requires distinct nodes and positive finite minutes <=1000000");if(graph.getOrDefault(a,List.of()).stream().anyMatch(e->e.to.equals(b)))throw new IllegalArgumentException("Duplicate edge");graph.computeIfAbsent(a,k->new ArrayList<>()).add(new Edge(b,n));graph.computeIfAbsent(b,k->new ArrayList<>()).add(new Edge(a,n));}
 static RoutePlanner load(Path path)throws IOException{
  if(Files.size(path)>1_000_000)throw new IOException("CSV exceeds 1 MB");var lines=Files.readAllLines(path);if(lines.isEmpty()||!lines.get(0).strip().equals("from,to,minutes"))throw new IllegalArgumentException("Header must be from,to,minutes");var p=new RoutePlanner();
  for(int i=1;i<lines.size();i++){if(lines.get(i).isBlank())continue;String[] f=lines.get(i).split(",",-1);if(f.length!=3)throw new IllegalArgumentException("Malformed row "+(i+1));p.add(f[0].strip(),f[1].strip(),Double.parseDouble(f[2].strip()));}
  return p;
 }
 Optional<Route> route(String from,String to,Set<String> avoid){
  id(from);id(to);if(!graph.containsKey(from)||!graph.containsKey(to))throw new IllegalArgumentException("Unknown endpoint");
  for(String a:avoid)if(!graph.containsKey(a))throw new IllegalArgumentException("Unknown avoided location: "+a);
  if(avoid.contains(from)||avoid.contains(to))throw new IllegalArgumentException("Cannot avoid an endpoint");
  Map<String,Double> dist=new HashMap<>();Map<String,String> previous=new HashMap<>();var q=new PriorityQueue<Entry>(Comparator.comparingDouble(Entry::distance).thenComparing(Entry::node));dist.put(from,0.0);q.add(new Entry(from,0));
  while(!q.isEmpty()){
   Entry cur=q.remove();if(cur.distance>dist.getOrDefault(cur.node,Double.POSITIVE_INFINITY))continue;if(cur.node.equals(to))break;
   for(Edge edge:graph.get(cur.node)){
    if(avoid.contains(edge.to))continue;double next=cur.distance+edge.minutes;
    if(next<dist.getOrDefault(edge.to,Double.POSITIVE_INFINITY)){dist.put(edge.to,next);previous.put(edge.to,cur.node);q.add(new Entry(edge.to,next));}
   }
  }
  if(!dist.containsKey(to))return Optional.empty();var nodes=new ArrayList<String>();for(String n=to;n!=null;n=previous.get(n))nodes.add(n);Collections.reverse(nodes);return Optional.of(new Route(List.copyOf(nodes),dist.get(to)));
 }
 static int run(String[] a){
  if(a.length!=3&&!(a.length==5&&a[3].equals("--avoid"))){System.err.println("Usage: RoutePlanner CSV FROM TO [--avoid NODE1,NODE2]");return 2;}
  try{
   RoutePlanner p=load(Path.of(a[0]));Set<String> avoid=a.length==5?new HashSet<>(Arrays.asList(a[4].split(","))):Set.of();var route=p.route(a[1],a[2],avoid);
   if(route.isEmpty()){System.out.println("No route available");return 1;}var r=route.get();System.out.println(String.join(" -> ",r.nodes));System.out.printf(Locale.ROOT,"%.2f minutes%n",r.minutes);return 0;
  }catch(IOException|IllegalArgumentException e){System.err.println("Error: "+e.getMessage());return 2;}
 }
 public static void main(String[] a){System.exit(run(a));}
}
