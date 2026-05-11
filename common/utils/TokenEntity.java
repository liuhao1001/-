package myproject.common.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * token表
 */
@Data
public class TokenEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;

	/**
	 * 用户id
	 */
	private Long userid;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 表名
	 */
	private String tablename;

	/**
	 * 角色
	 */
	private String role;

	/**
	 * token
	 */
	private String token;

	/**
	 * 新增时间
	 */
	private Date addtime;

	private String avatarurl;
	/**
	 * 性别
	 */
	private String gender;

	/**
	 * 联系方式
	 */
	private String phone;

	private Double money;

	private String name;
}
