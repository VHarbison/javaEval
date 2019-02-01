package fr.epsi.book.dal;

import fr.epsi.book.domain.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO implements IDAO<Book, Long> {

	private static final String INSERT_QUERY = "INSERT INTO book (idBook,code) values (?,?)";
	private static final String FIND_BY_ID_QUERY = "SELECT idBook,code  FROM book WHERE id = (?)";
	private static final String FIND_ALL_QUERY = "SELECT idBook,code  FROM book";
	private static final String UPDATE_QUERY = "UPDATE book SET code = (?) WHERE idBook =(?)";
	private static final String REMOVE_QUERY = "DELETE FROM book WHERE idBook = (?)";
	
	@Override
	public void create( Book o ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
		st.setString(1,o.getId());
		st.setString(2,o.getCode());
		st.executeUpdate();
	}
	
	@Override
	public Book findById( Long aLong ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		Statement st = connection.createStatement();
		st.executeQuery(FIND_BY_ID_QUERY);
		ResultSet res = st.getResultSet();
		Book book = new Book();
		book.setId(res.getString("idBook"));
		book.setCode(res.getString("code"));
		return book;
	}
	
	@Override
	public List<Book> findAll() throws SQLException {
		List<Book> Lbook = new ArrayList<>();
		Connection connection = PersistenceManager.getConnection();
		Statement st = connection.createStatement();
		st.executeQuery(FIND_ALL_QUERY);
		ResultSet res = st.getResultSet();
		Book book = new Book();
		while (res.next()) {
			Book books = new Book();
			books.setId(res.getString("idBook"));
			books.setCode(res.getString("code"));
			Lbook.add(books);
		}
		return Lbook;
	}
	
	@Override
	public Book update( Book o ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( UPDATE_QUERY, Statement.RETURN_GENERATED_KEYS);
		st.setString(1,o.getCode());
		st.setString(2,o.getId());
		st.executeUpdate();

		Book book = new Book();
		book.setId(o.getId());
		book.setCode(o.getCode());
		return book;
	}
	
	@Override
	public void remove( Book o ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( REMOVE_QUERY, Statement.RETURN_GENERATED_KEYS);
		st.setString(1,o.getId());
		st.executeUpdate();
	}
}
