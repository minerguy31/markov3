package markov3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws Exception {
		Random rnd = new Random();
		
		PrintWriter pw = new PrintWriter(new FileOutputStream("result.txt"));
		for(int lookahead = 10; lookahead < 16; lookahead++) {
			Dataset d = new Dataset(lookahead);
			d.addData(new Scanner(new File("../garmy_data.txt")));
			pw.println("## LOOKAHEAD = "+lookahead);
			System.out.println("st "+lookahead);
			for(int i = 0; i < 64; i++)
				pw.println(d.getSentence(rnd));
			pw.flush();
			System.out.println("end "+lookahead);
		}
		
		pw.flush();
		pw.close();
	}
}
