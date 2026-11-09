package piles;

public class Main {
	public static void main(String[] args) {
		Pile p = new Pile(2);
		if (p.cpt == 0)
			System.out.println("La pile est vide");
		else
			System.out.println("La pile n'est pas vide");
		p.t[p.cpt++] = 5;
		System.out.println("sommet : " +p.t[p.cpt-1]);
		p.t[p.cpt++] = 7;
		System.out.println("sommet : " +p.t[p.cpt-1]);
		if (p.cpt == p.taille)
			System.out.println("La pile est pleine");
		else
			System.out.println("La pile n'est pas pleine");
		System.out.println("Dépilé : "+p.t[--p.cpt]);
		System.out.println("Dépilé : "+p.t[--p.cpt]);
		if (p.cpt == 0)
			System.out.println("La pile est vide");
		else
			System.out.println("La pile n'est pas vide");
	}
}
