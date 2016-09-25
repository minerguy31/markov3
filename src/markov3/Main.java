package markov3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws Exception {
		//*/
		int[] lookaheads = new int[] {4};
		
		for(int i : lookaheads) {
			System.gc();
			
			System.out.println("Lookahead " + i);
			Random rnd = new Random();
			
			long l = System.currentTimeMillis();
			
			Dataset d = new Dataset(8);
			d.addData(new File("../output.txt"));
			System.out.println("Read data in " + (System.currentTimeMillis() - l) + " ms");
			l = System.currentTimeMillis();
			for(int j = 0; j < 50; j++) {
				System.out.println(d.getSentence(rnd));
			}
			
			System.out.println("Generated 50 responses in " + (System.currentTimeMillis() - l) + " ms\n"
					+ "Average: " + (System.currentTimeMillis() - l) / 50);
			System.gc();
			
			l = System.currentTimeMillis();
			
			String s = d.serialize();
			
			System.out.println("Done serializing");
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("../output_" + i + ".dat")));
			bw.write(s);
			bw.flush();
			bw.close();
			
			System.out.println("Serialized in " + (System.currentTimeMillis() - l) + " ms");
		}
		/*/
		
		BufferedReader bw = new BufferedReader(new FileReader(new File("output.dat")));
		System.out.println("Unserializing");
		long l = System.currentTimeMillis();
		
		Dataset d = Dataset.unserialize(bw.readLine());
		bw.close();
		System.out.println("Unserialized in " + (System.currentTimeMillis() - l) + " ms");
		
		System.out.println(d.getSentence(new Random()));
		
		//*/
		
		/*/
		PrintWriter pw = new PrintWriter(new FileOutputStream("result.txt"));
		for(int lookahead = 10; lookahead < 11; lookahead++) {
			Dataset d = new Dataset(lookahead);
			d.addData(new Scanner(new File("../output.txt")));
			pw.println("## LOOKAHEAD = "+lookahead);
			System.out.println("st "+lookahead);
			for(int i = 0; i < 64; i++)
				pw.println(d.getSentence(rnd));
			pw.flush();
			System.out.println("end "+lookahead);
		}
		
		pw.flush();
		pw.close();
		//*/
	}
}
