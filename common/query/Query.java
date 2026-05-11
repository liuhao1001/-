package myproject.common.query;

import lombok.Data;

/**
 * 查询公共参数
 */
@Data
public class Query {

	Integer page;

	Integer limit;

	String order;

	boolean asc;
}