import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.core.*;


public class Weka {

	static int N_neg1 = 3125;
	static int N_neg2 = 3127;
	static int N_neg3 = 3377;
	static int N_neg4 = 2876;
	static int N_pos1 = 3100;
	static int N_pos2 = 3112;
	static int N_pos3 = 3136;
	static int N_pos4 = 3148;
	
	static int N_neg1_2 = 3176;
	static int N_neg2_2 = 3088;
	static int N_neg3_2 = 3160;
	static int N_neg4_2 = 3072;
	
	static int N_pos1_2 = 3132;
	static int N_pos2_2 = 3196;
	static int N_pos3_2 = 3043;
	static int N_pos4_2 = 3125;
	static int N_words = 100;
	
	public static <K, V extends Comparable<? super V>> HashMap<K, V> 
    sortByValue( HashMap<K, V> map ){
		
	    List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
	    
	    Collections.sort( list, new Comparator<Entry<K, V>>() {
	        public int compare( Entry<K, V> o1, Entry<K, V> o2 ){
	            return -1*(o1.getValue()).compareTo( o2.getValue() );
	        }
	    } );
	
	    HashMap<K, V> result = new LinkedHashMap<K, V>();
	    for (Entry<K, V> entry : list){
	        result.put( entry.getKey(), entry.getValue() );
	    }
	    return result;
	}
	
	public static HashMap<String,Double> CalcTfIdf(HashMap<String,ArrayList<Double>> Map, int n){
		
		HashMap<String,Double> Results = new HashMap<String,Double>();
		double tfidf=0,tf,idf;
		
		for(Entry<String, ArrayList<Double>> word: Map.entrySet()){ //percorre todas as palavras
			
			tfidf = 0;
			
			ArrayList<Double> frequences = word.getValue();
			idf = Math.log(frequences.get(n)/n);
			
			for(Double tfd: frequences){ //soma dos tfs-idfs
				
				 tfidf+=tfd*idf;
			}
			
			Results.put(word.getKey(), tfidf);
		}
		
		
		return sortByValue(Results);
		
	}
	
	public static HashMap<String,Double> CalcTf(HashMap<String,ArrayList<Double>> Map, int n){
		
		HashMap<String,Double> Results = new HashMap<String,Double>();
		double tf;
		
		for(Entry<String, ArrayList<Double>> word: Map.entrySet()){ //percorre todas as palavras
			
			tf = 0;
			
			ArrayList<Double> frequences = word.getValue();
			
			for(Double tfd: frequences){ //soma dos tfs-idfs
				
				 tf+=tfd;
			}
			
			Results.put(word.getKey(), tf);
		}
		
		
		return sortByValue(Results);
		
	}
	
	public static HashMap<String,ArrayList<Double>> CalcFrequence(String path, int lb, int n){
		
		HashMap<String,ArrayList<Double>> Map = new HashMap<String,ArrayList<Double>>();
		Scanner sc = null;
			
		for(int i=0;i<n;i++){
			
			try {
				int file = i+lb;
		        sc = new Scanner(new File(path+ file + ".txt"));
		    } catch (FileNotFoundException e) {
		        e.printStackTrace();  
		    }
			
			while(sc.hasNext()){
				
				String word = sc.next();
				
				if(Map.containsKey(word)){
					
					ArrayList<Double> values = Map.get(word);
					
					double conti = values.get(i);
					values.set(i, conti+1); //frequencia i++
					
					if(values.get(n+1) == 0){
						values.set(n,values.get(n)+1); //if flag =0, df++
						values.set(n+1,1.);
					}
					
					Map.replace(word, values);
					
				}
				else{
					
					ArrayList<Double> cont = new ArrayList<Double>();
					
					for(int k=0;k<n;k++){
						if(k==i){
							cont.add(1.);
						}
						else{
							cont.add(0.);
						}
					}
					cont.add(1.);
					cont.add(1.);
					
					Map.put(word, cont);
				}
				
			}
			
			for (Entry<String, ArrayList<Double>> mapentry: Map.entrySet()){
				
				ArrayList<Double> value = mapentry.getValue();
				value.set(n+1, 0.);
			}
			
		}
		
		sc.close();
		
		System.out.println("END1");
		
		return Map;
		
	}
	
	public static HashMap<String,Double> NArq(HashMap<String,ArrayList<Double>> Map, int n){
		HashMap<String,Double> Results = new HashMap<String,Double>();
		
		for(Entry<String, ArrayList<Double>> word: Map.entrySet()){
			Results.put(word.getKey(), word.getValue().get(n));
		}
		
		return Results;
	}
	
	public static HashMap<String, Double> join_maps(HashMap<String, Double> map2, HashMap<String, Double> map1){
		
		HashMap<String, Double> map_result = new HashMap<String,Double>();
		HashMap<String, Double> map_result2 = new HashMap<String,Double>();
		
		for (Entry<String, Double> mapentry: map1.entrySet()){
			map_result.put(mapentry.getKey(),mapentry.getValue());
		}
		
		for (Entry<String, Double> mapentry: map2.entrySet()){
			map_result2.put(mapentry.getKey(),mapentry.getValue());
		}
		
		for (Entry<String, Double> mapentry: map_result2.entrySet()){
			
			if(map_result.containsKey(mapentry.getKey())){
				
				double value1 = map_result.get(mapentry.getKey());
				double value2 = mapentry.getValue();
				map_result.replace(mapentry.getKey(), value1+value2);
			}
			else{
				map_result.put(mapentry.getKey(),mapentry.getValue());
			}
		}
		
		return map_result;
		
	}
	
	public static String CreateARFF(String file,String label) throws IOException{
		File logFile = new File("attributes.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
		HashMap<String,Integer> instances = new HashMap<String,Integer>();
		int[] count = new int[N_words];
		
		for(int i=0; i<N_words; i++){
			count[i]=0;
		}

		String line="";
		
		Scanner sc = null;
	    try {
	        sc = new Scanner(new File("words2.txt"));
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();  
	    }
		
	    int j=0;
	    while (sc.hasNext()) {
	    	String word = sc.next();
	    	instances.put(word, j);
	    	writer.write("@ATTRIBUTE "+word+" numeric \n");
	    	j++;
	    }
	    System.out.println(j);
		
		
	    try {
	        sc = new Scanner(new File(file));
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();  
	    }
	    
	    while (sc.hasNext()) {
            String s = sc.next();
            
            if(instances.containsKey(s))
            	count[instances.get(s)]++;
            
        }
	    
	    line = ""+count[0];
	    for(int i=1;i<N_words;i++){
	    	line=line+","+count[i];
	    }
	    
	    line=line+","+label+"\n";
		
	    writer.close();
		
		return line;
		
	}
	
	public static void diffFile(String file1, String file2, String result) throws IOException{
		File logFile = new File(result);
		BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
		HashMap<String,Double> map1 = new HashMap<String,Double>();
		HashMap<String,Double> map2 = new HashMap<String,Double>();
		
		Scanner sc = null;
		
		try {
	        sc = new Scanner(new File(file1));
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();  
	    }
		
		Scanner sc2 = null;
		
		try {
	        sc2 = new Scanner(new File(file2));
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();  
	    }
		
		while( sc.hasNextLine()){
			String word = sc.next();
			map1.put(word, sc.nextDouble());
			System.out.println(word);
		}
		while( sc2.hasNextLine()){
			String word = sc2.next();
			map2.put(word, sc2.nextDouble());
			System.out.println(word);
		}
		
		for (Entry<String, Double> mapentry: map2.entrySet()){
			
			if(map1.containsKey(mapentry.getKey())){
				
				double value1 = map1.get(mapentry.getKey());
				double value2 = mapentry.getValue();
				map1.replace(mapentry.getKey(), value1-value2);
			}
			else{
				map1.put(mapentry.getKey(),mapentry.getValue());
			}
		}
		
		map1 = sortByValue(map1);
		
		for (Entry<String, Double> mapentry: map1.entrySet()){
			System.out.println(mapentry.getKey());
			writer.write(mapentry.getKey() + "   " + mapentry.getValue() + "\n");
		}
		
		writer.close();
		
		return;
		
	}
	
	public static void main(String[] args) throws Exception {
		
		//File logFile = new File("narq_pos2.txt");
//		File logFile2 = new File("frequency.txt");
		File logFile3 = new File("instances.txt");
////		
	//	BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
//		BufferedWriter writer2 = new BufferedWriter(new FileWriter(logFile2));
		BufferedWriter writer3 = new BufferedWriter(new FileWriter(logFile3));

//		
//		HashMap<String, Double> map1 = NArq(CalcFrequence("pos1/out/",0,1000),1000);
//		HashMap<String, Double> map2 = NArq(CalcFrequence("pos1/out/",1000,1000),1000);
//		HashMap<String, Double> map3 = NArq(CalcFrequence("pos1/out/",2000,1176),1176);
//		
//		
//		HashMap<String, Double> map4 = join_maps(map1,map2);
//		System.out.println("END2");
//		map4 = sortByValue(join_maps(map4,map3));
//		
//		System.out.println("END3");
//		
//		map1 = NArq(CalcFrequence("pos2/out/",0,1000),1000);
//		map2 = NArq(CalcFrequence("pos2/out/",1000,1000),1000);
//		map3 = NArq(CalcFrequence("pos2/out/",2000,1196),1196);
//		
//		map4 = join_maps(map4,map1);
//		System.out.println("END2");
//		map4 = join_maps(map4,map2);
//		System.out.println("END2");
//		map4 = join_maps(map4,map3);
//		
//		
//		map1 = NArq(CalcFrequence("pos3/out/",0,1000),1000);
//		map2 = NArq(CalcFrequence("pos3/out/",1000,1000),1000);
//		map3 = NArq(CalcFrequence("pos3/out/",2000,1043),1043);
//		
//		map4 = join_maps(map4,map1);
//		System.out.println("END2");
//		map4 = join_maps(map4,map2);
//		System.out.println("END2");
//		map4 = join_maps(map4,map3);
//		
//		map1 = NArq(CalcFrequence("pos4/out/",0,1000),1000);
//		map2 = NArq(CalcFrequence("pos4/out/",1000,1000),1000);
//		map3 = NArq(CalcFrequence("pos4/out/",2000,1125),1125);
//		
//		map4 = join_maps(map4,map1);
//		System.out.println("END2");
//		map4 = join_maps(map4,map2);
//		System.out.println("END2");
//		map4 = join_maps(map4,map3);
//		
//		System.out.println("END");
//
//		map4=sortByValue(map4);
//		
//		for (Entry<String, Double> mapentry: map4.entrySet()){
//			writer.write("Word: " + mapentry.getKey() + " Value: " + mapentry.getValue() + "\n");
//		}
//		
		for(int i=0; i<=N_neg1_2; i++){
			writer3.write(CreateARFF("neg1/out/parsed/" + i + ".txt","N"));  
		}
		for(int i=0; i<=N_neg2_2; i++){
			writer3.write(CreateARFF("neg2/out/parsed/" + i + ".txt","N"));  
		}
		for(int i=0; i<=N_neg3_2; i++){
			writer3.write(CreateARFF("neg3/out/parsed/" + i + ".txt","N"));  
		}
		for(int i=0; i<=N_neg4_2; i++){
			writer3.write(CreateARFF("neg4/out/parsed/" + i + ".txt","N"));  
		}
		for(int i=0; i<=N_pos1_2; i++){
			writer3.write(CreateARFF("pos1/out/parsed/" + i + ".txt","P"));  
		}
		for(int i=0; i<=N_pos2_2; i++){
			writer3.write(CreateARFF("pos2/out/parsed/" + i + ".txt","P"));  
		}
		for(int i=0; i<=N_pos3_2; i++){
			writer3.write(CreateARFF("pos3/out/parsed/" + i + ".txt","P"));  
		}
		for(int i=0; i<=N_pos4_2; i++){
			writer3.write(CreateARFF("pos4/out/parsed/" + i + ".txt","P"));  
		}
	
//		writer.close();
		writer3.close();
		
	//	diffFile("frequency_pos2.txt","frequency_neg2.txt","diff_frequency2.txt");
		//diffFile("narq_pos2.txt","narq_neg2.txt","diff_narq2.txt");
		
	}
}
