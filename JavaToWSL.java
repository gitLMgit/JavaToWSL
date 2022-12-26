import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class JavaToWSL {
	
	
	public static void main(String[] args) throws IOException {
		
		LinkedList<String> ugnjezdeni = new LinkedList<String>(); 
		
		List<String> tipovi = new ArrayList<String>();
		tipovi.add("byte");
		tipovi.add("short");
		tipovi.add("int");
		tipovi.add("long");
		tipovi.add("float");
		tipovi.add("double");
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/fajlovi/proba"));
			String linija;
			String rezultat = "";
			
			/* kada imam 'if' pa 'while' na steku, onda ce, bez ovog flag-a
			   program da prodje i kroz zatvaranje 'if' i kroz zatvaranje 'while' */
			boolean zatvorenIfWhile = false;

			while((linija = br.readLine()) != null) {
				
				zatvorenIfWhile = false;
				// skidamo visak razmaka sa pocetka i kraja
				linija = linija.trim();
				// splitujemo po razmaku sve
				String[] split = linija.split(" ");
				
				
				// ovde prevodimo klasicne aritmeticke izraze
				// po pravilu lepog kodiranja, racunamo da su simboli odvojeni razmakom (separator)
				// za sada, samo inicijalizacija i proste binarne operacije
				// drugi uslov je aritmeticka operacija nad promenljivom koja je vec deklarisana
				// spli.length != 2 preskace slucajeve kada imamo deklaraciju promenljive, tipa int x;
				if((tipovi.contains(split[0].trim()) && split.length != 2) 
						|| (split.length >= 3 && split[1].compareTo("=") == 0)) {
					String razmak = "";
					if (!ugnjezdeni.isEmpty()) {
						char[] razmaci = new char[2*ugnjezdeni.size()];
						// ovaj metod mi popunjava niz zeljenim elementima, tj. razmacima
						Arrays.fill(razmaci, ' ');
						for(int i = 0; i < razmaci.length; i++) {
							razmak += razmaci[i];
						}
						rezultat += razmak;
					}
					// u ovom slucaju imamo bar inicijalizaciju, a ne prostu deklaraciju
					int j;
					if(split.length >= 4 && tipovi.contains(split[0].trim())) {
						// ovde zelim da resim udvajanje
						// za svaki nivo ugnjezdenosti dodajem po 2 razmaka
						j = 1;
					} else {
						j = 0; 
					}
					for(int i = j; i < split.length; i++) {
						String element = split[i];
						if(split[i].compareTo("%") == 0)
							element = " MOD ";
						if(split[i].compareTo("=") == 0)
							element = " := ";
						rezultat += element; 
					}
					rezultat += "\n";
				}
				
				// operatori za inkrementaciju i dekrementaciju
				if(split.length == 1 && split[0].length() == 4) {
					String razmak = "";
					if (!ugnjezdeni.isEmpty()) {
						char[] razmaci = new char[2*ugnjezdeni.size()];
						// ovaj metod mi popunjava niz zeljenim elementima, tj. razmacima
						Arrays.fill(razmaci, ' ');
						for(int i = 0; i < razmaci.length; i++) {
							razmak += razmaci[i];
						}
						rezultat += razmak;
					}
					String incDec = "";
					
					// prefiksni inkrement
					if(split[0].charAt(0) == '+' && split[0].charAt(1) == '+') {
						incDec += split[0].charAt(2) + " := " + split[0].charAt(2) + " + 1\n ";
					} else if (split[0].charAt(1) == '+' && split[0].charAt(2) == '+') {
					// postfiksni inkrement
						incDec += split[0].charAt(0) + " := " + split[0].charAt(0) + " + 1\n ";	
					}
					
					// prefiksni dekrement
					if(split[0].charAt(0) == '-' && split[0].charAt(1) == '-') {
						incDec += split[0].charAt(2) + " := " + split[0].charAt(2) + " - 1\n ";
					} else if (split[0].charAt(1) == '-' && split[0].charAt(2) == '-') {
					// postfiksni dekrement
						incDec += split[0].charAt(0) + " := " + split[0].charAt(0) + " - 1\n ";	
					}

					rezultat += razmak + incDec;
				}
				
				// ovde obradjujemo IF
				if(split[0].compareTo("if") == 0) {
					String[] split1 = linija.split(" ");
					String uslovi = "";
					// split1[0] = if, split1[....] = uslovi, split1[split1.length-1] = {
					for(int i = 1; i < split1.length - 2; i++) {
						uslovi += split1[i] + " ";
					}
					uslovi += split1[split1.length - 2];

					// za svaki nivo ugnjezdenosti dodajem po 2 razmaka
					String razmak = "";
					if (!ugnjezdeni.isEmpty()) {
						char[] razmaci = new char[2*ugnjezdeni.size()];
						// ovaj metod mi popunjava niz zeljenim elementima, tj. razmacima
						Arrays.fill(razmaci, ' ');
						for(int i = 0; i < razmaci.length; i++) {
							razmak += razmaci[i];
						}
						rezultat += razmak;
					}
					rezultat += "IF " + obradiUslov(uslovi) + " THEN\n";
					ugnjezdeni.addLast("if");
				}
				
				// ELSE IF deo
				if(split.length > 4 && split[1].compareTo("else") == 0 &&
						split[2].compareTo("if") == 0) {
					String[] split1 = linija.split(" ");
					String uslovi = "";
					// split1[1] = else, split1[2] = if, split1[....] = uslovi, split1[split1.length-1] = {
					for(int i = 3; i < split1.length - 2; i++) {
						uslovi += split1[i] + " ";
					}
					uslovi += split1[split1.length - 2];
					
					rezultat = String.copyValueOf(rezultat.toCharArray(), 0, rezultat.length()-2);
					rezultat += "\n";
					// za svaki nivo ugnjezdenosti dodajem po 2 razmaka
					String razmak = "";
					if (!ugnjezdeni.isEmpty()) {
						char[] razmaci = new char[2*ugnjezdeni.size()-2];
						// ovaj metod mi popunjava niz zeljenim elementima, tj. razmacima
						Arrays.fill(razmaci, ' ');
						for(int i = 0; i < razmaci.length; i++) {
							razmak += razmaci[i];
						}
						rezultat += razmak;
					}
					rezultat += "ELSE IF " + obradiUslov(uslovi) + " THEN\n";
				}
				
				// ELSE deo
				if(split.length == 3 && split[1].compareTo("else") == 0) {
					rezultat = String.copyValueOf(rezultat.toCharArray(), 0, rezultat.length()-2);
					rezultat += "\n";
					// za svaki nivo ugnjezdenosti dodajem po 2 razmaka
					String razmak = "";
					if (!ugnjezdeni.isEmpty()) {
						char[] razmaci = new char[2*ugnjezdeni.size()-2];
						// ovaj metod mi popunjava niz zeljenim elementima, tj. razmacima
						Arrays.fill(razmaci, ' ');
						for(int i = 0; i < razmaci.length; i++) {
							razmak += razmaci[i];
						}
						rezultat += razmak;
					}
					rezultat += "ELSE\n";
				}
				
				// zatvaramo IF
				if(split.length == 1 && split[0].compareTo("}") == 0 && !ugnjezdeni.isEmpty() 
						&& ugnjezdeni.getLast().compareTo("if") == 0 && !zatvorenIfWhile) {
					rezultat = String.copyValueOf(rezultat.toCharArray(), 0, rezultat.length()-2);
		
					// za svaki nivo ugnjezdenosti dodajem po 2 razmaka
					String razmak = "";
					if (!ugnjezdeni.isEmpty()) {
						char[] razmaci = new char[2*ugnjezdeni.size()-2];
						// ovaj metod mi popunjava niz zeljenim elementima, tj. razmacima
						Arrays.fill(razmaci, ' ');
						for(int i = 0; i < razmaci.length; i++) {
							razmak += razmaci[i];
						}
					}
					rezultat += "\n" + razmak + "FI;\n";
					zatvorenIfWhile = true;
					ugnjezdeni.removeLast();
				}
				
				// ovde obradjujemo WHILE
				if(split[0].compareTo("while") == 0) {
					String[] split1 = linija.split(" ");
					String uslovi = "";
					// split1[0] = while, split1[....] = uslovi, split1[split1.length-1] = {
					for(int i = 1; i < split1.length - 2; i++) {
						uslovi += split1[i] + " ";
					}
					uslovi += split1[split1.length - 2];
					// ovde zelim da resim udvajanje
					// za svaki nivo ugnjezdenosti dodajem po 2 razmaka
					if (!ugnjezdeni.isEmpty()) {
						char[] razmaci = new char[2*ugnjezdeni.size()];
						// ovaj metod mi popunjava niz zeljenim elementima, tj. razmacima
						Arrays.fill(razmaci, ' ');
						String razmak = "";
						for(int i = 0; i < razmaci.length; i++) {
							razmak += razmaci[i];
						}
						rezultat += razmak;
					}
					rezultat += "WHILE " + obradiUslov(uslovi) + " DO\n";
					ugnjezdeni.addLast("while");
				}
				
				
				if(split.length == 1 && split[0].compareTo("}") == 0 && !ugnjezdeni.isEmpty() 
						&& ugnjezdeni.getLast().compareTo("while") == 0 && !zatvorenIfWhile) {
					rezultat = String.copyValueOf(rezultat.toCharArray(), 0, rezultat.length()-2);
					// ovde zelim da resim udvajanje
					// za svaki nivo ugnjezdenosti dodajem po 2 razmaka
					String razmak = "";
					if (!ugnjezdeni.isEmpty()) {
						char[] razmaci = new char[2*ugnjezdeni.size()-2];
						// ovaj metod mi popunjava niz zeljenim elementima, tj. razmacima
						Arrays.fill(razmaci, ' ');
						for(int i = 0; i < razmaci.length; i++) {
							razmak += razmaci[i];
						}
					}
					rezultat += "\n" + razmak + "OD;\n";
					zatvorenIfWhile = true;
					ugnjezdeni.removeLast();
				}
				
				// ovde radimo prepoznavanje ispisa
				if(split[0].length() >= 18 && String.valueOf(split[0].toCharArray(), 0, 18).compareTo("System.out.println") == 0) {
					// split1[1] uzima vrednost ispisa u zagradi
					// split1 ima 0: 'System.out...', 1: ispis u zagradi, 2: ';'
					String[] split1 = linija.split("[()]");
					
					// ovde razlomimo ispis da vidimo ima li konkatenacije
					String[] konkatenacija = split1[1].split("\\+");
					String ispis = "";
					// ovaj uslov proverava da li je ispis iz jednog dela ili je bilo konkatenacije
					if(konkatenacija.length > 1) {
						for(int i = 0; i < konkatenacija.length-1; i++) {
							ispis += konkatenacija[i] + "++";
						}
						ispis += konkatenacija[konkatenacija.length-1];
					} else {
						// ako nije bilo konkatenacije, onda je ispis iz jednog dela
						ispis = konkatenacija[0];
					}
					
					String razmak = "";
					if (!ugnjezdeni.isEmpty()) {
						char[] razmaci = new char[2*ugnjezdeni.size()];
						// ovaj metod mi popunjava niz zeljenim elementima, tj. razmacima
						Arrays.fill(razmaci, ' ');
						for(int i = 0; i < razmaci.length; i++) {
							razmak += razmaci[i];
						}
					}
					rezultat += razmak + "PRINT(" + ispis + ");\n";
				}
				
			}

			String rezultat1 = String.copyValueOf(rezultat.toCharArray(), 0, rezultat.length()-2);
			// poslednja linija programa mora da terminira '\n' novom linijom
			System.out.println(rezultat1 + "\n");
		} catch (FileNotFoundException e) {
			System.out.println("Greska prilikom citanja iz fajla");
		}
	}
	
	// za pocetak prosti uslovi sa binarnim, relacionim operatorima
	private static String obradiUslov(String string) {
		//lista sa relacionim operatorima
		List<String> relacioni = new ArrayList<String>();
		relacioni.add("<");
		relacioni.add("<=");
		relacioni.add(">=");
		relacioni.add(">");
		relacioni.add("==");
		relacioni.add("!=");

		List<String> logicki = new ArrayList<String>();
		logicki.add("&&");
		logicki.add("&");
		logicki.add("||");
		logicki.add("|");
		
		String[] split = string.split(" ");
		
		for(int i = 1; i < split.length; i+=2) {
			if(relacioni.contains(split[i])) {
				if (split[i].compareTo("==") == 0) {
					split[i] = "=";
				} else if (split[i].compareTo("!=") == 0) {
					split[i] = "<>";
				} 
			} else if (logicki.contains(split[i])) {
				if (split[i].compareTo("&&") == 0 || split[i].compareTo("&") == 0) {
					split[i] = "AND";
				} else if (split[i].compareTo("||") == 0 || split[i].compareTo("|") == 0) {
					split[i] = "OR";
				}
			}
		}
		
		String rezultat = "";
		for(int i = 0; i < split.length; i++) {
			rezultat += split[i];
			// za lepsi ispis. razmaci izmedju simbola
			if(i != split.length - 1) {
				rezultat += " ";
			}
		}
		return rezultat;
	}

	
}
