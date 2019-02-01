package fr.epsi.book;

import fr.epsi.book.dal.BookDAO;
import fr.epsi.book.domain.Book;
import fr.epsi.book.domain.Contact;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.FileWriter;

public class App {
	
	private static final String BOOK_BKP_DIR = "./resources/backup/";
	
	private static final Scanner sc = new Scanner( System.in );
	private static Book book = new Book();
	
	public static void main( String... args ) throws SQLException {
		dspMainMenu();
	}
	
	public static Contact.Type getTypeFromKeyboard() {
		int response;
		boolean first = true;
		do {
			if ( !first ) {
				System.out.println( "***********************************************" );
				System.out.println( "* Mauvais choix, merci de recommencer !       *" );
				System.out.println( "***********************************************" );
			}
			System.out.println( "*******Choix type de contact *******" );
			System.out.println( "* 1 - Pero                         *" );
			System.out.println( "* 2 - Pro                          *" );
			System.out.println( "************************************" );
			System.out.print( "*Votre choix : " );
			try {
				response = sc.nextInt() - 1;
			} catch ( InputMismatchException e ) {
				response = -1;
			} finally {
				sc.nextLine();
			}
			first = false;
		} while ( 0 != response && 1 != response );
		return Contact.Type.values()[response];
	}
	
	public static void addContact(String idbook) throws SQLException {
		Scanner add = new Scanner( System.in );
		System.out.println( "**************************************" );
		System.out.println( "**********Ajout d'un contact**********" );
		Contact contact = new Contact();
		System.out.print( "Entrer le nom :" );
		contact.setName( add.nextLine() );
		System.out.print( "Entrer l'email :" );
		contact.setEmail( add.nextLine() );
		System.out.print( "Entrer le téléphone :" );
		contact.setPhone( add.nextLine() );
		contact.setType(getTypeFromKeyboard());
		contact.setIdBook(idbook);
		contact.createContact(contact);
		System.out.println( "Nouveau contact ajouté ..." );
	}
	
	public static void editContact() {
		System.out.println( "*********************************************" );
		System.out.println( "**********Modification d'un contact**********" );
		dspContacts( false );
		System.out.print( "Entrer l'identifiant du contact : " );
		String id = sc.nextLine();
		Contact contact = book.getContacts().get( id );
		if ( null == contact ) {
			System.out.println( "Aucun contact trouvé avec cet identifiant ..." );
		} else {
			System.out
					.print( "Entrer le nom ('" + contact.getName() + "'; laisser vide pour ne pas mettre à jour) : " );
			String name = sc.nextLine();
			if ( !name.isEmpty() ) {
				contact.setName( name );
			}
			System.out.print( "Entrer l'email ('" + contact
					.getEmail() + "'; laisser vide pour ne pas mettre à jour) : " );
			String email = sc.nextLine();
			if ( !email.isEmpty() ) {
				contact.setEmail( email );
			}
			System.out.print( "Entrer le téléphone ('" + contact
					.getPhone() + "'; laisser vide pour ne pas mettre à jour) : " );
			String phone = sc.nextLine();
			if ( !phone.isEmpty() ) {
				contact.setPhone( phone );
			}
			System.out.println( "Le contact a bien été modifié ..." );
		}
	}
	
	public static void deleteContact() {
		System.out.println( "*********************************************" );
		System.out.println( "***********Suppression d'un contact**********" );
		dspContacts( false );
		System.out.print( "Entrer l'identifiant du contact : " );
		String id = sc.nextLine();
		Contact contact = book.getContacts().remove( id );
		if ( null == contact ) {
			System.out.println( "Aucun contact trouvé avec cet identifiant ..." );
		} else {
			System.out.println( "Le contact a bien été supprimé ..." );
		}
	}
	
	public static void sort() {
		int response;
		boolean first = true;
		do {
			if ( !first ) {
				System.out.println( "***********************************************" );
				System.out.println( "* Mauvais choix, merci de recommencer !       *" );
				System.out.println( "***********************************************" );
			}
			System.out.println( "*******Choix du critère*******" );
			System.out.println( "* 1 - Nom     **              *" );
			System.out.println( "* 2 - Email **                *" );
			System.out.println( "*******************************" );
			System.out.print( "*Votre choix : " );
			try {
				response = sc.nextInt();
			} catch ( InputMismatchException e ) {
				response = -1;
			} finally {
				sc.nextLine();
			}
			first = false;
		} while ( 0 >= response || response > 2 );
		Map<String, Contact> contacts = book.getContacts();
		switch ( response ) {
			case 1:
				contacts.entrySet().stream()
						.sorted( ( e1, e2 ) -> e1.getValue().getName().compareToIgnoreCase( e2.getValue().getName() ) )
						.forEach( e -> dspContact( e.getValue() ) );
				break;
			case 2:
				
				contacts.entrySet().stream().sorted( ( e1, e2 ) -> e1.getValue().getEmail()
																	 .compareToIgnoreCase( e2.getValue().getEmail() ) )
						.forEach( e -> dspContact( e.getValue() ) );
				break;
		}
	}
	
	public static void searchContactsByName() {
		
		System.out.println( "*******************************************************************" );
		System.out.println( "************Recherche de contacts sur le nom ou l'email************" );
		System.out.println( "*******************************************************************" );
		System.out.print( "*Mot clé (1 seul) : " );
		String word = sc.nextLine();
		Map<String, Contact> subSet = book.getContacts().entrySet().stream()
										  .filter( entry -> entry.getValue().getName().contains( word ) || entry
												  .getValue().getEmail().contains( word ) )
										  .collect( HashMap::new, ( newMap, entry ) -> newMap
												  .put( entry.getKey(), entry.getValue() ), Map::putAll );
		
		if ( subSet.size() > 0 ) {
			System.out.println( subSet.size() + " contact(s) trouvé(s) : " );
			subSet.entrySet().forEach( entry -> dspContact( entry.getValue() ) );
		} else {
			System.out.println( "Aucun contact trouvé avec cet identifiant ..." );
		}
	}
	
	public static void dspContact( Contact contact ) {
		System.out.println( contact.getId() + "\t\t\t\t" + contact.getName() + "\t\t\t\t" + contact
				.getEmail() + "\t\t\t\t" + contact.getPhone() + "\t\t\t\t" + contact.getType() );
	}
	
	public static void dspContacts( boolean dspHeader ) {
		if ( dspHeader ) {
			System.out.println( "**************************************" );
			System.out.println( "********Liste de vos contacts*********" );
		}
		for ( Map.Entry<String, Contact> entry : book.getContacts().entrySet() ) {
			dspContact( entry.getValue() );
		}
		System.out.println( "**************************************" );
	}
	
	public static void dspMainMenu() throws SQLException {
		int response;
		boolean first = true;
		do {
			if ( !first ) {
				System.out.println( "***********************************************" );
				System.out.println( "* Mauvais choix, merci de recommencer !       *" );
				System.out.println( "***********************************************" );
			}
			System.out.println( "**************************************" );
			System.out.println( "******Gestion carnet d'adresse********" );
			System.out.println( "* 1 - Création d'un book             *" );
			System.out.println( "* 2 - afficher les tous les book     *" );
			System.out.println( "* 3 - Modifier un book               *" );
			System.out.println( "* 4 - supprimé un book               *" );
			System.out.println( "* 5 - Editer un book                 *" );
			System.out.println( "* 6 - Quitter			              *" );
			System.out.println( "**************************************" );
			System.out.print( "*Votre choix : " );
			try {
				response = sc.nextInt();
			} catch ( InputMismatchException e ) {
				response = -1;
			} finally {
				sc.nextLine();
			}
			first = false;
		} while ( 1 > response || 6 < response );
		switch ( response ) {
			case 1:
				createBook();
				dspMainMenu();
				break;
			case 2:
				listBook();
				dspMainMenu();
				break;
			case 3:
				editBook();
				MenuContact();
				break;
			case 4:
				removeBook();
				dspMainMenu();
				break;
			case 5:
				MenuContact();
				break;
			case 6:
				System.exit(1);
				break;
		}
	}

	public static void MenuContact() throws SQLException {
		// selection du book
		System.out.println( "********Book diponible**************" );
		int f = 0;
		Vector <Book> test = new Vector();
		BookDAO bookdao = new BookDAO();
		List<Book> result = bookdao.findAll();
		for (Book  book : result) {
			test.add(f,book);
			System.out.println(f + " : " + book.getCode());
			f++;
		}
		System.out.print("Votre choix : ");
		int choixbook = sc.nextInt();
		String bookmodif = result.get(choixbook).getCode();
		System.out.println( "**************************************" );
		System.out.println( "*Gestion contact du book "+bookmodif+"" );
		System.out.println( "* 1 - Ajouté un contact	  *" );
		System.out.println( "* 2 - Supprimé un contact (Par ID)	  *" );
		System.out.println( "* 3 - rechercher contacts (Par ID)   *" );
		System.out.println( "* 4 - afficher tous les contacts     *" );
		System.out.println( "* 5 - Menu carnet d'adresse          *" );
		System.out.println( "* 6 - Quitter			              *" );
		System.out.println( "**************************************" );
		System.out.print( "*Votre choix : " );
		int choix = sc.nextInt();
		switch (choix){
			case 1:
				addContact(result.get(choixbook).getId());
				MenuContact();
				break;
			case 2:

				break;
			case 3:

				break;
			case 4:

				break;
			case 5:

				break;
			case 6:
				System.exit(1);
				break;
		}
	}

	public static void createBook () throws SQLException {
		System.out.print("Nom de book souhaité : ");
		String code = sc.nextLine();
		Book cbook = new Book();
		cbook.setCode(code);
		BookDAO bookdao = new BookDAO();
		bookdao.create(cbook);
	}

	public static void listBook () throws SQLException {
		System.out.println( "**************************************" );
		System.out.println( "********Liste des books **************" );
		BookDAO bookdao = new BookDAO();
		List<Book> result = bookdao.findAll();
			for (Book  book : result) {
				System.out.println("ID du book : "+book.getId());
				System.out.println("Nom du book : "+book.getCode());
			}
		System.out.println( "**************************************" );
	}

	public static void editBook () throws SQLException {
		System.out.println( "********Liste des books **************" );
		int f = 0;
		Vector <Book> test = new Vector();
		BookDAO bookdao = new BookDAO();
		List<Book> result = bookdao.findAll();
		for (Book  book : result) {
			test.add(f,book);
			System.out.println(f + " : " + book.getCode());
			f++;
		}
		System.out.print("Votre choix : ");
		int choix = sc.nextInt();
		System.out.print("Nouveau nom : ");
		Scanner nv = new Scanner( System.in );
		String nom = nv.nextLine();
		test.get(choix).setCode(nom);
		bookdao.update(test.get(choix));
	}

	public static void removeBook () throws SQLException {
		System.out.println( "********Liste des books **************" );
		int f = 0;
		Vector <Book> test = new Vector();
		BookDAO bookdao = new BookDAO();
		List<Book> result = bookdao.findAll();
		for (Book  book : result) {
			test.add(f,book);
			System.out.println(f + " : " + book.getCode());
			f++;
		}
		System.out.print("Votre choix : ");
		int choix = sc.nextInt();
		bookdao.remove(test.get(choix));
	}
}
