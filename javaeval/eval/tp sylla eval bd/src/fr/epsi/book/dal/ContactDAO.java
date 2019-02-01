package fr.epsi.book.dal;

import fr.epsi.book.domain.Contact;

import java.sql.*;
import java.util.List;

public class ContactDAO implements IDAO<Contact, Long> {

	private static final String INSERT_QUERY = "INSERT INTO contact (idContact,idBook,nom,email,numero,type) values (?,?,?,?,?,?)";
	private static final String FIND_BY_ID_QUERY = "SELECT (idContact,nom,email,numero,type)  FROM contact WHERE id = (?)";
	private static final String FIND_ALL_QUERY = "SELECT (idContact,nom,email,numero,type)  FROM contact";
	private static final String UPDATE_QUERY = "UPDATE contact SET nom = (?), email = (?), numero = (?), type = (?) WHERE id =(?)";
	private static final String REMOVE_QUERY = "DELETE FROM contact WHERE id = (?)";

	@Override
	public void create( Contact c ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
		st.setString( 1, c.getId() );
		st.setString(2, c.getIdBook());
		st.setString( 3, c.getName() );
		st.setString( 4, c.getEmail() );
		st.setString( 5, c.getPhone() );
		st.setString( 6, c.getType().toString() );
		st.executeUpdate();
		ResultSet rs = st.getGeneratedKeys();
		if ( rs.next() ) {
			c.setId( rs.getString( 1 ) );
		}
	}
	@Override
	public Contact findById( Long aLong ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement(FIND_BY_ID_QUERY, Statement.RETURN_GENERATED_KEYS);
		st.setString(1, String.valueOf(aLong));
		ResultSet res = st.executeQuery();
		Contact contact = new Contact();
		contact.setId(res.getNString("idContact"));
		contact.setName(res.getNString("nom"));
		contact.setEmail(res.getNString("email"));
		contact.setPhone(res.getNString("numero"));
		int res_type = res.getInt("type");
			if ( res_type == 0 ) {
				contact.setType(Contact.Type.valueOf("Perso"));
			}
			else {
				contact.setType(Contact.Type.valueOf("Pro"));
			}
		return contact;
	}

	@Override
	public List<Contact> findAll() throws SQLException {
		List<Contact> Lcontact = null;
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement(FIND_ALL_QUERY, Statement.RETURN_GENERATED_KEYS);
		ResultSet resu_all = st.executeQuery();
		Contact contact = new Contact();
		while (resu_all.next()) {
			contact.setId(resu_all.getNString("idContact"));
			contact.setName(resu_all.getNString("name"));
			contact.setEmail(resu_all.getNString("email"));
			contact.setPhone(resu_all.getNString("numero"));
			int res_type = resu_all.getInt("type");
			if ( res_type == 0 ) {
				contact.setType(Contact.Type.valueOf("Perso"));
			}
			else {
				contact.setType(Contact.Type.valueOf("Pro"));
			}
			Lcontact.add(contact);
		}
		return Lcontact;
	}

	@Override
	public Contact update( Contact o ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement(UPDATE_QUERY, Statement.RETURN_GENERATED_KEYS);
		st.setString(1, o.getName());
		st.setString(2,o.getEmail());
		st.setString(3,o.getPhone());
		st.setString(4,o.getType().toString());
		st.setString(5,o.getId());
		st.executeQuery();

		Contact contact = new Contact();
		contact.setId(o.getId());
		contact.setName(o.getName());
		contact.setEmail(o.getEmail());
		contact.setPhone(o.getPhone());
		contact.setType(o.getType());
		return contact;
	}

	@Override
	public void remove( Contact o ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement(REMOVE_QUERY, Statement.RETURN_GENERATED_KEYS);
		st.setString(1, o.getId());
		st.executeUpdate();
	}
}


