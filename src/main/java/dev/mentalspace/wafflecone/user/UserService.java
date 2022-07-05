package dev.mentalspace.wafflecone.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class UserService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public User getById(long id) {
		String sql =
			"SELECT type, username, email, email_verified, password, school_id, teacher_id, student_id FROM user WHERE user_id = ?;";
		RowMapper<User> rowMapper = new UserRowMapper();
		User user = jdbcTemplate.queryForObject(sql, rowMapper, id);
		return user;
	}

	public User getByStudentId(long id) {
		String sql =
			"SELECT user_id, type, username, email, email_verified, password, school_id, teacher_id FROM user WHERE student_id = ?;";
		RowMapper<User> rowMapper = new UserRowMapper();
		User user = jdbcTemplate.queryForObject(sql, rowMapper, id);
		return user;
	}

	public User getByTeacherId(long id) {
		String sql =
			"SELECT user_id, type, username, email, email_verified, password, school_id, student_id FROM user WHERE teacher_id = ?;";
		RowMapper<User> rowMapper = new UserRowMapper();
		User user = jdbcTemplate.queryForObject(sql, rowMapper, id);
		return user;
	}

	public boolean existsByUsername(String username) {
		String sql = "SELECT COUNT(*) FROM user WHERE username = ?;";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, username);
		return count != 0;
	}

	public boolean existsByEmail(String email) {
		String sql = "SELECT COUNT(*) FROM user WHERE email = ?;";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, email);
		return count != 0;
	}	

	public void add(User user) {
		String sql =
			"INSERT INTO user (type, username, email, email_verified, password)"
				+ " VALUES (?, ?, ?, ?, ?);";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		jdbcTemplate.update(
			new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps =
						connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setInt(1, user.type);
					ps.setString(2, user.username);
					ps.setString(3, user.email);
					ps.setBoolean(4, user.emailVerified);
					ps.setString(5, user.password);
					// ps.setLong(6, user.schoolId);
					// ps.setLong(7, user.teacherId);
					// ps.setLong(8, user.studentId);
					return ps;
				}
		},  keyHolder);

		user.userId = keyHolder.getKey().longValue();
	}
}
