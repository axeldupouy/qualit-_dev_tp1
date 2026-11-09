package refuge;

import java.util.Scanner;

public class Refuge {
	private  Chien[] chiens = new Chien[5];
	private  int nbChiens = 0;
	private  Chat[] chats = new Chat[10];
	private  int nbChats = 0;
	private  Scanner sc;

	public void session() {
		sc = new Scanner(System.in);
		String choix = "oui";
		while (choix.equals("oui")) {
			System.out.println("Voulez-vous ajouter un animal ?");
			while (true) {
				choix = sc.nextLine();
				 if (choix.equals("oui") || choix.equals("non"))
					 break;
				 else System.out.println("oui ou non ?");
			}
			if (choix.equals("oui"))
				ajouterAnimal();
		}
		lesAnimauxCrient();
		nourrirLesAnimaux();
	}
	public void nourrirLesAnimaux() {
		for (int i =0; i< nbChiens; ++i)
			System.out.println(chiens[i].getNom() + " recoit de la viande");
		for (int i =0; i< nbChats; ++i)
			System.out.println(chats[i].getNom() + " recoit du lait");
	}
	public void ajouterAnimal() {
		System.out.println("chien ou chat ?");
		String espece;
		while (true) {
			espece = sc.nextLine();
			 if (espece.equals("chien") || espece.equals("chat"))
				 break;
			 else System.out.println("Entrez chien ou chat uniquement\n");
		}
		if ((espece.equals("chien") && nbChiens ==5 )||
				(espece.equals("chat") && nbChats ==10 )) {
			System.out.println("Il n'y a plus de place pour cette espece.");
			return;
		}
		System.out.println("Entrez le nom de l'animal : ");
		String nom = sc.nextLine();
		if (espece.equals("chien"))
			chiens[nbChiens++]= new Chien(nom);
		else
			chats[nbChats++] = new Chat(nom);
	}

	public void lesAnimauxCrient() {
		for (int i =0; i< nbChiens; ++i)
			System.out.println(chiens[i].getNom() + " aboie");
		for (int i =0; i< nbChats; ++i)
			System.out.println(chats[i].getNom() + " miaule");
	}
}
