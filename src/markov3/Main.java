package markov3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;


/////////////////////////////////////////////////////////////////////////////////////////////////////////////
// This main class is just for testing. Nothing is here is supposed to work or be remotely understandable. //
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class Main {
	public static void main(String[] args) throws Exception {
		
		Random rnd = new Random();
		
		long l = System.currentTimeMillis();
		
		Dataset d = new Dataset();
		d.addData(new File("../output.txt"));
		System.out.println("Read data in " + (System.currentTimeMillis() - l) + " ms");
		l = System.currentTimeMillis();
		for(int i = 0;i < 50; i++) {
			System.out.println(d.getSentence(rnd));
		}
		
		System.out.println("Generated 50 responses in " + (System.currentTimeMillis() - l) + " ms\n"
				+ "Average: " + (System.currentTimeMillis() - l) / 50);
		
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
