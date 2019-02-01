package fr.epsi.book.domain;

import fr.epsi.book.dal.ContactDAO;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.UUID;

public class Contact implements Serializable {
	
	private String id;
	private String idBook ;
	private String name;
	private String email;
	private String phone;
	private Type type;
	
	public Contact() {
		id = UUID.randomUUID().toString();
	}
	
	public String getId() {
		return id;
	}
	
	public void setId( String id ) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail( String email ) {
		this.email = email;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone( String phone ) {
		this.phone = phone;
	}

	public Type getType() {
		return type;
	}
	
	public void setType( Type type ) {
		this.type = type;
	}

	public String getIdBook() {
		return idBook;
	}

	public void setIdBook(String idBook) {
		this.idBook = idBook;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder( "Contact{" );
		sb.append( "id='" ).append( id ).append( '\'' );
		sb.append( ", name='" ).append( name ).append( '\'' );
		sb.append( ", email='" ).append( email ).append( '\'' );
		sb.append( ", phone='" ).append( phone ).append( '\'' );
		sb.append( ", type=" ).append( type );
		sb.append( '}' );
		return sb.toString();
	}
	
	public enum Type {
		PERSO( "Perso" ), PRO( "Pro" );
		
		private String value;
		
		Type( String value ) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public void createContact (Contact o) throws SQLException {
		ContactDAO contact = new ContactDAO();
		contact.create(o);
	}

	public void listcontact () {

	}
}


